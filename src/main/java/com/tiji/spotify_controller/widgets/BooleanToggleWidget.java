package com.tiji.spotify_controller.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class BooleanToggleWidget extends ClickableWidget implements ValueHolder {
    private boolean state;
    private static final Text OFF_TEXT = Text.translatable("ui.spotify_controller.toggle.off").formatted(Formatting.RED);
    private static final Text ON_TEXT = Text.translatable("ui.spotify_controller.toggle.on").formatted(Formatting.GREEN);

    public BooleanToggleWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Text.literal(""));
    }

    @Override
    public Object getValue() {
        return state;
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Boolean) {
            state = (boolean) value;
        } else {
            throw new IllegalArgumentException("Value must be of type Boolean");
        }
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        int yOffset = (getHeight() - textRenderer.fontHeight) / 2;
        Text text = state ? ON_TEXT : OFF_TEXT;

        if (isHovered(mouseX, mouseY))
            text = text.copy().formatted(Formatting.UNDERLINE);

        context.drawText(textRenderer, text, getX(), getY() + yOffset, 0xFFFFFFFF, false);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (isHovered(mouseX, mouseY)) {
            state = !state;
        }
    }

    private boolean isHovered(double mouseX, double mouseY) {
        return mouseX >= getX() && mouseY >= getY() && mouseY <= getY() + getHeight();
    }
}
