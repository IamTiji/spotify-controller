package com.tiji.spotify_controller.ui;

import com.tiji.spotify_controller.Main;
import com.tiji.spotify_controller.util.SafeDrawer;
import com.tiji.spotify_controller.util.ImageWithColor;
import java.util.ArrayList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import org.jetbrains.annotations.NotNull;

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
    protected void clearWidgets() {
        super.clearWidgets();

        drawables.clear();
    }

    @Override
    public void safeRender(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.safeRender(context, mouseX, mouseY, delta);

        // Playback info
        ImageWithColor cover = Main.currentlyPlaying.coverImage;
        SafeDrawer.drawImage(
                context,
                cover.getImage(),
                MARGIN, height - IMAGE_SIZE - MARGIN,
                0, 0,
                IMAGE_SIZE, IMAGE_SIZE
        );
        int nextX = (int) (MARGIN*1.5 + widgetsOffset + IMAGE_SIZE + 3);

        SafeDrawer.drawString(context, 
                font,
                Main.currentlyPlaying.title,
                nextX, height - (MARGIN + TITLE_Y),
                0xFFFFFFFF, false
        ); // title
        SafeDrawer.drawString(context, 
                font,
                Main.currentlyPlaying.artist,
                nextX, height - (MARGIN + ARTIST_Y),
                0xFFFFFFFF, false
        ); // artist

        context.enableScissor(0, 0, width, height - INFO_HEIGHT);
        //#if MC<26100
        drawables.forEach(drawable -> drawable.render(context, mouseX, mouseY, delta));
        //#else
        //$$ drawables.forEach(drawable -> drawable.extractRenderState(context, mouseX, mouseY, delta));
        //#endif
        context.disableScissor();
    }

    @Override
    public void onClose() {
        assert minecraft != null;
        minecraft.setScreen(Main.nowPlayingScreen);
    }

    @Override
    protected <T extends GuiEventListener & Renderable & NarratableEntry> @NotNull T addRenderableWidget(T drawableElement) {
        super.addWidget(drawableElement);
        drawables.add(drawableElement);
        return drawableElement;
    }

    public void songChangeCallback() {}
}
