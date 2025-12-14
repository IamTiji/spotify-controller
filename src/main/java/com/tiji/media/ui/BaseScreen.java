package com.tiji.media.ui;

import com.tiji.media.MediaClient;
import com.tiji.media.util.ImageWithColor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
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
        ImageWithColor cover = MediaClient.currentlyPlaying.coverImage;
        context.drawTexture(RenderLayer::getGuiTextured,
                Identifier.of("media", "ui/gradient.png"),
                widgetsOffset, 0,
                0, 0,
                255, height,
                255, 1,
                cover.color
        );

        widgetsOffset = (int) (-ANIMATION_AMOUNT + easeInOut(normalized) * ANIMATION_AMOUNT);

        for (Element child : children()) {
            if (child instanceof ClickableWidget clickable) {
                clickable.setX(clickable.getX() + (widgetsOffset - previousOffset));
            }
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private float easeInOut(float t) {
        return t * t * (3 - 2 * t);
    }
}