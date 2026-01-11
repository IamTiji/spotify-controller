package com.tiji.spotify_controller.ui;

import com.tiji.spotify_controller.Main;
import com.tiji.spotify_controller.api.ApiCalls;
import com.tiji.spotify_controller.util.SafeDrawer;
import com.tiji.spotify_controller.util.ImageWithColor;
import com.tiji.spotify_controller.util.RepeatMode;
import com.tiji.spotify_controller.util.TextUtils;
import com.tiji.spotify_controller.widgets.BorderlessButtonWidget;
import com.tiji.spotify_controller.widgets.ProgressWidget;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

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

    private static final Map<Component, Class<? extends SecondaryBaseScreen>> SUBSCREENS = Map.of(
            subscreenText(Icons.SEARCH, "ui.spotify_controller.subscreens.search"), SearchScreen.class
    );
    private static final int SUBSCREEN_BUTTONS_HEIGHT = 16;
    private static Component subscreenText(Component Icon, String description) {
        return Icon.copy()
                .append(" ")
                .append(Component.translatable(description)
                        .setStyle(Style.EMPTY.withFont(TextUtils.DEFAULT)));
    }

    public NowPlayingScreen() {
        super(true);
    }

    @Override
    protected void init() {
        super.init();

        Minecraft client = Minecraft.getInstance();

        if (isFirstInit) {
            GLFW.glfwSetCursorPos(client.getWindow().getWindow(), 0, 0);
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
        addRenderableWidget(shuffleButton); // Shuffle

        x += BorderlessButtonWidget.BUTTON_SIZE + 1;
        addRenderableWidget(
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
        addRenderableWidget(playPauseButton);

        x += BorderlessButtonWidget.BUTTON_SIZE + 1;
        addRenderableWidget(
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
        addRenderableWidget(repeatButton); // Repeat

        // Progress bar
        progressBar = new ProgressWidget(
                MARGIN + widgetsOffset, (int) (MARGIN * 1.5 + IMAGE_SIZE), PLAYBACK_SIZE,
                (float) Main.playbackState.progressValue,
                (v) -> ApiCalls.setPlaybackLoc((int) (Main.currentlyPlaying.duration * v))
        );
        addRenderableWidget(progressBar);

        // Subscreen button
        y = height - MARGIN - SUBSCREEN_BUTTONS_HEIGHT;
        for (Map.Entry<Component, Class<? extends SecondaryBaseScreen>> entry : SUBSCREENS.entrySet()) {
            addRenderableWidget(
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
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // Playback info
        ImageWithColor cover = Main.currentlyPlaying.coverImage;
        SafeDrawer.drawImage(
                context,
                cover.image,
                MARGIN + widgetsOffset, MARGIN,
                0, 0,
                IMAGE_SIZE, IMAGE_SIZE
        );

        int nextX = MARGIN *2 + widgetsOffset + IMAGE_SIZE + 3;

        Component title = Component.translationArg(Main.currentlyPlaying.title);
        Component artist = Component.nullToEmpty(Main.currentlyPlaying.artist);

        title = TextUtils.getTrantedText(title, INFO_TEXT_SIZE);
        artist = TextUtils.getTrantedText(artist, INFO_TEXT_SIZE);

        context.enableScissor(
                IMAGE_SIZE + MARGIN*2 + 2 + widgetsOffset, 0,
                PLAYBACK_SIZE + widgetsOffset, height
        );
        context.drawString(
                font,
                title,
                nextX, MARGIN + TITLE_Y,
                0xFFFFFFFF, false
        ); // title
        context.drawString(
                font,
                artist,
                nextX, MARGIN + ARTIST_Y,
                0xFFFFFFFF, false
        ); // artist
        context.disableScissor();

        // Text for progress bar
        context.drawString(
                font,
                Component.nullToEmpty(Main.playbackState.progressLabel),
                MARGIN + widgetsOffset,
                MARGIN + PLAYBACK_CONTROL_Y + 35,
                0xFFFFFFFF, false
        ); // progress label

        context.drawString(
                font,
                Component.nullToEmpty(Main.currentlyPlaying.durationLabel),
                MARGIN + widgetsOffset + PLAYBACK_SIZE - font.width(Component.nullToEmpty(Main.currentlyPlaying.durationLabel)) + 1,
                MARGIN + PLAYBACK_CONTROL_Y + 35,
                0xFFFFFFFF, false
        ); // duration label

        drawFullName(context, mouseX, mouseY, nextX, nextX);
    }

    @Override
    public void onClose() {
        super.onClose();
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

    private void drawFullName(GuiGraphics context,
                              int mouseX, int mouseY,
                              int titleX, int artistX) {
        int textHeight = font.lineHeight;
        int textWidth = PLAYBACK_SIZE - MARGIN*2 - IMAGE_SIZE;

        if (isInsideOf(mouseX, mouseY, titleX, TITLE_Y + MARGIN, textWidth, textHeight))
            context.renderTooltip(font, Component.translationArg(Main.currentlyPlaying.title), mouseX, mouseY);
        else if (isInsideOf(mouseX, mouseY, artistX, ARTIST_Y + MARGIN, textWidth, textHeight))
            context.renderTooltip(font, Component.nullToEmpty(Main.currentlyPlaying.artist), mouseX, mouseY);
    }

    private static boolean isInsideOf(int mouseX, int mouseY, int x, int y, int w, int h) {
        return  mouseX >= x && mouseX <= x + w &&
                mouseY >= y && mouseY <= y + h;
    }
}