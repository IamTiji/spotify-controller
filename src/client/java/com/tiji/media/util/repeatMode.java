package com.tiji.media.util;

import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class repeatMode {
    public static final String OFF = "off";
    public static final String CONTEXT = "context";
    public static final String TRACK = "track";

    public static Text getAsText(String mode) {
        final Style ICON = Style.EMPTY.withFont(Identifier.of("media", "icon"));

        return switch (mode) {
            case CONTEXT -> Text.literal("7").setStyle(ICON);
            case TRACK -> Text.literal("8").setStyle(ICON);
            default -> Text.literal("6").setStyle(ICON);
        };
    }
    public static String getNextMode(String currentMode) {
        return switch (currentMode) {
            case OFF -> CONTEXT;
            case CONTEXT -> TRACK;
            default -> OFF;
        };
    }
}
