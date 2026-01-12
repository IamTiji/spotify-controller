package com.tiji.spotify_controller.ui;

import com.tiji.spotify_controller.Main;
import com.tiji.spotify_controller.util.SafeDrawer;
import com.tiji.spotify_controller.util.ImageWithColor;
import java.util.ArrayList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;

public class SecondaryBaseScreen extends BaseScreen {
    private static final int IMAGE_SIZE = 30;
    private static final int MARGIN = 10;
    private static final int TITLE_Y = 24;
    private static final int ARTIST_Y = 9;

    protected static final int INFO_HEIGHT = MARGIN*2 + IMAGE_SIZE;

    private final ArrayList<Renderable> drawables = new ArrayList<>();

    public SecondaryBaseScreen() {
        super(false);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // Playback info
        ImageWithColor cover = Main.currentlyPlaying.coverImage;
        SafeDrawer.drawImage(
                context,
                cover.image,
                MARGIN, height - IMAGE_SIZE - MARGIN,
                0, 0,
                IMAGE_SIZE, IMAGE_SIZE
        );
        int nextX = (int) (MARGIN*1.5 + widgetsOffset + IMAGE_SIZE + 3);

        context.drawString(
                font,
                Main.currentlyPlaying.title,
                nextX, height - (MARGIN + TITLE_Y),
                0xFFFFFFFF, false
        ); // title
        context.drawString(
                font,
                Main.currentlyPlaying.artist,
                nextX, height - (MARGIN + ARTIST_Y),
                0xFFFFFFFF, false
        ); // artist

        context.enableScissor(0, 0, width, height - INFO_HEIGHT);
        drawables.forEach(drawable -> drawable.render(context, mouseX, mouseY, delta));
        context.disableScissor();
    }

    @Override
    public void onClose() {
        assert minecraft != null;
        minecraft.setScreen(Main.nowPlayingScreen);
    }

    @Override
    protected <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T drawableElement) {
        return drawables.add(drawableElement) ? drawableElement : null;
    }
}
