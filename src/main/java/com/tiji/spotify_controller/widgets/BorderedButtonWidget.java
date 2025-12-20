package com.tiji.spotify_controller.widgets;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class BorderedButtonWidget extends BorderlessButtonWidget {
    private static final int PADDING = 2;
    private final int width;
    private final boolean needsCentering;
    private final int labelWidth;

    public BorderedButtonWidget(Text innerText, int x, int y, Runnable action, boolean isIcon) {
        super(innerText, x, y, action, isIcon);

        if (isIcon) {
            width = BUTTON_SIZE + PADDING*2;
        } else {
            width = client.textRenderer.getWidth(innerText) + PADDING*2;
        }

        needsCentering = false;
        labelWidth = -1;
    }

    public BorderedButtonWidget(Text innerText, int x, int y, Runnable action, boolean isIcon, int width) {
        super(innerText, x, y, action, isIcon);
        this.width = width;
        this.labelWidth = client.textRenderer.getWidth(innerText);

        needsCentering = true;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawBorder(getX(), getY(), width, BUTTON_SIZE + PADDING*2, isHovered(mouseX, mouseY) ? HOVERED_COLOR : NORMAL_COLOR);

        int x = getX() + PADDING;
        if (needsCentering) {
            x += (width - labelWidth) / 2;
        }

        context.drawText(client.textRenderer, label,
                x, getY() + LABEL_OFFSET + PADDING,
                isHovered(mouseX, mouseY) ? HOVERED_COLOR : NORMAL_COLOR,
                false);
    }

    private boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= getX() && mouseX <= getX() + width && mouseY >= getY() && mouseY <= getY() + BUTTON_SIZE + PADDING*2;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (isHovered((int) mouseX, (int) mouseY)) {
            this.onPress();
        }
    }
}
