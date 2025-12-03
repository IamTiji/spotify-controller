package com.tiji.spotify_controller.util;

import com.tiji.spotify_controller.api.ImageColorExtractor;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class ImageWithColor {
    public Identifier image;
    public final int color;
    private static final HashMap<Identifier, Integer> cachedColors = new HashMap<>();
    public boolean shouldUseDarkUI;

    public ImageWithColor(NativeImage image, Identifier id) {
        this.image = id;
        if (cachedColors.containsKey(id)) {
            this.color = cachedColors.get(id);
            return;
        }
        this.color = ImageColorExtractor.getDominantColor(image);
        this.shouldUseDarkUI = ImageColorExtractor.shouldUseDarkMode(this.color);
        cachedColors.put(id, color);
    }
    public ImageWithColor(int color, Identifier id) {
        this.image = id;
        this.color = color;
        this.shouldUseDarkUI = ImageColorExtractor.shouldUseDarkMode(this.color);
    }
    public ImageWithColor(Identifier id) {
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
