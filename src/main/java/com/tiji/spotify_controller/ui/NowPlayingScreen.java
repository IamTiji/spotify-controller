package com.tiji.spotify_controller.ui;

import com.tiji.spotify_controller.Main;
import com.tiji.spotify_controller.api.ApiCalls;
import com.tiji.spotify_controller.util.ImageDrawer;
import com.tiji.spotify_controller.util.ImageWithColor;
import com.tiji.spotify_controller.util.RepeatMode;
import com.tiji.spotify_controller.util.TextUtils;
import com.tiji.spotify_controller.widgets.BorderlessButtonWidget;
import com.tiji.spotify_controller.widgets.ProgressWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
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
            subscreenText(Icons.SEARCH, "ui.spotify_controller.subscreens.search"), SearchScreen.class
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
                () -> ApiCalls.setShuffle(!Main.playbackState.shuffle),
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
                Main.playbackState.isPlaying ? Icons.PAUSE : Icons.RESUME,
                x, y,
                () -> ApiCalls.playPause(!Main.playbackState.isPlaying),
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
                () -> ApiCalls.setRepeat(RepeatMode.getNextMode(Main.playbackState.repeat)),
                true
        );
        addDrawableChild(repeatButton); // Repeat

        // Progress bar
        progressBar = new ProgressWidget(
                MARGIN + widgetsOffset, (int) (MARGIN * 1.5 + IMAGE_SIZE), PLAYBACK_SIZE,
                (float) Main.playbackState.progressValue,
                (v) -> ApiCalls.setPlaybackLoc((int) (Main.currentlyPlaying.duration * v))
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
        ImageWithColor cover = Main.currentlyPlaying.coverImage;
        ImageDrawer.drawImage(
                context,
                cover.image,
                MARGIN + widgetsOffset, MARGIN,
                0, 0,
                IMAGE_SIZE, IMAGE_SIZE
        );

        int nextX = MARGIN *2 + widgetsOffset + IMAGE_SIZE + 3;

        Text title = Text.of(Main.currentlyPlaying.title);
        Text artist = Text.of(Main.currentlyPlaying.artist);

        title = TextUtils.getTrantedText(title, INFO_TEXT_SIZE);
        artist = TextUtils.getTrantedText(artist, INFO_TEXT_SIZE);

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
                Text.of(Main.playbackState.progressLabel),
                MARGIN + widgetsOffset,
                MARGIN + PLAYBACK_CONTROL_Y + 35,
                0xFFFFFFFF, false
        ); // progress label

        context.drawText(
                textRenderer,
                Text.of(Main.currentlyPlaying.durationLabel),
                MARGIN + widgetsOffset + PLAYBACK_SIZE - textRenderer.getWidth(Text.of(Main.currentlyPlaying.durationLabel)) + 1,
                MARGIN + PLAYBACK_CONTROL_Y + 35,
                0xFFFFFFFF, false
        ); // duration label
    }

    @Override
    public void close() {
        super.close();
        Main.nowPlayingScreen = null;
    }

    public void updateStatus() {
        playPauseButton.setLabel(Main.playbackState.isPlaying ? Icons.PAUSE : Icons.RESUME);
        progressBar.setValue((float) Main.playbackState.progressValue);
        repeatButton.setLabel(RepeatMode.getAsText(Main.playbackState.repeat));
        shuffleButton.setLabel(Main.playbackState.shuffle ? Icons.SHUFFLE_ON : Icons.SHUFFLE);
    }
    public void updateNowPlaying() {}
    public void nothingPlaying() {}
    public void updateCoverImage() {}
}