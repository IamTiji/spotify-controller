package com.tiji.spotify_controller.api.spotify;

import com.google.gson.annotations.SerializedName;
import com.tiji.spotify_controller.ui.Icons;
import net.minecraft.network.chat.Component;

public enum RepeatState {
    @SerializedName("off")     OFF,
    @SerializedName("context") CONTEXT,
    @SerializedName("track")   TRACK;

    public Component getAsIcon() {
        return switch (this) {
            case CONTEXT -> Icons.REPEAT_ON;
            case TRACK -> Icons.REPEAT_SINGLE;
            case OFF -> Icons.REPEAT;
        };
    }

    public RepeatState getNextMode() {
        return switch (this) {
            case OFF -> CONTEXT;
            case CONTEXT -> TRACK;
            case TRACK -> OFF;
        };
    }

    public String getName() {
        return switch (this) {
            case OFF -> "off";
            case CONTEXT -> "context";
            case TRACK -> "track";
        };
    }

    public static RepeatState getStateFromString(String state) {
        return switch (state) {
            case "off" -> OFF;
            case "context" -> CONTEXT;
            case "track" -> TRACK;

            default -> throw new IllegalArgumentException();
        };
    }
}
