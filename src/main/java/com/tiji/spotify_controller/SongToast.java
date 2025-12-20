package com.tiji.spotify_controller;

import com.tiji.spotify_controller.util.ImageWithColor;
import com.tiji.spotify_controller.util.TextUtils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

public class SongToast implements Toast {
    private static final int TITLE_Y = 6;
    private static final int ARTIST_Y = 18;
    private static final int TOAST_WIDTH = Toast.BASE_WIDTH;
    private static final int TOAST_HEIGHT = 32;
    private static final int MARGIN = 5;
    private static final int IMAGE_WIDTH = TOAST_HEIGHT;
    private static final long DISPLAY_DURATION_MS = 3000L;
    private static final int TEXT_WIDTH = TOAST_WIDTH - MARGIN*2 - IMAGE_WIDTH;

    private final ImageWithColor cover;
    private final Text artist;
    private final Text title;
    private Toast.Visibility visibility;

    public SongToast(ImageWithColor cover, String artist, Text title) {
        this.cover = cover;
        this.artist = TextUtils.getTrantedText(Text.of(artist), TEXT_WIDTH);
        this.title =  TextUtils.getTrantedText(title          , TEXT_WIDTH);

        this.visibility = Visibility.HIDE;
    }

    public void show(ToastManager manager) {
        manager.add(this);
    }

    @Override
    public Visibility getVisibility() {
        return this.visibility;
    }

    @Override
    public void update(ToastManager manager, long time) {
        this.visibility = DISPLAY_DURATION_MS * manager.getNotificationDisplayTimeMultiplier() <= time
                ? Visibility.HIDE
                : Visibility.SHOW;
    }

    @Override
    public void draw(DrawContext context, TextRenderer textRenderer, long startTime) {
        context.fill(0, 0, TOAST_WIDTH, TOAST_HEIGHT, cover.color);

        int labelColor = cover.shouldUseDarkUI ? Colors.WHITE : Colors.BLACK;
        context.drawText(textRenderer, title , IMAGE_WIDTH + MARGIN, TITLE_Y , labelColor, false);
        context.drawText(textRenderer, artist, IMAGE_WIDTH + MARGIN, ARTIST_Y, labelColor, false);

        context.drawTexture(RenderLayer::getGuiTextured, cover.image, 0, 0, 0, 0, IMAGE_WIDTH, TOAST_HEIGHT, IMAGE_WIDTH, TOAST_HEIGHT);
    }
}
