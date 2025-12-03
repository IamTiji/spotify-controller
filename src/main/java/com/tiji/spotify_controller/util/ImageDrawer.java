package com.tiji.spotify_controller.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class ImageDrawer {
    public static void drawImage(DrawContext context,
                                 Identifier sprite,
                                 int x,
                                 int y,
                                 float u,
                                 float v,
                                 int width,
                                 int height,
                                 int regionWith,
                                 int regionHeight,
                                 int textureWidth,
                                 int textureHeight) {
        //#if MC>=12102
        context.drawTexture(
                //#if MC>=12107
                //$$ net.minecraft.client.gl.RenderPipelines.GUI_TEXTURED,
                //#else
                RenderLayer::getGuiTextured,
                //#endif
                sprite,
                x, y, u, v,
                width, height,
                regionWith, regionHeight, textureWidth, textureHeight
        );
        //#else
        //$$ context.drawTexture(
        //$$         sprite,
        //$$         x, y,
        //$$         width, height, u, v,
        //$$         regionWith, regionHeight, textureWidth, textureHeight
        //$$ );
        //#endif
    }

    public static void drawImage(DrawContext context,
                                 Identifier sprite,
                                 int x,
                                 int y,
                                 float u,
                                 float v,
                                 int width,
                                 int height,
                                 int regionWith,
                                 int regionHeight,
                                 int textureWidth,
                                 int textureHeight,
                                 int tint) {
        //#if MC>=12102
        context.drawTexture(
                //#if MC>=12106
                //$$ net.minecraft.client.gl.RenderPipelines.GUI_TEXTURED,
                //#else
                RenderLayer::getGuiTextured,
                //#endif
                sprite,
                x, y, u, v,
                width, height,
                regionWith, regionHeight, textureWidth, textureHeight,
                tint
        );
        //#else
        //$$ RenderSystem.enableBlend();
        //$$ RenderSystem.defaultBlendFunc();
        //$$ float a = (tint >> 24 & 0xFF) / 255.0f;
        //$$ float r = (tint       & 0xFF) / 255.0f;
        //$$ float g = (tint >> 8  & 0xFF) / 255.0f;
        //$$ float b = (tint >> 16 & 0xFF) / 255.0f;
        //$$
        //$$ context.setShaderColor(r, g, b, a);
        //$$ context.drawTexture(
        //$$         sprite,
        //$$         x, y,
        //$$         width, height, u, v,
        //$$         regionWith, regionHeight, textureWidth, textureHeight
        //$$ );
        //$$ context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        //$$ RenderSystem.disableBlend();
        //#endif
    }

    public static void drawImage(DrawContext context,
                                 Identifier sprite,
                                 int x,
                                 int y,
                                 float u,
                                 float v,
                                 int width,
                                 int height) {
        drawImage(context, sprite, x, y, u, v, width, height, 1, 1, 1, 1);
    }

    public static void drawImage(DrawContext context,
                                 Identifier sprite,
                                 int x,
                                 int y,
                                 float u,
                                 float v,
                                 int width,
                                 int height,
                                 int color) {
        drawImage(context, sprite, x, y, u, v, width, height, 1, 1, 1, 1, color);
    }
}
