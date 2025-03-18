package com.tiji.media;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

public class SongToast implements Toast {
    private final Identifier cover;
    private final String artist;
    private final Text title;
    private Long startTime;

    private static final Identifier TEXTURE = Identifier.of("media", "ui/toast.png");

    public SongToast(Identifier cover, String artist, Text title) {
        this.cover = cover;
        this.artist = artist;
        this.title = title;
    }

    public void show(ToastManager manager) {
        manager.add(this);
    }

    @Override
    public Visibility getVisibility() {
        return System.currentTimeMillis() - this.startTime > 3000 ? Visibility.HIDE : Visibility.SHOW;
    }

    @Override
    public void update(ToastManager manager, long time) {

    }

    @Override
    public void draw(DrawContext context, TextRenderer textRenderer, long startTime) {
        if (this.startTime == null) {
            this.startTime = System.currentTimeMillis();
        }
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, 0, 0, 1, 1, 160, 32, 160, 32);

        context.drawText(textRenderer, title, 35, 6, Colors.LIGHT_YELLOW, false);
        context.drawText(textRenderer, artist, 35, 18, Colors.WHITE, false);

        context.drawTexture(RenderLayer::getGuiTextured, cover, 0, 0, 0, 0, 32, 32, 32, 32);
    }
}
