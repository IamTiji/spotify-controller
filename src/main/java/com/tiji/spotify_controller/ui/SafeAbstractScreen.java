package com.tiji.spotify_controller.ui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
//#if MC>=12109
//$$ import net.minecraft.client.input.KeyEvent;
//#endif

public abstract class SafeAbstractScreen extends Screen {
    protected SafeAbstractScreen(Component title) {
        super(title);
    }

    public boolean safeKeyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float a) {
        super.render(graphics, mouseX, mouseY, a);

        safeRender(graphics, mouseX, mouseY, a);
    }

    public abstract void safeRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks);

    //#if MC<=12108
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return safeKeyPressed(keyCode, scanCode, modifiers);
    }
    public boolean keyPressedSuper(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    //#else
    //$$ @Override
    //$$ public boolean keyPressed(KeyEvent event) {
    //$$     return safeKeyPressed(event.key(), event.scancode(), event.modifiers());
    //$$ }
    //$$
    //$$ public boolean keyPressedSuper(int keyCode, int scanCode, int modifiers) {
    //$$     return super.keyPressed(new KeyEvent(keyCode, scanCode, modifiers));
    //$$ }
    //$$ public static KeyEvent convertToKeyEvent(int keyCode, int scanCode, int modifiers) {
    //$$     return new KeyEvent(keyCode, scanCode, modifiers);
    //$$ }
    //#endif
}
