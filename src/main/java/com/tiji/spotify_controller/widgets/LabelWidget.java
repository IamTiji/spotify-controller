package com.tiji.spotify_controller.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import java.util.function.Consumer;

public class LabelWidget implements Renderable, GuiEventListener, NarratableEntry, LayoutElement {
    private int x, y;
    private Component text;

    public LabelWidget(int x, int y, Component text) {
        this.x = x;
        this.y = y;
        this.text = text;
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        context.drawString(Minecraft.getInstance().font, text, x, y, 0xFFFFFFFF, false);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> consumer) {}

    @Override
    public void setFocused(boolean focused) {}

    public void setY(int y) {
        this.y = y;
    }

    public Component getText() {
        return text;
    }

    public void setText(Component text) {
        this.text = text;
    }


    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public ScreenRectangle getRectangle() {
        return GuiEventListener.super.getRectangle();
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput builder) {}
}
