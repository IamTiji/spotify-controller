package com.tiji.spotify_controller.ui;

import com.tiji.spotify_controller.Main;
import com.tiji.spotify_controller.api.LRCLibApi;
import com.tiji.spotify_controller.api.Lyrics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class LyricScreen extends SecondaryBaseScreen {
    private Lyrics lyric;
    private String failedMessage;

    private static final int MARGIN = 8;
    private static final int LYRIC_MARGIN = 2;

    public LyricScreen() {
        super();

        LRCLibApi.getLyric(Main.currentlyPlaying, lyrics -> lyric = lyrics, error -> failedMessage = error);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        if (failedMessage != null) {
            bigText(context, "ui.spotify_controller.failed");
            context.drawString(font, Component.literal(failedMessage), MARGIN, MARGIN*2 + font.lineHeight*2, 0xFFFFFFFF, false);

            return;
        } else if (lyric == null) {
            bigText(context, "ui.spotify_controller.loading");

            return;
        }

        int y = MARGIN;
        boolean isPast = false;
        for (int i = 0; i < lyric.lines.size(); i++) {
            if (lyric.timestamps.get(i) >= Main.playbackState.progress) isPast = true;

            context.drawString(font, lyric.lines.get(i), MARGIN, y, isPast ? 0x77FFFFFF : 0xFFFFFFFF, false);
            y += font.lineHeight + LYRIC_MARGIN;
        }
    }

    private void bigText(GuiGraphics context, String text) {
        //#if MC>=12106
        //$$ context.pose().pushMatrix();
        //$$ context.pose().scale(2, 2);
        //#else
        context.pose().pushPose();
        context.pose().scale(2, 2, 2);
        //#endif

        context.drawString(font, Component.translatable(text), MARGIN / 2, MARGIN / 2, 0xFFFFFFFF, false);

        //#if MC>=12106
        //$$ context.pose().popMatrix();
        //#else
        context.pose().popPose();
        //#endif
    }
}
