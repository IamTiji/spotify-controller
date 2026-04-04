package com.tiji.spotify_controller.widgets;

import com.tiji.spotify_controller.Main;
import com.tiji.spotify_controller.api.Lyrics;
import com.tiji.spotify_controller.util.SafeDrawer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class LyricWidget extends SafeAbstractWidget {
    private Lyrics lyric;

    private static final int LYRIC_MARGIN = 2;
    private static final Font font = Minecraft.getInstance().font;

    public LyricWidget(Lyrics lyrics, int x, int y, int width) {
        super(x, y, width, 0, Component.empty());
        setLyric(lyrics);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}

    public void setLyric(Lyrics lyric) {
        this.lyric = lyric;
        setHeight(lyric.lines.size() * (font.lineHeight + LYRIC_MARGIN));
    }

    @Override
    public void safeRender(GuiGraphics context, int mouseX, int mouseY, float delta) {
        int y = getY();
        boolean isPast = false;
        for (int i = 0; i < lyric.lines.size(); i++) {
            if (lyric.timestamps.get(i) >= Main.playbackState.progress) isPast = true;

            SafeDrawer.drawString(context, font, lyric.lines.get(i), getX(), y, isPast ? 0x77FFFFFF : 0xFFFFFFFF, false);
            y += font.lineHeight + LYRIC_MARGIN;
        }
    }
}
