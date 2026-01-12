package com.tiji.spotify_controller.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public abstract class SafeAbstractWidget extends AbstractWidget {
    public SafeAbstractWidget(int i, int j, int k, int l, Component component) {
        super(i, j, k, l, component);
    }

    public void onClick(double x, double y) {}
    public void onRelease(double x, double y) {}
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public void onClick(MouseButtonEvent mouseButtonEvent, boolean bl) {
        super.onClick(mouseButtonEvent, bl);

        mouseClicked(mouseButtonEvent.x(), mouseButtonEvent.y(), mouseButtonEvent.button());
        if (mouseButtonEvent.button() == 0) onClick(mouseButtonEvent.x(), mouseButtonEvent.y());
    }

    @Override
    public void onRelease(MouseButtonEvent mouseButtonEvent) {
        super.onRelease(mouseButtonEvent);

        if (mouseButtonEvent.button() == 0) onRelease(mouseButtonEvent.x(), mouseButtonEvent.y());
    }

    public abstract void safeRender(GuiGraphics context, int mouseX, int mouseY, float delta);

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        safeRender(guiGraphics, i, j, f);
    }
}