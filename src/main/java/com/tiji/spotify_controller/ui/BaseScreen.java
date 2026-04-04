package com.tiji.spotify_controller.ui;

import com.tiji.spotify_controller.Main;
import com.tiji.spotify_controller.util.SafeDrawer;
import com.tiji.spotify_controller.util.ImageWithColor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BaseScreen extends SafeAbstractScreen {
    protected float totalTime = 0f;
    protected int widgetsOffset = -100;
    protected static final int ANIMATION_AMOUNT = 100;
    private static final float animationTime = 0.5f;

    public BaseScreen(boolean animate) {
        super(Component.nullToEmpty(""));

        if (!animate) {
            totalTime += animationTime;
            widgetsOffset = 0;
        }
    }

    @Override
    public void safeRender(GuiGraphics context, int mouseX, int mouseY, float delta) {
        //#if MC<=12106
        super.renderBackground(context, mouseX, mouseY, delta);
        //#endif

        totalTime += delta / 10f;
        float normalized = Math.min(totalTime, animationTime) / animationTime;
        int previousOffset = widgetsOffset;

        // Glow
        ImageWithColor cover = Main.currentlyPlaying.coverImage;
        int color = cover.color;

        SafeDrawer.drawImage(
                context,
                ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "ui/gradient.png"),
                widgetsOffset, 0,
                0, 0,
                255, height,
                255, 1,
                255, 1,
                color
        );

        widgetsOffset = (int) (-ANIMATION_AMOUNT + easeInOut(normalized) * ANIMATION_AMOUNT);

        for (GuiEventListener child : children()) {
            if (child instanceof LayoutElement widget) {
                widget.setX(widget.getX() + (widgetsOffset - previousOffset));
            }
        }

        //#if MC<26100
        renderables.forEach(it -> it.render(context, mouseX, mouseY, delta));
        //#else
        //$$ renderables.forEach(it -> it.extractRenderState(context, mouseX, mouseY, delta));
        //#endif
    }

    private float easeInOut(float t) {
        return t * t * (3 - 2 * t);
    }

    @Override
    public boolean safeKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyPressedSuper(keyCode, scanCode, modifiers)) return true;

        boolean handled = Main.SETUP_KEY
                //#if MC<=12108
                .matches(keyCode, scanCode);
                //#else
                //$$ .matches(convertToKeyEvent(keyCode, scanCode, modifiers));
                //#endif

        if (handled) {
            assert minecraft != null;
            minecraft.setScreen(null);
            return true;
        }
        return false;
    }
}