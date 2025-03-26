package com.tiji.media.widgets;

import io.github.cottonmc.cotton.gui.widget.WTextField;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class stringInputWidget extends WTextField {
    private @Nullable Consumer<String> consumer;
    public stringInputWidget() {
        super();
    }

    @Override
    public InputResult onCharTyped(char ch) {
        InputResult result = super.onCharTyped(ch);
        if (consumer != null && !getText().isEmpty()) consumer.accept(getText());
        return result;
    }
    public void setOnCharTyped(Consumer<String> consumer) {
        this.consumer = consumer;
    }
}