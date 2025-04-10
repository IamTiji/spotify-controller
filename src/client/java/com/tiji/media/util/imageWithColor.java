package com.tiji.media.util;

import com.tiji.media.api.ImageColorExtractor;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class imageWithColor {
    public Identifier image;
    public int color;
    private static final HashMap<Identifier, Integer> cachedColors = new HashMap<>();

    public imageWithColor(NativeImage image, Identifier id) {
        this.image = id;
        if (cachedColors.containsKey(id)) {
            this.color = cachedColors.get(id);
            return;
        }
        this.color = ImageColorExtractor.getDominantColor(image);
        cachedColors.put(id, color);
    }
    public imageWithColor(int color, Identifier id) {
        this.image = id;
        this.color = color;
    }
    public imageWithColor(Identifier id) {
        this.image = id;
        this.color = cachedColors.getOrDefault(id, 0xffffffff);
    }
    public String toString() {
        return "imageWithColor{" +
                "image=" + image +
                ", color=" + Integer.toHexString(color) +
                '}';
    }
}
