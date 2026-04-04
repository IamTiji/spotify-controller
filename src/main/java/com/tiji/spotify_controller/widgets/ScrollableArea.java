package com.tiji.spotify_controller.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

//#if MC>=12109
//$$ import net.minecraft.client.input.MouseButtonEvent;
//#endif

public class ScrollableArea extends SafeAbstractWidget {
    private static final int SCROLLBAR_WIDTH = 4;
    private static final int SCROLL_SPEED = 25;

    private final int width, height, x, y;
    private final ArrayList<SafeAbstractWidget> widgets = new ArrayList<>(20);
    private float scrollBarPos;
    private int offset;
    private int contentHeight;

    public ScrollableArea(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void safeRender(GuiGraphics context, int mouseX, int mouseY, float delta) {
        float relativeScrollBarPos = scrollBarPos / contentHeight;
        int scrollBarSize          = (int) ((float) height / contentHeight * height);
        int scrollBarPos           = (int) ((height - scrollBarSize) * relativeScrollBarPos);

        offset = 0;
        if (height < contentHeight) {
            context.fill(width - SCROLLBAR_WIDTH + x, y,
                    width + x, height + y,
                    0x77FFFFFF);
            context.fill(width - SCROLLBAR_WIDTH + x, scrollBarPos + y,
                    width + x, scrollBarPos + scrollBarSize + y,
                    0xFFFFFFFF);
            offset = -(int) ((contentHeight - height) * relativeScrollBarPos);
        }

        context.enableScissor(x, y, width - SCROLLBAR_WIDTH + x, height + y);

        //#if MC>=12106
        //$$ context.pose().pushMatrix();
        //$$ context.pose().translate(x, offset + y);
        //#else
        context.pose().pushPose();
        context.pose().translate(x, offset + y, 0);
        //#endif

        synchronized (widgets) {
            for (SafeAbstractWidget widget : widgets) {
                widget.safeRender(context, mouseX - x, mouseY - offset - y, delta);
            }
        }

        //#if MC>=12106
        //$$ context.pose().popMatrix();
        //#else
        context.pose().popPose();
        //#endif

        context.disableScissor();
    }

    @Override
    public void setFocused(boolean bl) {}

    @Override
    public boolean isFocused() { return false; }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scrollBarPos = (float) Math.clamp(scrollBarPos - verticalAmount*SCROLL_SPEED, 0f, Math.max(contentHeight, 0));
        return true;
    }

    //#if MC<=12108
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (SafeAbstractWidget widget : widgets) {
            if (widget.mouseClicked(mouseX - x, mouseY - offset - y, button))
                return true;
        }
        return false;
    }
    //#else
    //$$ @Override
    //$$ public boolean mouseClicked(MouseButtonEvent event, boolean doubleClicked) {
    //$$     MouseButtonEvent relativeEvent = new MouseButtonEvent(event.x() - x, event.y() - offset - y, event.buttonInfo());
    //$$     for (SafeAbstractWidget widget : widgets) {
    //$$         if (widget.mouseClicked(relativeEvent, doubleClicked))
    //$$             return true;
    //$$     }
    //$$     return false;
    //$$ }
    //#endif

    public void addWidget(SafeAbstractWidget widget) {
        synchronized (widgets) {
            widgets.add(widget);
            contentHeight = Math.max(contentHeight, widget.getHeight() + widget.getY());
        }
    }

    public void checkHeight() {
        synchronized (widgets) {
            contentHeight = 0;
            widgets.forEach(widget ->
                    contentHeight = Math.max(contentHeight, widget.getHeight() + widget.getY()));
        }
    }

    public void clearWidgets() {
        synchronized (widgets) {
            widgets.clear();
            contentHeight = 0;
        }
    }

    @Override
    public @NotNull NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}

    @Override
    public boolean isMouseOver(double x, double y) {
        return x >= this.x && x < this.x + width && y >= this.y && y < this.y + height;
    }
}
