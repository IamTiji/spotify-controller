package com.tiji.spotify_controller;

import com.tiji.spotify_controller.api.spotify.RepeatState;
import com.tiji.spotify_controller.util.InterpolatedTime;

public class PlaybackState {
    public boolean isPlaying = false;

    public InterpolatedTime progressMs = InterpolatedTime.nonProgressing();
    public long durationMs = 0; // Reference time to calculate progressNorm

    public RepeatState repeat = RepeatState.OFF;
    public boolean shuffle = false;
    public boolean isLiked = false;

    public boolean canShuffle = false;
    public boolean canRepeat = false;
    public boolean canSkip = false;
    public boolean canGoBack = false;
    public boolean canSeek = false;

    public double getProgressNorm() {
        return (double) progressMs.getInterpolatedTime() / durationMs;
    }

    public String getProgressLabel() {
        long progress = progressMs.getInterpolatedTime();

        progress /= 1000;

        long minutes_progress = progress / 60;
        long seconds_progress = progress % 60;

        return String.format("%02d:%02d", minutes_progress, seconds_progress);
    }
}
