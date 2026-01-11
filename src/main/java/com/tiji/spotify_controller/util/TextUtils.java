package com.tiji.spotify_controller.util;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Contract;

import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public class TextUtils {
    private static final Font textRenderer = Minecraft.getInstance().font;
    
    //#if MC<=12108
    public static final ResourceLocation DEFAULT = Style.DEFAULT_FONT;
    //#else
    //$$ public static final net.minecraft.network.chat.FontDescription DEFAULT = net.minecraft.network.chat.FontDescription.DEFAULT;
    //#endif
    
    @Contract(value = "null, _ -> fail; _, _ -> new")
    public static Component getTrantedText(Component title, int maxWidth) {
        if (textRenderer.width(title) <= maxWidth) {
            return title;
        }
        int ellipsisSize = textRenderer.width("...");
        int remainingWidth = maxWidth - ellipsisSize;

        MutableComponent result = Component.literal("");
        title.visit((style, asString) -> {
            Component textToAppend = Component.literal(asString).setStyle(style);
            int width = textRenderer.width(textToAppend);
            if (width <= remainingWidth) {
                result.append(textToAppend);
                return Optional.empty();
            } else {
                result.append(trimToWidth(asString, style, remainingWidth));
                return Optional.of(new Object());
            }
        }, Style.EMPTY);

        return result.append("...");
    }

    private static Component trimToWidth(String text, Style style, int maxWidth) {
        Component styledText = Component.literal(text).setStyle(style);
        int width = textRenderer.width(styledText);
        if (width <= maxWidth) {
            return styledText;
        } else {
            int cutoff = (int) Math.ceil(text.length() / 2f);
            String halfString = text.substring(0, cutoff);
            MutableComponent halfText = Component.literal(halfString).setStyle(style);
            int halfWidth = textRenderer.width(halfText);

            if (halfWidth >= maxWidth) {
                if (text.equals(halfString)) return Component.literal("");

                return trimToWidth(halfString, style, maxWidth);
            } else {
                Component otherHalf = trimToWidth(text.substring(cutoff), style, maxWidth - halfWidth);
                return halfText.append(otherHalf);
            }
        }
    }

    public static String[] warpText(String text, int maxWidth) {
        StringBuilder sb = new StringBuilder();
        StringBuilder textSoFar = new StringBuilder();
        StringBuilder currentWord = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            currentWord.append(c);
            if (Character.isWhitespace(c)) {
                if (textRenderer.width(textSoFar + currentWord.toString()) > maxWidth) {
                    sb.append(textSoFar).append("\n");
                    textSoFar.setLength(0);
                }
                textSoFar.append(currentWord);
                currentWord.setLength(0);
            }
        }
        sb.append(textSoFar);
        return sb.toString().split("\n");
    }
}
