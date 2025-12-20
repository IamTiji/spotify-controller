package com.tiji.spotify_controller.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class StringInputWidget extends TextFieldWidget implements ValueHolder {
    private final Text icon;
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private long time = System.currentTimeMillis();
    private static final long CURSOR_BLINK_DURATION = 1000;
    private static final int MAX_TYPING_PAUSE = 500;
    private final Consumer<String> action;
    private boolean didRunAction = false;

    public StringInputWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text, Text icon, Consumer<String> action) {
        super(textRenderer, x, y, width, height, text);
        setMaxLength(Integer.MAX_VALUE);
        this.icon = icon;
        this.action = action;
    }

    public StringInputWidget(int x, int y, int width, int height) {
        this(client.textRenderer, x, y, width, height, Text.literal(""), Text.literal(""), s -> {});
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawBorder(getX(), getY(), width, height, 0xFFFFFFFF);

        int y = (height - client.textRenderer.fontHeight) / 2 + getY();
        context.drawText(client.textRenderer, icon, width - 18 + getX(), y+2, 0xFFFFFFFF, false);

        context.enableScissor(getX(), getY(), getX() + width - 18, getY() + height);
        context.drawText(client.textRenderer, getText(), getX() + 4, y+1, 0xFFFFFFFF, false);
        context.disableScissor();

        long timePast = System.currentTimeMillis() - time;
        boolean shouldBlink = timePast % CURSOR_BLINK_DURATION < CURSOR_BLINK_DURATION / 2;
        if (shouldBlink && isFocused()) {
            context.drawVerticalLine(client.textRenderer.getWidth(getText()) + getX() + 4, getY() + 3, getY() + height - 5, 0xFFFFFFFF);
        }

        if (!didRunAction && timePast > MAX_TYPING_PAUSE) {
            didRunAction = true;
            action.accept(getText());
        }
    }

    @Override
    public void write(String text) {
        if (client.textRenderer.getWidth(getText() + text) > width - 22) return; // text is too long; probably won't fit
        time = System.currentTimeMillis();
        didRunAction = false;
        super.write(text);
    }

    @Override
    public Object getValue() {
        return getText();
    }

    @Override
    public void setValue(Object value) {
        setText(value.toString());
    }
}