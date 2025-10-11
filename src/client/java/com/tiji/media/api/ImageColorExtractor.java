package com.tiji.media.api;

import com.tiji.media.Media;
import com.tiji.media.MediaClient;
import net.minecraft.client.texture.NativeImage;

import java.util.HashMap;

public class ImageColorExtractor {
    public static final int MAX_COLOR_DIST = 40;
    public static final int MAX_COLOR_DIST_SQR = MAX_COLOR_DIST * MAX_COLOR_DIST;

    public static int getDominantColor(NativeImage image) {
        HashMap<Integer, Integer> colorCount = getColorFrequency(image, MediaClient.CONFIG.sampleSize());
        int highestScoredColor = 0;
        double highestScore = -Double.MAX_VALUE;
        Media.LOGGER.debug("Color frequencies: {}", colorCount);
        for (Integer color : colorCount.keySet()) {
            double score = calcWeightByArea(colorCount.getOrDefault(color, 0), MediaClient.CONFIG.sampleSize())
                    * calcScore(color,
                    MediaClient.CONFIG.brightnessFactor(),
                    MediaClient.CONFIG.saturationFactor(),
                    MediaClient.CONFIG.targetBrightness());
            Media.LOGGER.debug("Score for color {}: {}", color, score);
            if (score > highestScore) {
                highestScore = score;
                highestScoredColor = color;
            }
        }
        Media.LOGGER.debug("Dominant color: {}", highestScoredColor);
        return highestScoredColor;
    }

    public static boolean shouldUseDarkMode(int dominantColor) {
        int r = (dominantColor >> 16) & 0xFF;
        int g = (dominantColor >> 8) & 0xFF;
        int b = dominantColor & 0xFF;
        double brightness = (0.2126 * r + 0.7152 * g + 0.0722 * b) / 255.0;
        return brightness < 0.5;
    }

    private static HashMap<Integer, Integer> getColorFrequency(NativeImage image, int sampleSize) {
        HashMap<Integer, Integer> colorCount = new HashMap<>();
        final float multiplier_width = (float) image.getWidth() / sampleSize;
        final float multiplier_height = (float) image.getHeight() / sampleSize;
        final int multiplier_width_half = image.getWidth() / sampleSize / 2;
        final int multiplier_height_half = image.getHeight() / sampleSize / 2;

        for (int x = 0; x < sampleSize; x++) {
            for (int y = 0; y < sampleSize; y++) {
                int color = image.getColorArgb((int) (x * multiplier_width) + multiplier_width_half,
                        (int) (y * multiplier_height) + multiplier_height_half);
                int r = (color << 24) & 0xFF;
                int g = (color << 16) & 0xFF;
                int b = (color << 8 ) & 0xFF;

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

    private static float calcScore(int color, float brightnessWeight, float saturationWeight, int target_brightness) {
        final float TARGET_BRIGHTNESS = target_brightness / 100f;

        int b = (color >> 24) & 0xFF;
        int g = (color >> 16) & 0xFF;
        int r = (color >> 8) & 0xFF;

        float r_norm = r / 255f;
        float g_norm = g / 255f;
        float b_norm = b / 255f;

        float brightness = 0.299f*r_norm + 0.587f*g_norm + 0.114f*b_norm;

        float minc = Math.min(Math.min(r_norm, g_norm), b_norm);
        float maxc = Math.max(Math.max(r_norm, g_norm), b_norm);
        float delta = maxc - minc;

        float saturation;
        if (maxc == 0) {
            saturation = 0;
        } else {
            saturation = delta / maxc;
        }
        saturation = saturation * (1 - Math.abs(brightness * 2 - 1));

        float score = saturationWeight * saturation
                + (1 - Math.abs(brightness - TARGET_BRIGHTNESS)) * brightnessWeight;
        Media.LOGGER.debug("color: {}, saturation: {}, brightness: {}, score: {}", color, saturation, brightness, score);
        return score;
    }

    private static double calcWeightByArea(int x, int sampleSize) {
        double normalizedX = (double) x / (sampleSize * sampleSize);
        double t = Math.min(1.4*normalizedX*normalizedX*normalizedX,1);
        return t * t * (3.0f - 2.0f * t);
    }
}
