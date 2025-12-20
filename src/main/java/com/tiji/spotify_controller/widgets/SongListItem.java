package com.tiji.spotify_controller.widgets;

import com.google.gson.JsonObject;
import com.tiji.spotify_controller.api.ApiCalls;
import com.tiji.spotify_controller.api.SongData;
import com.tiji.spotify_controller.api.SongDataExtractor;
import com.tiji.spotify_controller.ui.Icons;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

public class SongListItem implements Drawable, Element, Selectable {
    SongData song;
    private final int x, y;

    public static final int WIDTH = 300;
    public static final int HEIGHT = 50;

    private static final int IMAGE_SIZE = HEIGHT;
    private static final int MARGIN = 10;
    private static final float FADE_TIME = 0.2f;
    private static final int IMAGE_FADE_OUT = 80;

    private float fadePos = 0f;

    private static final MinecraftClient client = MinecraftClient.getInstance();

    public SongListItem(JsonObject data, int x, int y) {
        song = SongDataExtractor.getDataFor(data, () -> {});
        this.x = x;
        this.y = y;
    }

    @Override
    public void setFocused(boolean focused) {

    }

    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        boolean isHovered = mouseX >= x && mouseY >= y && mouseX < x + WIDTH && mouseY < y + HEIGHT;

        float change = delta / 10 / FADE_TIME;
        if (isHovered) fadePos += change;
        else fadePos -= change;
        fadePos = Math.clamp(fadePos, 20 / 255f, 1);

        int color = ((int) (fadePos * 255)) << 24 | 0x00FFFFFF;
        int imageColor = ((int) (255 - fadePos * IMAGE_FADE_OUT) * 0x00010101) | 0xFF000000;

        context.enableScissor(x, y, x+WIDTH, y+HEIGHT);

        context.drawTexture(RenderLayer::getGuiTextured, song.coverImage.image,
                x, y, 0, 0, IMAGE_SIZE, IMAGE_SIZE, 1, 1, 1, 1, imageColor);
        context.drawText(client.textRenderer, song.title, x + IMAGE_SIZE + MARGIN, y + MARGIN, 0xFFFFFFFF, false);
        context.drawText(client.textRenderer, song.artist, x + IMAGE_SIZE + MARGIN, y + MARGIN + 15, 0xFFFFFFFF, false);

        context.disableScissor();

        context.drawText(client.textRenderer, Icons.ADD_TO_QUEUE, x + IMAGE_SIZE - 8 - MARGIN, y + IMAGE_SIZE - 5 - MARGIN, color, false);
    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {}

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean isHovered = mouseX >= x && mouseY >= y && mouseX < x + WIDTH && mouseY < y + HEIGHT;
        if (isHovered && button == 0) {
            ApiCalls.addSongToQueue(song.Id);
            client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }
        return false;
    }
}