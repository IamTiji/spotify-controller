package com.tiji.media.ui;

//import com.tiji.media.Media;
//import com.tiji.media.MediaClient;
//import com.tiji.media.api.ApiCalls;
//import com.tiji.media.util.repeatMode;
//import com.tiji.media.widgets.borderlessButtonWidget;
//import com.tiji.media.widgets.clickableSprite;
//import com.tiji.media.widgets.progressWidget;
//import io.github.cottonmc.cotton.gui.ValidatedSlot;
//import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
//import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
//import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
//import io.github.cottonmc.cotton.gui.widget.WLabel;
//import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
//import io.github.cottonmc.cotton.gui.widget.WSprite;
//import io.github.cottonmc.cotton.gui.widget.data.Axis;
//import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
//import io.github.cottonmc.cotton.gui.widget.data.Insets;
//import net.fabricmc.fabric.api.util.TriState;
//import net.minecraft.client.MinecraftClient;
//import net.minecraft.client.gui.screen.Screen;
//import net.minecraft.text.Style;
//import net.minecraft.text.Text;
//import net.minecraft.util.Identifier;
//import net.minecraft.util.Util;
//
//public class NowPlayingScreen extends LightweightGuiDescription {
//    private static class RootPanel extends WPlainPanel {
//        public void onHidden() {
//            MediaClient.nowPlayingScreen = null;
//        }
//    }
//
//    private final WLabel songName = new WLabel(Text.translatable("ui.media.nothing_playing"));
//    private final WLabel artistName = new WLabel(Text.translatable("ui.media.unknown_artist"));
//    private final progressWidget progressBar = new progressWidget(0, 100, Axis.HORIZONTAL);
//    private final WLabel durationLabel = new WLabel(Text.translatable("ui.media.unknown_duration"));
//    private final WLabel currentTimeLabel = new WLabel(Text.translatable("ui.media.unknown_time"));
//    private final borderlessButtonWidget playPauseButton = new borderlessButtonWidget(Icons.PAUSE);
//    private final WSprite albumCover = new WSprite(Identifier.of("media", "ui/nothing.png"));
//    private final borderlessButtonWidget repeat = new borderlessButtonWidget(repeatMode.getAsText(MediaClient.repeat));
//    private final borderlessButtonWidget shuffle = new borderlessButtonWidget(Icons.SHUFFLE);
//    private final borderlessButtonWidget like = new borderlessButtonWidget(Icons.ADD_TO_FAV);
//    private final WPlainPanel root = new RootPanel();
//
//    public NowPlayingScreen() {
//        super();
//
//        setUseDefaultRootBackground(false);
//        root.setBackgroundPainter(BackgroundPainter.createColorful(MediaClient.currentlyPlaying.coverImage.color));
//
//        root.setSize(300, 200);
//        root.setInsets(Insets.NONE);
//
//        root.add(albumCover, 100, 10, 100, 100);
//
//        root.add(new clickableSprite(Identifier.of("media", "ui/attribution.png")).setOnClick(() -> {
//            if (MediaClient.currentlyPlaying.songURI == null) return;
//            Util.getOperatingSystem().open(MediaClient.currentlyPlaying.songURI);
//        }), 270, 10);
//
//        songName.setHorizontalAlignment(HorizontalAlignment.CENTER);
//        root.add(songName, 100, 120, 100, 20);
//
//        artistName.setHorizontalAlignment(HorizontalAlignment.CENTER);
//        root.add(artistName, 100, 135, 100, 20);
//
//        root.add(new borderlessButtonWidget(Icons.SEARCH).setOnClick(() -> {
//            Screen screen = new CottonClientScreen(new SearchScreen());
//            MinecraftClient.getInstance().setScreen(screen);
//            MediaClient.nowPlayingScreen = null;
//        }), 80, 150, 20, 20);
//
//        shuffle.setOnClick(() -> {
//            MediaClient.shuffle = !MediaClient.shuffle;
//            ApiCalls.setShuffle(MediaClient.shuffle);
//        });
//        root.add(shuffle, 100, 150, 20, 20);
//
//        root.add(new borderlessButtonWidget(Icons.NEXT).setOnClick(ApiCalls::previousTrack), 120, 150, 20, 20);
//
//        playPauseButton.setOnClick(() -> {
//            if (MediaClient.currentlyPlaying.Id.isEmpty()) return;
//
//            MediaClient.isPlaying = !MediaClient.isPlaying;
//            ApiCalls.playPause(MediaClient.isPlaying);
//        });
//        root.add(playPauseButton, 140, 150, 20, 20);
//
//        root.add(new borderlessButtonWidget(Icons.PREVIOUS).setOnClick(ApiCalls::nextTrack), 160, 150, 20, 20);
//
//        repeat.setOnClick(() -> {
//            MediaClient.repeat = repeatMode.getNextMode(MediaClient.repeat);
//            ApiCalls.setRepeat(MediaClient.repeat);
//        });
//        root.add(repeat, 180, 150, 20, 20);
//
//        like.setOnClick(() -> {
//            MediaClient.isLiked = !MediaClient.isLiked;
//            ApiCalls.toggleLikeSong(MediaClient.currentlyPlaying.Id, MediaClient.isLiked);
//        });
//        root.add(like, 200, 150, 20, 20);
//
//        currentTimeLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
//        root.add(currentTimeLabel, 10, 160, 60, 20);
//
//        progressBar.setMaxValue(300);
//        root.add(progressBar, 10, 175, 280, 10);
//
//        durationLabel.setHorizontalAlignment(HorizontalAlignment.RIGHT);
//        root.add(durationLabel, 230, 160, 60, 20);
//
//        root.validate(this);
//
//        setRootPanel(root);
//    }
//
//    @Override
//    public TriState isDarkMode() {
//        return MediaClient.currentlyPlaying.coverImage.shouldUseDarkUI ? TriState.TRUE : TriState.FALSE;
//    }
//
//    public void updateStatus() {
//        if (MediaClient.currentlyPlaying.Id.isEmpty()) return;
//
//        if (progressBar.allowUpdateProgress) {
//            progressBar.setValue((int) Math.round(MediaClient.progressValue * 300));
//        }
//        currentTimeLabel.setText(Text.of(MediaClient.progressLabel));
//        playPauseButton.setLabel(MediaClient.isPlaying ? Icons.PAUSE : Icons.RESUME);
//        repeat.setLabel(repeatMode.getAsText(MediaClient.repeat));
//        shuffle.setLabel(MediaClient.shuffle ? Icons.SHUFFLE_ON : Icons.SHUFFLE);
//        like.setLabel(MediaClient.isLiked ? Icons.REMOVE_FROM_FAV : Icons.ADD_TO_FAV);
//    }
//
//    public void updateNowPlaying() {
//        if (MediaClient.currentlyPlaying.Id.isEmpty()) {
//            nothingPlaying();
//            return;
//        }
//
//        Media.LOGGER.info(MediaClient.currentlyPlaying.toString());
//
//        songName.setText(MediaClient.currentlyPlaying.title);
//        artistName.setText(Text.of(MediaClient.currentlyPlaying.artist));
//        durationLabel.setText(Text.of(MediaClient.currentlyPlaying.durationLabel));
//        updateCoverImage();
//        updateStatus();
//    }
//
//    public void nothingPlaying() {
//        songName.setText(Text.translatable("ui.media.nothing_playing"));
//        artistName.setText(Text.translatable("ui.media.unknown_artist"));
//        durationLabel.setText(Text.translatable("ui.media.unknown_duration"));
//        updateCoverImage();
//        progressBar.setValue(0);
//        currentTimeLabel.setText(Text.translatable("ui.media.unknown_time"));
//    }
//
//    public void updateCoverImage() {
//        albumCover.setImage(MediaClient.currentlyPlaying.coverImage.image);
//        root.setBackgroundPainter(BackgroundPainter.createColorful(MediaClient.currentlyPlaying.coverImage.color));
//    }
//}
//

import com.tiji.media.MediaClient;
import com.tiji.media.api.ApiCalls;
import com.tiji.media.util.imageWithColor;
import com.tiji.media.util.repeatMode;
import com.tiji.media.widgets.borderlessButtonWidget;
import com.tiji.media.widgets.progressWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class NowPlayingScreen extends BaseScreen {
    private static final int MARGIN = 10;
    private static final int IMAGE_SIZE = 70;

    private static final int PLAYBACK_CONTROL_Y = 50;
    private static final int TITLE_Y = 8;
    private static final int ARTIST_Y = 23;
    private static final int PLAYBACK_SIZE = 200;
    private static final int INFO_TEXT_SIZE = PLAYBACK_SIZE - MARGIN*2 - IMAGE_SIZE;

    private borderlessButtonWidget playPauseButton;
    private progressWidget progressBar;
    private borderlessButtonWidget repeatButton;
    private borderlessButtonWidget shuffleButton;

    private boolean isFirstInit = true;

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

        shuffleButton = new borderlessButtonWidget(
                Icons.SHUFFLE,
                x, y,
                () -> ApiCalls.setShuffle(!MediaClient.shuffle)
        );
        addDrawableChild(shuffleButton); // Shuffle

        x += borderlessButtonWidget.BUTTON_SIZE + 1;
        addDrawableChild(
                new borderlessButtonWidget(
                        Icons.PREVIOUS,
                        x, y,
                        ApiCalls::previousTrack
                )
        ); // Previous

        x += borderlessButtonWidget.BUTTON_SIZE + 1;
        playPauseButton = new borderlessButtonWidget(
                MediaClient.isPlaying ? Icons.PAUSE : Icons.RESUME,
                x, y,
                () -> ApiCalls.playPause(!MediaClient.isPlaying)
        );
        addDrawableChild(playPauseButton);

        x += borderlessButtonWidget.BUTTON_SIZE + 1;
        addDrawableChild(
                new borderlessButtonWidget(
                        Icons.NEXT,
                        x, y,
                        ApiCalls::nextTrack
                )
        ); // Next

        x += borderlessButtonWidget.BUTTON_SIZE + 1;
        repeatButton = new borderlessButtonWidget(
                Icons.REPEAT,
                x, y,
                () -> ApiCalls.setRepeat(repeatMode.getNextMode(MediaClient.repeat))
        );
        addDrawableChild(repeatButton); // Repeat

        // Progress bar
        progressBar = new progressWidget(
                MARGIN + widgetsOffset, (int) (MARGIN * 1.5 + IMAGE_SIZE), PLAYBACK_SIZE,
                (float) MediaClient.progressValue,
                (v) -> ApiCalls.setPlaybackLoc((int) (MediaClient.currentlyPlaying.duration * v))
        );
        addDrawableChild(progressBar);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // Playback info
        imageWithColor cover = MediaClient.currentlyPlaying.coverImage;
        context.drawTexture(
                RenderLayer::getGuiTextured,
                cover.image,
                MARGIN + widgetsOffset, MARGIN,
                0, 0,
                IMAGE_SIZE, IMAGE_SIZE,
                300, 300,
                300, 300
        );
        int nextX = MARGIN *2 + widgetsOffset + IMAGE_SIZE + 3;

        Text title = Text.of(MediaClient.currentlyPlaying.title);
        Text artist = Text.of(MediaClient.currentlyPlaying.artist);

        int titleWidth = textRenderer.getWidth(title);
        int artistWidth = textRenderer.getWidth(artist);

        if (titleWidth > INFO_TEXT_SIZE)
            title = getTrantedText(title);
        if (artistWidth > INFO_TEXT_SIZE)
            artist = getTrantedText(artist);

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
                MARGIN + widgetsOffset + 201 - textRenderer.getWidth(Text.of(MediaClient.currentlyPlaying.durationLabel)),
                MARGIN + PLAYBACK_CONTROL_Y + 35,
                0xFFFFFFFF, false
        ); // duration label
    }

    private Text getTrantedText(Text title) {
        int ellipsisSize = textRenderer.getWidth("...");
        String plainText = textRenderer.trimToWidth(title, INFO_TEXT_SIZE - ellipsisSize).getString();
        return Text.of(plainText + "...");
    }

    public void updateStatus() {
        playPauseButton.setLabel(MediaClient.isPlaying ? Icons.PAUSE : Icons.RESUME);
        progressBar.setValue((float) MediaClient.progressValue);
        repeatButton.setLabel(repeatMode.getAsText(MediaClient.repeat));
        shuffleButton.setLabel(MediaClient.shuffle ? Icons.SHUFFLE_ON : Icons.SHUFFLE);
    }
    public void updateNowPlaying() {}
    public void nothingPlaying() {}
    public void updateCoverImage() {}
}