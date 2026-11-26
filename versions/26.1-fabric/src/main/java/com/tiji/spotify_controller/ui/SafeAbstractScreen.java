package com.tiji.spotify_controller.ui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.input.KeyEvent;
import org.jspecify.annotations.NonNull;

public abstract class SafeAbstractScreen extends Screen {
    protected SafeAbstractScreen(Component title) {
        super(title);
    }

    public boolean safeKeyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    //#if MC<=26300
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);

        safeRender(graphics, mouseX, mouseY, a);
    }
    //#else
    //$$ @Override
    //$$ public void extractRenderState(GuiGraphicsExtractor graphics) {
    //$$     super.extractRenderState(graphics);
    //$$
    //$$     safeRender(graphics, graphics.mouseX(), graphics.mouseY(), graphics.partialTicks());
    //$$ }
    //#endif

    public abstract void safeRender(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks);

    @Override
    public boolean keyPressed(KeyEvent event) {
        //#if MC<=26200
        return safeKeyPressed(event.key(), event.scancode(), event.modifiers());
        //#else
        //$$ return safeKeyPressed(event.key(), event.keycode(), event.modifiers());
        //#endif
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(createKeyEvent(keyCode, scanCode, modifiers));
    }

    public boolean keyPressedSuper(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(createKeyEvent(keyCode, scanCode, modifiers));
    }

    public static KeyEvent convertToKeyEvent(int keyCode, int scanCode, int modifiers) {
        return createKeyEvent(keyCode, scanCode, modifiers);
    }

    private static @NonNull KeyEvent createKeyEvent(int keyCode, int scanCode, int modifiers) {
        //#if MC<=26300
        return new KeyEvent(keyCode, scanCode, modifiers);
        //#else
        //$$ return new KeyEvent(keyCode, scanCode, modifiers, false);
        //#endif
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
