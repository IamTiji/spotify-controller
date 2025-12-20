package com.tiji.spotify_controller.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Contract;

import java.util.Optional;

public class TextUtils {
    private static final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

    @Contract(value = "null, _ -> fail; _, _ -> new")
    public static Text getTrantedText(Text title, int maxWidth) {
        if (textRenderer.getWidth(title) <= maxWidth) {
            return title;
        }
        int ellipsisSize = textRenderer.getWidth("...");
        int remainingWidth = maxWidth - ellipsisSize;

        MutableText result = Text.literal("");
        title.visit((style, asString) -> {
            Text textToAppend = Text.literal(asString).setStyle(style);
            int width = textRenderer.getWidth(textToAppend);
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

    private static Text trimToWidth(String text, Style style, int maxWidth) {
        Text styledText = Text.literal(text).setStyle(style);
        int width = textRenderer.getWidth(styledText);
        if (width <= maxWidth) {
            return styledText;
        } else {
            int cutoff = (int) Math.ceil(text.length() / 2f);
            String halfString = text.substring(0, cutoff);
            MutableText halfText = Text.literal(halfString).setStyle(style);
            int halfWidth = textRenderer.getWidth(halfText);

            if (halfWidth >= maxWidth) {
                if (text.equals(halfString)) return Text.literal("");

                return trimToWidth(halfString, style, maxWidth);
            } else {
                Text otherHalf = trimToWidth(text.substring(cutoff), style, maxWidth - halfWidth);
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
                if (textRenderer.getWidth(textSoFar + currentWord.toString()) > maxWidth) {
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
