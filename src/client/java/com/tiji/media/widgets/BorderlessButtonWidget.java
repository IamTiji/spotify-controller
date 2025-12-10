package com.tiji.media.widgets;

//
//import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
//import io.github.cottonmc.cotton.gui.widget.WButton;
//import net.minecraft.client.gui.DrawContext;
//import net.minecraft.text.Text;
//
//public class borderlessButtonWidget extends WButton {
//    public borderlessButtonWidget(Text text) {
//        super(text);
//    }
//
//    @Override
//    public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
//        int foreground = shouldRenderInDarkMode() ? 0xFFFFFFFF : 0xFF555555;
//        foreground = isHovered() || isFocused() ? foreground : foreground - 0x00555555;
//
//        ScreenDrawing.drawString(context, getLabel().asOrderedText(), alignment, x, y + ((getHeight() - 8) / 2), width, foreground);
//    }
//}
//

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Text;

public class BorderlessButtonWidget extends PressableWidget {
    private Text label;
    private final Runnable action;
    private static final int HOVERED_COLOR = 0xFFAAAAAA;
    private static final int NORMAL_COLOR = 0xFFFFFFFF;
    private static final MinecraftClient client = MinecraftClient.getInstance();
    public static final int BUTTON_SIZE = 16;
    private static final int LABEL_OFFSET = 4;

    public BorderlessButtonWidget(Text innerText, int x, int y, Runnable action) {
        super(x, y, BUTTON_SIZE, BUTTON_SIZE, Text.empty());
        this.label = innerText;
        this.action = action;
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
        return mouseX >= getX() && mouseX <= getX() + BUTTON_SIZE && mouseY >= getY() && mouseY <= getY() + BUTTON_SIZE;
    }

    public void setLabel(Text label) {
        this.label = label;
    }
}