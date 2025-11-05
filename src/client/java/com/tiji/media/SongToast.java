package com.tiji.media;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

public class SongToast implements Toast {
    private final Identifier cover;
    private final String artist;
    private final String title;

    private Toast.Visibility visibility;

    private static final Identifier TEXTURE = Identifier.of("media", "ui/toast.png");
    private static final long DISPLAY_DURATION_MS = 5000L;

    public SongToast(Identifier cover, String artist, String title) {
        this.cover = cover;
        this.artist = artist;
        this.title = title;

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
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, 1, 1, 160, 32, 160, 32);

        context.drawText(textRenderer, title, 35, 6, Colors.LIGHT_YELLOW, false);
        context.drawText(textRenderer, artist, 35, 18, Colors.WHITE, false);

        context.drawTexture(RenderPipelines.GUI_TEXTURED, cover, 0, 0, 0, 0, 32, 32, 32, 32);
    }
}
