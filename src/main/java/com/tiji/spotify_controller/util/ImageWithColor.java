package com.tiji.spotify_controller.util;

import com.mojang.blaze3d.platform.NativeImage;
import com.tiji.spotify_controller.api.ImageColorExtractor;
import java.util.HashMap;
import net.minecraft.resources.ResourceLocation;

public class ImageWithColor {
    public ResourceLocation image;
    public final int color;
    private static final HashMap<ResourceLocation, Integer> cachedColors = new HashMap<>();
    public boolean shouldUseDarkUI;

    public ImageWithColor(NativeImage image, ResourceLocation id) {
        this.image = id;
        if (cachedColors.containsKey(id)) {
            this.color = cachedColors.get(id);
            return;
        }
        this.color = ImageColorExtractor.getDominantColor(image);
        this.shouldUseDarkUI = ImageColorExtractor.shouldUseDarkMode(this.color);
        cachedColors.put(id, color);
    }
    public ImageWithColor(int color, ResourceLocation id) {
        this.image = id;
        this.color = color;
        this.shouldUseDarkUI = ImageColorExtractor.shouldUseDarkMode(this.color);
    }
    public ImageWithColor(ResourceLocation id) {
        this.image = id;
        this.color = cachedColors.getOrDefault(id, 0xffEFE4B0);
        this.shouldUseDarkUI = ImageColorExtractor.shouldUseDarkMode(this.color);
    }

    public String toString() {
        return "imageWithColor{" +
                "image=" + image +
                ", color=" + Integer.toHexString(color) +
                '}';
    }
}
