package com.tiji.spotify_controller.api;

import com.mojang.blaze3d.platform.NativeImage;
import com.tiji.spotify_controller.Main;
import java.util.HashMap;

public class ImageColorExtractor {
    public static final float MAX_COLOR_DIST = 0.2f;
    public static final float MAX_COLOR_DIST_SQR = MAX_COLOR_DIST * MAX_COLOR_DIST;

    public static int getDominantColor(NativeImage image) {
        HashMap<Integer, Integer> colorCount = getColorFrequency(image, Main.CONFIG.sampleSize());
        int highestScoredColor = 0;
        double highestScore = -Double.MAX_VALUE;
        Main.LOGGER.debug("Color frequencies (Found: {}): {}", colorCount.size(), colorCount);
        for (Integer color : colorCount.keySet()) {
            double score = calcWeightByArea(colorCount.getOrDefault(color, 0), Main.CONFIG.sampleSize())
                    * calcScore(color,
                    Main.CONFIG.brightnessFactor(),
                    Main.CONFIG.saturationFactor(),
                    Main.CONFIG.targetBrightness());
            Main.LOGGER.debug("Score for color {}: {}", color, score);
            if (score > highestScore) {
                highestScore = score;
                highestScoredColor = color;
            }
        }
        Main.LOGGER.debug("Dominant color: {}", highestScoredColor);
        return fixContrast(highestScoredColor);
    }

    private static int fixContrast(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8 ) & 0xFF;
        int b =  color        & 0xFF;
        double brightness = (0.2126 * r + 0.7152 * g + 0.0722 * b) / 255.0;

        if (brightness > 0.9) {
            r = (int) (r * 0.7);
            g = (int) (g * 0.7);
            b = (int) (b * 0.7);
        } else if (brightness < 0.1) {
            r = (int) (r * 1.7);
            g = (int) (g * 1.7);
            b = (int) (b * 1.7);
        }
        return (r << 16) | (g << 8) | b | (0xFF << 24);
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
                //#if MC>=12103
                int color = image.getPixel((int) (x * multiplier_width) + multiplier_width_half,
                        (int) (y * multiplier_height) + multiplier_height_half);
                //#else
                //$$ int color = image.getPixelRGBA((int) (x * multiplier_width) + multiplier_width_half,
                //$$        (int) (y * multiplier_height) + multiplier_height_half);
                //#endif
                int r = (color >> 16) & 0xFF;
                int g = (color >> 8 ) & 0xFF;
                int b = (color      ) & 0xFF;

                boolean found = false;
                for (Integer index : colorCount.keySet()) {
                    int r2 = (index >> 16) & 0xFF;
                    int g2 = (index >> 8 ) & 0xFF;
                    int b2 = (index      ) & 0xFF;

                    // Convert to linear first
                    double lr = convertToLinear(r / 255.0);
                    double lg = convertToLinear(g / 255.0);
                    double lb = convertToLinear(b / 255.0);

                    double lr2 = convertToLinear(r2 / 255.0);
                    double lg2 = convertToLinear(g2 / 255.0);
                    double lb2 = convertToLinear(b2 / 255.0);

                    // Calculate appropriate distance cap
                    double brightness = (0.299f*r2 + 0.587f*g2 + 0.114f*b2) / 255;
                    double dist_cap = (brightness  + 0.3) * MAX_COLOR_DIST;

                    double dist_sq = (lr - lr2) * (lr - lr2) + (lg - lg2) * (lg - lg2) + (lb - lb2) * (lb - lb2);

                    if (dist_sq < dist_cap) {
                        colorCount.put(index, colorCount.get(index) + 1);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    colorCount.put(color, 1);
                    Main.LOGGER.debug("New color: {}", color);
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
        Main.LOGGER.debug("color: {}, saturation: {}, brightness: {}, score: {}", color, saturation, brightness, score);
        return score;
    }

    private static double calcWeightByArea(int x, int sampleSize) {
//        double normalizedX = (double) x / (sampleSize * sampleSize);
//        double t = Math.min(2*normalizedX*normalizedX*normalizedX,1);
//        return t * t * (3.0f - 2.0f * t);
        return (double) x / (sampleSize * sampleSize) < 0.2 ? 0 : 1;
    }

    private static double convertToLinear(double color) {
        if (color <= 0.04045) {
            return color / 12.92;
        } else {
            return Math.pow((color + 0.055) / 1.055, 2.4);
        }
    }
}
