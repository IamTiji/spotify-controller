package com.tiji.spotify_controller.api.spotify;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.tiji.spotify_controller.util.InterpolatedTime;

import java.util.Map;

public class PlaybackState {
    public @SerializedName("repeat_state") RepeatState repeatState;
    public @SerializedName("shuffle_state") boolean shuffleState;
    public @SerializedName("progress_ms") InterpolatedTime progress;
    public @SerializedName("is_playing") boolean isPlaying;
    public @SerializedName("item") TrackObject track;

    public @SerializedName("actions") Map<String, Map<String, JsonElement>> allowedActions;
}
