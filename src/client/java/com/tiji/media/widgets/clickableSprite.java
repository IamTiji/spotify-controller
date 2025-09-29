package com.tiji.media.widgets;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class clickableSprite extends WWidget {
    private @Nullable Runnable onClickEvent;
    private final Identifier texture;

    private final int width = 18;
    private final int height = 18;

    public clickableSprite(Identifier texture) {
        super();
        this.texture = texture;
    }

    public clickableSprite setOnClick(@Nullable Runnable onClickEvent) {
        this.onClickEvent = onClickEvent;
        return this;
    }

    @Override
    public InputResult onClick(int x, int y, int button) {
        if (onClickEvent != null) {
            onClickEvent.run();
            return InputResult.PROCESSED;
        }
        return InputResult.IGNORED;
    }

    public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
        ScreenDrawing.texturedRect(context, x, y, width, height, texture, 0xFFFFFFFF);
    }
}