package com.tiji.spotify_controller.widgets;

import com.tiji.spotify_controller.util.SafeDrawer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

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
