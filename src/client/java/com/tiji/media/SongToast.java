package com.tiji.media;

import com.tiji.media.util.ImageWithColor;
import com.tiji.media.util.TextTranter;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

public class SongToast implements Toast {
    private final ImageWithColor cover;
    private final Text artist;
    private final Text title;
    private Toast.Visibility visibility;

    private static final long DISPLAY_DURATION_MS = 3000L;

    public SongToast(ImageWithColor cover, String artist, Text title) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        this.cover = cover;
        this.artist = TextTranter.getTrantedText(Text.of(artist), textRenderer, TEXT_WIDTH);
        this.title =  TextTranter.getTrantedText(title          , textRenderer, TEXT_WIDTH);

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
        context.fill(0, 0, 180, 32, cover.color);

        context.drawText(textRenderer, title , 35, 6 , cover.shouldUseDarkUI ? Colors.WHITE : Colors.BLACK, false);
        context.drawText(textRenderer, artist, 35, 18, cover.shouldUseDarkUI ? Colors.WHITE : Colors.BLACK, false);

        context.drawTexture(RenderLayer::getGuiTextured, cover.image, 0, 0, 0, 0, 32, 32, 32, 32);
    }
}
