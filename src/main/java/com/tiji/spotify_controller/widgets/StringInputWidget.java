package com.tiji.spotify_controller.widgets;

import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class StringInputWidget extends EditBox implements ValueHolder {
    private final Component icon;
    private static final Minecraft client = Minecraft.getInstance();
    private long time = System.currentTimeMillis();
    private static final long CURSOR_BLINK_DURATION = 1000;
    private static final int MAX_TYPING_PAUSE = 500;
    private final Consumer<String> action;
    private boolean didRunAction = false;

    public StringInputWidget(Font textRenderer, int x, int y, int width, int height, Component text, Component icon, Consumer<String> action) {
        super(textRenderer, x, y, width, height, text);
        setMaxLength(Integer.MAX_VALUE);
        this.icon = icon;
        this.action = action;
    }

    public StringInputWidget(int x, int y, int width, int height) {
        this(client.font, x, y, width, height, Component.literal(""), Component.literal(""), s -> {});
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        context.renderOutline(getX(), getY(), width, height, 0xFFFFFFFF);

        int y = (height - client.font.lineHeight) / 2 + getY();
        context.drawString(client.font, icon, width - 18 + getX(), y+2, 0xFFFFFFFF, false);

        context.enableScissor(getX(), getY(), getX() + width - 18, getY() + height);
        context.drawString(client.font, getValue(), getX() + 4, y+1, 0xFFFFFFFF, false);
        context.disableScissor();

        long timePast = System.currentTimeMillis() - time;
        boolean shouldBlink = timePast % CURSOR_BLINK_DURATION < CURSOR_BLINK_DURATION / 2;
        if (shouldBlink && isFocused()) {
            context.vLine(client.font.width(getValue()) + getX() + 4, getY() + 3, getY() + height - 5, 0xFFFFFFFF);
        }

        if (!didRunAction && timePast > MAX_TYPING_PAUSE) {
            didRunAction = true;
            action.accept(getValue());
        }
    }

    @Override
    public void insertText(String text) {
        if (client.font.width(getValue_() + text) > width - 22) return; // text is too long; probably won't fit
        time = System.currentTimeMillis();
        didRunAction = false;
        super.insertText(text);
    }

    @Override
    public Object getValue_() {
        return getValue();
    }

    @Override
    public void setValue(Object value) {
        setValue(value.toString());
    }
}