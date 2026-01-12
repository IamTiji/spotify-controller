package com.tiji.spotify_controller.widgets;

import com.tiji.spotify_controller.util.SafeDrawer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class BorderedButtonWidget extends BorderlessButtonWidget {
    private static final int PADDING = 2;
    private final int width;
    private final boolean needsCentering;
    private final int labelWidth;

    public BorderedButtonWidget(Component innerText, int x, int y, Runnable action, boolean isIcon) {
        super(innerText, x, y, action, isIcon);

        if (isIcon) {
            width = BUTTON_SIZE + PADDING*2;
        } else {
            width = client.font.width(innerText) + PADDING*2;
        }

        needsCentering = false;
        labelWidth = -1;
    }

    public BorderedButtonWidget(Component innerText, int x, int y, Runnable action, boolean isIcon, int width) {
        super(innerText, x, y, action, isIcon);
        this.width = width;
        this.labelWidth = client.font.width(innerText);

        needsCentering = true;
    }

    @Override
    public void safeRender(GuiGraphics context, int mouseX, int mouseY, float delta) {
        SafeDrawer.drawOutline(context, getX(), getY(), width, BUTTON_SIZE + PADDING*2, isHovered(mouseX, mouseY) ? HOVERED_COLOR : NORMAL_COLOR);

        int x = getX() + PADDING;
        if (needsCentering) {
            x += (width - labelWidth) / 2;
        }

        context.drawString(client.font, label,
                x, getY() + LABEL_OFFSET + PADDING,
                isHovered(mouseX, mouseY) ? HOVERED_COLOR : NORMAL_COLOR,
                false);
    }

    private boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= getX() && mouseX <= getX() + width && mouseY >= getY() && mouseY <= getY() + BUTTON_SIZE + PADDING*2;
    }

    @Override
    public void onPress() {
        super.onPress();
    }
}
