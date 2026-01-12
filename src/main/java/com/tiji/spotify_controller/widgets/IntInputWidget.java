package com.tiji.spotify_controller.widgets;

import java.util.function.Consumer;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public class IntInputWidget extends StringInputWidget {
    public IntInputWidget(Font textRenderer, int x, int y, int width, int height, Component text, Component icon, Consumer<String> action) {
        super(textRenderer, x, y, width, height, text, icon, action);
    }

    public IntInputWidget(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void insertText(String text) {
        if (text.length() != 1) return;

        char c = text.charAt(0);
        if (c >= '0' && c <= '9') {
            super.insertText(text);
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
    public Object getValue_() {
        return Integer.parseInt(getValue());
    }
}
