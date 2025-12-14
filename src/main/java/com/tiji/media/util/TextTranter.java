package com.tiji.media.util;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Contract;

public class TextTranter {
    @Contract(value = "null, null, _ -> fail; _, _, _ -> new")
    public static Text getTrantedText(Text title, TextRenderer textRenderer, int maxWidth) {
        //if (textRenderer.getWidth(title) <= maxWidth) {
        //    return title;
        //}
        //
        //int ellipsisSize = textRenderer.getWidth("...");
        //String plainText = textRenderer.trimToWidth(title, maxWidth - ellipsisSize).;
        //return Text.of(plainText + "...");

        return title; // TODO: Please for gods sake fix trimToWidth mojang
    }
}
