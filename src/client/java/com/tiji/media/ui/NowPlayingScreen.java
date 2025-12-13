package com.tiji.media.ui;

import com.tiji.media.MediaClient;
import com.tiji.media.api.ApiCalls;
import com.tiji.media.util.ImageWithColor;
import com.tiji.media.util.RepeatMode;
import com.tiji.media.util.TextTranter;
import com.tiji.media.widgets.BorderlessButtonWidget;
import com.tiji.media.widgets.ProgressWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class NowPlayingScreen extends BaseScreen {
    private static final int MARGIN = 10;
    private static final int IMAGE_SIZE = 70;

    private static final int PLAYBACK_CONTROL_Y = 50;
    private static final int TITLE_Y = 8;
    private static final int ARTIST_Y = 23;
    private static final int PLAYBACK_SIZE = 200;
    private static final int INFO_TEXT_SIZE = PLAYBACK_SIZE - MARGIN*2 - IMAGE_SIZE;

    private BorderlessButtonWidget playPauseButton;
    private ProgressWidget progressBar;
    private BorderlessButtonWidget repeatButton;
    private BorderlessButtonWidget shuffleButton;

    private boolean isFirstInit = true;

    private static final Map<Text, Class<? extends SecondaryBaseScreen>> SUBSCREENS = Map.of(
            subscreenText(Icons.SEARCH, "ui.media.subscreens.search"), SearchScreen.class
    );
    private static final int SUBSCREEN_BUTTONS_HEIGHT = 16;
    private static Text subscreenText(Text Icon, String description) {
        return Icon.copy()
                .append(" ")
                .append(Text.translatable(description)
                        .setStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT_ID)));
    }

    public NowPlayingScreen() {
        super(true);
    }

    @Override
    protected void init() {
        super.init();

        MinecraftClient client = MinecraftClient.getInstance();

        if (isFirstInit) {
            GLFW.glfwSetCursorPos(client.getWindow().getHandle(), 0, 0);
                    //IMAGE_SIZE + borderlessButtonWidget.BUTTON_SIZE * 2 * client.options.getGuiScale().getValue(),
                    //PLAYBACK_CONTROL_Y * client.options.getGuiScale().getValue());
            isFirstInit = false;
        }

        // Buttons
        int x = MARGIN *2 + widgetsOffset + IMAGE_SIZE;
        int y = MARGIN + PLAYBACK_CONTROL_Y;

        shuffleButton = new BorderlessButtonWidget(
                Icons.SHUFFLE,
                x, y,
                () -> ApiCalls.setShuffle(!MediaClient.shuffle),
                true
        );
        addDrawableChild(shuffleButton); // Shuffle

        x += BorderlessButtonWidget.BUTTON_SIZE + 1;
        addDrawableChild(
                new BorderlessButtonWidget(
                        Icons.PREVIOUS,
                        x, y,
                        ApiCalls::previousTrack,
                        true
                )
        ); // Previous

        x += BorderlessButtonWidget.BUTTON_SIZE + 1;
        playPauseButton = new BorderlessButtonWidget(
                MediaClient.isPlaying ? Icons.PAUSE : Icons.RESUME,
                x, y,
                () -> ApiCalls.playPause(!MediaClient.isPlaying),
                true
        );
        addDrawableChild(playPauseButton);

        x += BorderlessButtonWidget.BUTTON_SIZE + 1;
        addDrawableChild(
                new BorderlessButtonWidget(
                        Icons.NEXT,
                        x, y,
                        ApiCalls::nextTrack,
                        true
                )
        ); // Next

        x += BorderlessButtonWidget.BUTTON_SIZE + 1;
        repeatButton = new BorderlessButtonWidget(
                Icons.REPEAT,
                x, y,
                () -> ApiCalls.setRepeat(RepeatMode.getNextMode(MediaClient.repeat)),
                true
        );
        addDrawableChild(repeatButton); // Repeat

        // Progress bar
        progressBar = new ProgressWidget(
                MARGIN + widgetsOffset, (int) (MARGIN * 1.5 + IMAGE_SIZE), PLAYBACK_SIZE,
                (float) MediaClient.progressValue,
                (v) -> ApiCalls.setPlaybackLoc((int) (MediaClient.currentlyPlaying.duration * v))
        );
        addDrawableChild(progressBar);

        // Subscreen button
        y = height - MARGIN - SUBSCREEN_BUTTONS_HEIGHT;
        for (Map.Entry<Text, Class<? extends SecondaryBaseScreen>> entry : SUBSCREENS.entrySet()) {
            addDrawableChild(
                    new BorderlessButtonWidget(
                            entry.getKey(),
                            MARGIN + widgetsOffset, y,
                            () -> {
                                try {
                                    SecondaryBaseScreen screen = entry.getValue().getDeclaredConstructor().newInstance();
                                    client.setScreen(screen);
                                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                                         InvocationTargetException ignored) {}
                            }, false
                    )
            );
            y -= SUBSCREEN_BUTTONS_HEIGHT;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // Playback info
        ImageWithColor cover = MediaClient.currentlyPlaying.coverImage;
        context.drawTexture(
                RenderLayer::getGuiTextured,
                cover.image,
                MARGIN + widgetsOffset, MARGIN,
                0, 0,
                IMAGE_SIZE, IMAGE_SIZE,
                1, 1, 1, 1 // When drawing full texture, they can be 1
        );
        int nextX = MARGIN *2 + widgetsOffset + IMAGE_SIZE + 3;

        Text title = Text.of(MediaClient.currentlyPlaying.title);
        Text artist = Text.of(MediaClient.currentlyPlaying.artist);

        title = TextTranter.getTrantedText(title, textRenderer, INFO_TEXT_SIZE);
        artist = TextTranter.getTrantedText(artist, textRenderer, INFO_TEXT_SIZE);

        context.enableScissor(
                IMAGE_SIZE + MARGIN*2 + 2 + widgetsOffset, 0,
                PLAYBACK_SIZE + widgetsOffset, height
        );
        context.drawText(
                textRenderer,
                title,
                nextX, MARGIN + TITLE_Y,
                0xFFFFFFFF, false
        ); // title
        context.drawText(
                textRenderer,
                artist,
                nextX, MARGIN + ARTIST_Y,
                0xFFFFFFFF, false
        ); // artist
        context.disableScissor();

        // Text for progress bar
        context.drawText(
                textRenderer,
                Text.of(MediaClient.progressLabel),
                MARGIN + widgetsOffset,
                MARGIN + PLAYBACK_CONTROL_Y + 35,
                0xFFFFFFFF, false
        ); // progress label

        context.drawText(
                textRenderer,
                Text.of(MediaClient.currentlyPlaying.durationLabel),
                MARGIN + widgetsOffset + PLAYBACK_SIZE - textRenderer.getWidth(Text.of(MediaClient.currentlyPlaying.durationLabel)) + 1,
                MARGIN + PLAYBACK_CONTROL_Y + 35,
                0xFFFFFFFF, false
        ); // duration label
    }

    @Override
    public void close() {
        super.close();
        MediaClient.nowPlayingScreen = null;
    }

    public void updateStatus() {
        playPauseButton.setLabel(MediaClient.isPlaying ? Icons.PAUSE : Icons.RESUME);
        progressBar.setValue((float) MediaClient.progressValue);
        repeatButton.setLabel(RepeatMode.getAsText(MediaClient.repeat));
        shuffleButton.setLabel(MediaClient.shuffle ? Icons.SHUFFLE_ON : Icons.SHUFFLE);
    }
    public void updateNowPlaying() {}
    public void nothingPlaying() {}
    public void updateCoverImage() {}
}