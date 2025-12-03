package com.tiji.spotify_controller.ui;

import com.tiji.spotify_controller.Main;
import com.tiji.spotify_controller.util.ImageDrawer;
import com.tiji.spotify_controller.util.ImageWithColor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BaseScreen extends Screen {
    protected float totalTime = 0f;
    protected int widgetsOffset = -100;
    protected static final int ANIMATION_AMOUNT = 100;
    private static final float animationTime = 0.5f;

    public BaseScreen(boolean animate) {
        super(Text.of(""));

        if (!animate) {
            totalTime += animationTime;
            widgetsOffset = 0;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        totalTime += delta / 10f;
        float normalized = Math.min(totalTime, animationTime) / animationTime;
        int previousOffset = widgetsOffset;

        // Glow
        ImageWithColor cover = Main.currentlyPlaying.coverImage;
        int color = cover.color;

        //#if MC>=12106
        //$$ // In this version, background darkening seems to be broken, so we need to do it ourselves
        //$$ final float darkenAmount = 1 - (1/64f);
        //$$ final int mask = 0xFF000000;
        //$$ int r = (int) (((color >> 16) & 0xff) * darkenAmount);
        //$$ int g = (int) (((color >> 8)  & 0xff) * darkenAmount);
        //$$ int b = (int) (( color        & 0xff) * darkenAmount);
        //$$ color &= mask;
        //$$ color |= r << 16 | g << 8 | b;
        //#endif

        ImageDrawer.drawImage(
                context,
                Identifier.of(Main.MOD_ID, "ui/gradient.png"),
                widgetsOffset, 0,
                0, 0,
                255, height,
                255, 1,
                255, 1,
                color
        );

        widgetsOffset = (int) (-ANIMATION_AMOUNT + easeInOut(normalized) * ANIMATION_AMOUNT);

        for (Element child : children()) {
            if (child instanceof Widget widget) {
                widget.setX(widget.getX() + (widgetsOffset - previousOffset));
            }
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private float easeInOut(float t) {
        return t * t * (3 - 2 * t);
    }
}