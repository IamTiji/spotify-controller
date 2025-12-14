package com.tiji.media.widgets;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class IntInputWidget extends StringInputWidget {
    public IntInputWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text, Text icon, Consumer<String> action) {
        super(textRenderer, x, y, width, height, text, icon, action);
    }

    @Override
    public void write(String text) {
        if (text.length() != 1) return;

        char c = text.charAt(0);
        if (c >= '0' && c <= '9') {
            super.write(text);
        }
    }
}
