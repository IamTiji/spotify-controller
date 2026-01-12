package com.tiji.spotify_controller.widgets;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class BooleanToggleWidget extends SafeAbstractWidget implements ValueHolder {
    private boolean state;
    private static final Component OFF_TEXT = Component.translatable("ui.spotify_controller.toggle.off").withStyle(ChatFormatting.RED);
    private static final Component ON_TEXT = Component.translatable("ui.spotify_controller.toggle.on").withStyle(ChatFormatting.GREEN);

    public BooleanToggleWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Component.literal(""));
    }

    @Override
    public Object getValue_() {
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
    public void safeRender(GuiGraphics context, int mouseX, int mouseY, float delta) {
        Font textRenderer = Minecraft.getInstance().font;

        int yOffset = (getHeight() - textRenderer.lineHeight) / 2;
        Component text = state ? ON_TEXT : OFF_TEXT;

        if (isHovered(mouseX, mouseY))
            text = text.copy().withStyle(ChatFormatting.UNDERLINE);

        context.drawString(textRenderer, text, getX(), getY() + yOffset, 0xFFFFFFFF, false);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {}

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
