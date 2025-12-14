package com.tiji.media.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Contract;

public class TextUtils {
    private static final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

    @Contract(value = "null, _ -> fail; _, _ -> new")
    public static Text getTrantedText(Text title, int maxWidth) {
        //if (textRenderer.getWidth(title) <= maxWidth) {
        //    return title;
        //}
        //
        //int ellipsisSize = textRenderer.getWidth("...");
        //String plainText = textRenderer.trimToWidth(title, maxWidth - ellipsisSize).;
        //return Text.of(plainText + "...");

        return title; // TODO: Please for gods sake fix trimToWidth mojang
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
