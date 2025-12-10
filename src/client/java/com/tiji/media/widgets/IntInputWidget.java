package com.tiji.media.widgets;

import io.github.cottonmc.cotton.gui.widget.data.InputResult;

public class IntInputWidget extends StringInputWidget {
    public IntInputWidget() {
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
