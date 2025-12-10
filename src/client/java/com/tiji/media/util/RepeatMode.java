package com.tiji.media.util;

import com.tiji.media.ui.Icons;
import net.minecraft.text.Text;

public class RepeatMode {
    public static final String OFF = "off";
    public static final String CONTEXT = "context";
    public static final String TRACK = "track";

    public static Text getAsText(String mode) {
        return switch (mode) {
            case CONTEXT -> Icons.REPEAT_ON;
            case TRACK -> Icons.REPEAT_SINGLE;
            default -> Icons.REPEAT;
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
