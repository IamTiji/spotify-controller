package com.tiji.spotify_controller.widgets;

import com.tiji.spotify_controller.Main;
import com.tiji.spotify_controller.api.Lyrics;
import com.tiji.spotify_controller.util.SafeDrawer;
import com.tiji.spotify_controller.util.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class LyricWidget extends SafeAbstractWidget {
    private Lyrics lyric;

    private static final int LYRIC_MARGIN = 8;
    private static final int LINE_MARGIN  = 2;
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
            if (lyric.timestamps.get(i) >= Main.playbackState.progressMs.getInterpolatedTime()) isPast = true;

            String[] lines = TextUtils.warpText(lyric.lines.get(i), getWidth() - 2 * LYRIC_MARGIN);
            for (String line : lines) {
                SafeDrawer.drawString(context, font,
                    line,
                    getX(), y,
                    isPast ? 0x77FFFFFF : 0xFFFFFFFF,
                    false);
                y += font.lineHeight + LINE_MARGIN;
            }
            y += LYRIC_MARGIN - LINE_MARGIN;
        }
        if (getHeight() != y) {
            if (!lyric.lines.getLast().isBlank()) y += font.lineHeight;
            setHeight(y);
        }
    }
}
