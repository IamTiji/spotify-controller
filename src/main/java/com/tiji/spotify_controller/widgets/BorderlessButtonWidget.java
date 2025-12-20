package com.tiji.spotify_controller.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Text;

public class BorderlessButtonWidget extends PressableWidget {
    protected Text label;
    protected final Runnable action;
    private final int width;
    protected static final int HOVERED_COLOR = 0xFFAAAAAA;
    protected static final int NORMAL_COLOR = 0xFFFFFFFF;
    protected static final MinecraftClient client = MinecraftClient.getInstance();
    public static final int BUTTON_SIZE = 16;
    protected static final int LABEL_OFFSET = 4;

    public BorderlessButtonWidget(Text innerText, int x, int y, Runnable action, boolean isIcon) {
        super(x, y,
                isIcon ? BUTTON_SIZE : client.textRenderer.getWidth(innerText), BUTTON_SIZE,
                Text.empty());

        this.label = innerText;
        this.action = action;
        this.width = isIcon ? BUTTON_SIZE : client.textRenderer.getWidth(innerText);
    }

    @Override
    public void onPress() {
        action.run();
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawText(client.textRenderer, label, getX(), getY() + LABEL_OFFSET, isHovered(mouseX, mouseY) ? HOVERED_COLOR : NORMAL_COLOR, false);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    private boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= getX() && mouseX <= getX() + width && mouseY >= getY() && mouseY <= getY() + BUTTON_SIZE;
    }

    public void setLabel(Text label) {
        this.label = label;
    }
}