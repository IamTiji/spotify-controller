package com.tiji.spotify_controller.widgets;

import com.google.gson.JsonObject;
import com.tiji.spotify_controller.api.ApiCalls;
import com.tiji.spotify_controller.api.SongData;
import com.tiji.spotify_controller.api.SongDataExtractor;
import com.tiji.spotify_controller.ui.Icons;
import com.tiji.spotify_controller.util.SafeDrawer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

public class SongListItem extends SafeAbstractWidget {
    SongData song;
    private final int x, y;

    public static final int WIDTH = 300;
    public static final int HEIGHT = 50;

    private static final int IMAGE_SIZE = HEIGHT;
    private static final int MARGIN = 10;
    private static final float FADE_TIME = 0.2f;
    private static final int IMAGE_FADE_OUT = 80;

    private float fadePos = 0f;

    private static final Minecraft client = Minecraft.getInstance();

    public SongListItem(JsonObject data, int x, int y) {
        super(x, y, WIDTH, HEIGHT, Component.empty());

        song = SongDataExtractor.getDataFor(data, () -> {});
        this.x = x;
        this.y = y;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}

    @Override
    public void safeRender(GuiGraphics context, int mouseX, int mouseY, float delta) {
        boolean isHovered = mouseX >= x && mouseY >= y && mouseX < x + WIDTH && mouseY < y + HEIGHT;

        float change = delta / 10 / FADE_TIME;
        if (isHovered) fadePos += change;
        else fadePos -= change;
        fadePos = Math.clamp(fadePos, 20 / 255f, 1);

        int color = ((int) (fadePos * 255)) << 24 | 0x00FFFFFF;

        int imageColor = ((int) (255 - fadePos * IMAGE_FADE_OUT) * 0x00010101) | 0xFF000000;
        SafeDrawer.drawImage(
                context,
                song.coverImage.image,
                x, y,
                0, 0,
                IMAGE_SIZE, IMAGE_SIZE,
                imageColor
        );

        context.drawString(client.font, song.title, x + IMAGE_SIZE + MARGIN, y + MARGIN, 0xFFFFFFFF, false);
        context.drawString(client.font, song.artist, x + IMAGE_SIZE + MARGIN, y + MARGIN + 15, 0xFFFFFFFF, false);

        context.drawString(client.font, Icons.ADD_TO_QUEUE, x + IMAGE_SIZE - 8 - MARGIN, y + IMAGE_SIZE - 5 - MARGIN, color, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean isHovered = mouseX >= x && mouseY >= y && mouseX < x + WIDTH && mouseY < y + HEIGHT;
        if (isHovered && button == 0) {
            ApiCalls.addSongToQueue(song.Id);
            client.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }
        return false;
    }
}