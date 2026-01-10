package com.tiji.spotify_controller.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class BorderlessButtonWidget extends AbstractButton {
    protected Component label;
    protected final Runnable action;
    private final int width;
    protected static final int HOVERED_COLOR = 0xFFAAAAAA;
    protected static final int NORMAL_COLOR = 0xFFFFFFFF;
    protected static final Minecraft client = Minecraft.getInstance();
    public static final int BUTTON_SIZE = 16;
    protected static final int LABEL_OFFSET = 4;

    public BorderlessButtonWidget(Component innerText, int x, int y, Runnable action, boolean isIcon) {
        super(x, y,
                isIcon ? BUTTON_SIZE : client.font.width(innerText), BUTTON_SIZE,
                Component.empty());

        this.label = innerText;
        this.action = action;
        this.width = isIcon ? BUTTON_SIZE : client.font.width(innerText);
    }

    @Override
    public void onPress() {
        action.run();
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        context.drawString(client.font, label, getX(), getY() + LABEL_OFFSET, isHovered(mouseX, mouseY) ? HOVERED_COLOR : NORMAL_COLOR, false);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {}

    private boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= getX() && mouseX <= getX() + width && mouseY >= getY() && mouseY <= getY() + BUTTON_SIZE;
    }

    public void setLabel(Component label) {
        this.label = label;
    }
}