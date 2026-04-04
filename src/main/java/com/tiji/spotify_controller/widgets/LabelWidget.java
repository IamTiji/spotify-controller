package com.tiji.spotify_controller.widgets;

import com.tiji.spotify_controller.util.SafeDrawer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import java.util.function.Consumer;

public class LabelWidget extends SafeAbstractWidget {
    private Component text;

    public LabelWidget(int x, int y, Component text) {
        super(x, y,
                Minecraft.getInstance().font.width(text),
                Minecraft.getInstance().font.lineHeight,
                text);
        this.text = text;
    }

    @Override
    public void safeRender(GuiGraphics context, int mouseX, int mouseY, float delta) {
        SafeDrawer.drawString(context, Minecraft.getInstance().font, text, getX(), getY(), 0xFFFFFFFF, false);
    }

    public Component getText() {
        return text;
    }

    public void setText(Component text) {
        this.text = text;
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput builder) {}
}
