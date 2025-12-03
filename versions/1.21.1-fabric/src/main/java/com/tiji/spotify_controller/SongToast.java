package com.tiji.spotify_controller;

import com.tiji.spotify_controller.util.ImageWithColor;
import com.tiji.spotify_controller.util.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

public class SongToast implements Toast {
    private static final int TITLE_Y = 6;
    private static final int ARTIST_Y = 18;
    private static final int TOAST_WIDTH = 160; // Hard-coded until 1.21.2
    private static final int TOAST_HEIGHT = 32;
    private static final int MARGIN = 5;
    private static final int IMAGE_WIDTH = TOAST_HEIGHT;
    private static final long DISPLAY_DURATION_MS = 3000L;
    private static final int TEXT_WIDTH = TOAST_WIDTH - MARGIN*2 - IMAGE_WIDTH;

    private final ImageWithColor cover;
    private final Text artist;
    private final Text title;

    public SongToast(ImageWithColor cover, String artist, Text title) {
        this.cover = cover;
        this.artist = TextUtils.getTrantedText(Text.of(artist), TEXT_WIDTH);
        this.title =  TextUtils.getTrantedText(title          , TEXT_WIDTH);
    }

    public void show(ToastManager manager) {
        manager.add(this);
    }

    @Override
    public Visibility draw(DrawContext context, ToastManager manager, long timePast) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        context.fill(0, 0, TOAST_WIDTH, TOAST_HEIGHT, cover.color);

        int labelColor = cover.shouldUseDarkUI ? Colors.WHITE : Colors.BLACK;
        context.drawText(textRenderer, title , IMAGE_WIDTH + MARGIN, TITLE_Y , labelColor, false);
        context.drawText(textRenderer, artist, IMAGE_WIDTH + MARGIN, ARTIST_Y, labelColor, false);

        context.drawTexture(cover.image, 0, 0, 0, 0, IMAGE_WIDTH, TOAST_HEIGHT, IMAGE_WIDTH, TOAST_HEIGHT);

        return DISPLAY_DURATION_MS * manager.getNotificationDisplayTimeMultiplier() <= timePast
                ? Visibility.HIDE
                : Visibility.SHOW;
    }
}