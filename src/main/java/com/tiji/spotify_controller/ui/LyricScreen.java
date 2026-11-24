package com.tiji.spotify_controller.ui;

import com.tiji.spotify_controller.Main;
import com.tiji.spotify_controller.api.LRCLibApi;
import com.tiji.spotify_controller.api.Lyrics;
import com.tiji.spotify_controller.util.SafeDrawer;
import com.tiji.spotify_controller.widgets.LyricWidget;
import com.tiji.spotify_controller.widgets.ScrollableArea;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class LyricScreen extends SecondaryBaseScreen {
    private boolean isLoading = true;
    private String failedMessage;

    private static final int MARGIN = 10;
    private static final int WIDTH = 300;

    private LyricWidget lyricWidget;
    private ScrollableArea scrollableArea;

    private boolean isDirty = false;

    @Override
    public void init() {
        scrollableArea = new ScrollableArea(MARGIN, MARGIN, WIDTH, height - MARGIN - INFO_HEIGHT);
        lyricWidget = new LyricWidget(Lyrics.empty(), 0, 0, WIDTH);
        scrollableArea.addWidget(lyricWidget);
        addRenderableWidget(scrollableArea);

        LRCLibApi.getLyric(Main.currentlyPlaying, lyrics -> {
            isLoading = false;
            isDirty = true;
            lyricWidget.setLyric(lyrics);
            scrollableArea.clearWidgets();
        }, error -> failedMessage = error);
    }

    @Override
    public void safeRender(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.safeRender(context, mouseX, mouseY, delta);

        if (isDirty) {
            lyricWidget.updateHeight();
            scrollableArea.addWidget(lyricWidget);
            scrollableArea.checkHeight();
            isDirty = false;
        }

        if (failedMessage != null) {
            bigText(context, "ui.spotify_controller.failed");
            SafeDrawer.drawString(context, font, Component.literal(failedMessage), MARGIN, MARGIN*2 + font.lineHeight*2, 0xFFFFFFFF, false);
        } else if (isLoading) {
            bigText(context, "ui.spotify_controller.loading");
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

        SafeDrawer.drawString(context, font, Component.translatable(text), MARGIN / 2, MARGIN / 2, 0xFFFFFFFF, false);

        //#if MC>=12106
        //$$ context.pose().popMatrix();
        //#else
        context.pose().popPose();
        //#endif
    }

    @Override
    public void songChangeCallback() {
        isLoading = true;
        failedMessage = null;
        scrollableArea.clearWidgets();

        LRCLibApi.getLyric(Main.currentlyPlaying, lyrics -> {
            isLoading = false;
            isDirty = true;

            lyricWidget.setLyric(lyrics);
        }, error -> failedMessage = error);
    }
}
