package com.tiji.media.api;

import com.tiji.media.Media;
import com.tiji.media.MediaClient;
import net.minecraft.client.texture.NativeImage;

import java.util.HashMap;

public class ImageColorExtractor {
    public static final int MAX_COLOR_DIST = 80;
    public static final int MAX_COLOR_DIST_SQR = MAX_COLOR_DIST * MAX_COLOR_DIST;
    public static final int CALC_SIZE = 100;

    public static int getDominantColor(NativeImage image) {
        HashMap<Integer, Integer> colorCount = getColorFrequency(image);
        int highestScoredColor = 0;
        int highestScore = 0;
        Media.LOGGER.debug("Color frequencies: {}", colorCount);
        for (Integer color : colorCount.keySet()) {
            float score = colorCount.getOrDefault(color, 0) * calcScore(color, MediaClient.CONFIG.brightnessFactor(), MediaClient.CONFIG.saturationFactor());
            Media.LOGGER.debug("Score for color {}: {}", color, score);
            if (score > highestScore) {
                highestScore = (int) score;
                highestScoredColor = color;
            }
        }
        Media.LOGGER.debug("Dominant color: {}", highestScoredColor);
        return highestScoredColor;
    }

    private static HashMap<Integer, Integer> getColorFrequency(NativeImage image) {
        HashMap<Integer, Integer> colorCount = new HashMap<>();
        final float multiplier_width = (float) image.getWidth() / CALC_SIZE;
        final float multiplier_height = (float) image.getHeight() / CALC_SIZE;

        for (int x = 0; x < CALC_SIZE; x++) {
            for (int y = 0; y < CALC_SIZE; y++) {
                int color = image.getColorArgb((int) (x * multiplier_width), (int) (y * multiplier_height));
                int b = (color >> 24) & 0xFF;
                int g = (color >> 16) & 0xFF;
                int r = (color >> 8) & 0xFF;

                boolean found = false;
                for (Integer index : colorCount.keySet()) {
                    int b2 = (index >> 24) & 0xFF;
                    int g2 = (index >> 16) & 0xFF;
                    int r2 = (index >> 8) & 0xFF;
                    int dist = (int) (Math.pow(r - r2, 2) + Math.pow(g - g2, 2) + Math.pow(b - b2, 2));
                    if (dist < MAX_COLOR_DIST_SQR) {
                        colorCount.put(index, colorCount.get(index) + 1);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    colorCount.put(color, 1);
                    Media.LOGGER.debug("New color: {}", color);
                }
            }
        }
        return colorCount;
    }

    private static float calcScore(int color, float brightnessWeight, float saturationWeight) {
        int b = (color >> 24) & 0xFF;
        int g = (color >> 16) & 0xFF;
        int r = (color >> 8) & 0xFF;

        float r_norm = r / 255f;
        float g_norm = g / 255f;
        float b_norm = b / 255f;

        float brightness = 0.2126f * r_norm + 0.7152f * g_norm + 0.0722f * b_norm;

        float minc = Math.min(Math.min(r_norm, g_norm), b_norm);
        float maxc = Math.max(Math.max(r_norm, g_norm), b_norm);
        float delta = maxc - minc;

        float saturation;
        if (maxc == 0) {
            saturation = 0;
        }else{
            saturation = delta / maxc;
        }
        Media.LOGGER.debug("color: {}, brightness: {}, saturation: {}", color, brightness, saturation);
        return brightnessWeight * brightness + saturationWeight * saturation;
    }
}
