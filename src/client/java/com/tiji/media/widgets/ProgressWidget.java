package com.tiji.media.widgets;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class ProgressWidget extends ClickableWidget {
    private static final int h = 10;
    private static final int RAIL_HEIGHT = 2;
    private static final int RAIL_Y = (h - RAIL_HEIGHT) / 2;
    private static final int THUMB_SIZE = 4;

    private static final int COLOR = 0xFFFFFFFF;

    private float value;
    private final Consumer<Float> action;
    private boolean dragging;

    public ProgressWidget(int x, int y, int w, float value, Consumer<Float> action) {
        super(x, y, w, h, Text.empty());
        this.value = value;
        this.action = action;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        dragging = true;
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        super.onRelease(mouseX, mouseY);
        dragging = false;
        action.accept(value);
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (dragging) {
            value = getValue(mouseX - getX());
        }

        int thumbPosition = getThumbPosition();
        context.fill(getX(), getY() + RAIL_Y,
                getX() + thumbPosition, getY() + RAIL_Y + RAIL_HEIGHT, COLOR); // Rail left
        context.fill(getX() + thumbPosition, getY() + RAIL_Y,
                getX() + getWidth(), getY() + RAIL_Y + RAIL_HEIGHT, COLOR - 0x88000000); // Translucent rail right
        context.fill(getX() + thumbPosition, getY() + (h - THUMB_SIZE) / 2,
                getX() + thumbPosition + THUMB_SIZE, getY() + (h + THUMB_SIZE) / 2, COLOR); // Thumb
    }

    protected int getThumbPosition() {
        return (int) (value * (getWidth() - THUMB_SIZE));
    }

    protected float getValue(int x) {
        return x / (float) getWidth();
    }

    public void setValue(float value) {
        this.value = Math.max(0, Math.min(1, value));
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}
}