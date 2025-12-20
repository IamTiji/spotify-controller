package com.tiji.spotify_controller.widgets;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class IntInputWidget extends StringInputWidget {
    public IntInputWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text, Text icon, Consumer<String> action) {
        super(textRenderer, x, y, width, height, text, icon, action);
    }

    public IntInputWidget(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void write(String text) {
        if (text.length() != 1) return;

        char c = text.charAt(0);
        if (c >= '0' && c <= '9') {
            super.write(text);
        }
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Integer) {
            super.setValue(value);
        } else {
            throw new IllegalArgumentException("Value must be an instance of Integer");
        }
    }

    @Override
    public Object getValue() {
        return Integer.parseInt(getText());
    }
}
