package com.tiji.spotify_controller.ui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.input.KeyEvent;

public abstract class SafeAbstractScreen extends Screen {
    protected SafeAbstractScreen(Component title) {
        super(title);
    }

    public boolean safeKeyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);

        safeRender(graphics, mouseX, mouseY, a);
    }

    public abstract void safeRender(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks);

    @Override
    public boolean keyPressed(KeyEvent event) {
        return safeKeyPressed(event.key(), event.scancode(), event.modifiers());
    }
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(new KeyEvent(keyCode, scanCode, modifiers));
    }
    public boolean keyPressedSuper(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(new KeyEvent(keyCode, scanCode, modifiers));
    }
    public static KeyEvent convertToKeyEvent(int keyCode, int scanCode, int modifiers) {
        return new KeyEvent(keyCode, scanCode, modifiers);
    }
}
