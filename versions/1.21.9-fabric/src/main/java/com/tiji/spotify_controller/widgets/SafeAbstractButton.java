package com.tiji.spotify_controller.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;

public abstract class SafeAbstractButton extends AbstractButton {
    public SafeAbstractButton(int i, int j, int k, int l, Component component) {
        super(i, j, k, l, component);
    }

    public abstract void onPress();

    @Override
    public void onPress(InputWithModifiers inputWithModifiers) {
        onPress();
    }

    public abstract void safeRender(GuiGraphics context, int mouseX, int mouseY, float delta);

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        safeRender(guiGraphics, i, j, f);
    }
}
