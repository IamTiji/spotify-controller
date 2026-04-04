package com.tiji.spotify_controller.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

//#if MC>=12109
//$$ import net.minecraft.client.input.MouseButtonEvent;
//#endif

public abstract class SafeAbstractWidget extends AbstractWidget {
    public SafeAbstractWidget(int i, int j, int k, int l, Component component) {
        super(i, j, k, l, component);
    }

    public abstract void safeRender(GuiGraphics context, int mouseX, int mouseY, float delta);

    @Override
    //#if MC<=12111
    protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f)
    //#else
    //$$ protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int i, int j, float f)
    //#endif
    {
        safeRender(guiGraphics, i, j, f);
    }

    //#if MC>=12109
    //$$ public void onClick(double mouseX, double mouseY) {}
    //$$ public void onRelease(double mouseX, double mouseY) {}
    //$$
    //$$ @Override
    //$$ public void onClick(MouseButtonEvent event, boolean isDoubleClick) {
    //$$     super.onClick(event, isDoubleClick);
    //$$     onClick(event.x(), event.y());
    //$$ }
    //$$
    //$$ @Override
    //$$ public void onRelease(MouseButtonEvent event) {
    //$$     super.onRelease(event);
    //$$     onRelease(event.x(), event.y());
    //$$ }
    //#endif
}