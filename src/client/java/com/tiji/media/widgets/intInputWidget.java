package com.tiji.media.widgets;

import io.github.cottonmc.cotton.gui.widget.data.InputResult;

public class intInputWidget extends stringInputWidget {
    public intInputWidget() {
        super();
    }

    @Override
    public InputResult onCharTyped(char ch) {
        if (ch >= '0' && ch <= '9') {
            return super.onCharTyped(ch);
        };
        return InputResult.IGNORED;
    }
}
