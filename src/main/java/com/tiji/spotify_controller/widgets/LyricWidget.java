package com.tiji.spotify_controller.widgets;

import com.tiji.spotify_controller.Main;
import com.tiji.spotify_controller.api.Lyrics;
import com.tiji.spotify_controller.util.SafeDrawer;
import com.tiji.spotify_controller.util.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class LyricWidget extends SafeAbstractWidget {
    private Lyrics lyric;

    private static final int PAST_COLOR     = 0x44FFFFFF;
    private static final int CURRENT_COLOR  = 0xFFFFFFFF;
    private static final int UPCOMING_COLOR = 0x99FFFFFF;

    private static final int LYRIC_MARGIN = 8;
    private static final int LINE_MARGIN  = 2;

    private static final Font font = Minecraft.getInstance().font;

    public LyricWidget(Lyrics lyrics, int x, int y, int width) {
        super(x, y, width, 0, Component.empty());
        setLyric(lyrics);
    }

    public void setLyric(Lyrics lyric) {
        this.lyric = lyric;
    }

    public void updateHeight() {
        setHeight(countHeight());
    }

    private int countHeight() {
        int y = 0;
        for (int i = 0; i < lyric.lines.size(); i++) {
            int lineCount = TextUtils.warpText(lyric.lines.get(i), getWidth() - 2 * LYRIC_MARGIN).length;
            y += (font.lineHeight + LINE_MARGIN) * lineCount;

            y += LYRIC_MARGIN - LINE_MARGIN;
        }

        return y;
    }

    @Override
    public void safeRender(GuiGraphics context, int mouseX, int mouseY, float delta) {
        int y = getY();
        boolean isUpcoming = false;
        boolean isUpcomingOrCurrent = false;
        for (int i = 0; i < lyric.lines.size(); i++) {
            long time = Main.playbackState.progressMs.getInterpolatedTime();
            if (lyric.timestamps.get(i) >= time){
                isUpcoming = true;
            } else if (lyric.timestamps.size() <= i + 1 || lyric.timestamps.get(i + 1) >= time) {
                isUpcomingOrCurrent = true;
            }

            int lineColor;

            if (!isUpcoming && isUpcomingOrCurrent) lineColor = CURRENT_COLOR;
            else if (isUpcoming) lineColor = UPCOMING_COLOR;
            else lineColor = PAST_COLOR;

            String[] lines = TextUtils.warpText(lyric.lines.get(i), getWidth() - 2 * LYRIC_MARGIN);
            for (String line : lines) {
                SafeDrawer.drawString(context, font,
                    line,
                    getX(), y,
                    lineColor,
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
