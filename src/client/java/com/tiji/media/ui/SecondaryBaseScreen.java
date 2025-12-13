package com.tiji.media.ui;

import com.tiji.media.MediaClient;
import com.tiji.media.util.ImageWithColor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.render.RenderLayer;

import java.util.ArrayList;

public class SecondaryBaseScreen extends BaseScreen {
    private static final int IMAGE_SIZE = 30;
    private static final int MARGIN = 10;
    private static final int TITLE_Y = 24;
    private static final int ARTIST_Y = 9;

    protected static final int INFO_HEIGHT = MARGIN*2 + IMAGE_SIZE;

    private final ArrayList<Drawable> drawables = new ArrayList<>();

    public SecondaryBaseScreen() {
        super(false);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // Playback info
        ImageWithColor cover = MediaClient.currentlyPlaying.coverImage;
        context.drawTexture(
                RenderLayer::getGuiTextured,
                cover.image,
                MARGIN, height - IMAGE_SIZE - MARGIN,
                0, 0,
                IMAGE_SIZE, IMAGE_SIZE,
                1, 1, 1, 1 // When drawing full texture, they can be 1
        );
        int nextX = (int) (MARGIN*1.5 + widgetsOffset + IMAGE_SIZE + 3);

        context.drawText(
                textRenderer,
                MediaClient.currentlyPlaying.title,
                nextX, height - (MARGIN + TITLE_Y),
                0xFFFFFFFF, false
        ); // title
        context.drawText(
                textRenderer,
                MediaClient.currentlyPlaying.artist,
                nextX, height - (MARGIN + ARTIST_Y),
                0xFFFFFFFF, false
        ); // artist

        context.enableScissor(0, 0, width, height - INFO_HEIGHT);
        drawables.forEach(drawable -> drawable.render(context, mouseX, mouseY, delta));
        context.disableScissor();
    }

    @Override
    public void close() {
        assert client != null;
        client.setScreen(MediaClient.nowPlayingScreen);
    }

    @Override
    protected <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement) {
        return drawables.add(drawableElement) ? drawableElement : null;
    }
}
