package com.tiji.media.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class LabelWidget implements Drawable, Element, Selectable, Widget {
    private int x, y;
    private Text text;

    public LabelWidget(int x, int y, Text text) {
        this.x = x;
        this.y = y;
        this.text = text;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawText(MinecraftClient.getInstance().textRenderer, text, x, y, 0xFFFFFFFF, false);
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
    public void forEachChild(Consumer<ClickableWidget> consumer) {}

    @Override
    public void setFocused(boolean focused) {}

    public void setY(int y) {
        this.y = y;
    }

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }


    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public ScreenRect getNavigationFocus() {
        return Element.super.getNavigationFocus();
    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {}
}
