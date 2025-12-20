package com.tiji.spotify_controller;

public class PlaybackState {
    public String progressLabel = "00:00";
    public boolean isPlaying = false;
    public double progressValue = 0;

    public String repeat = "off";
    public boolean shuffle = false;
    public boolean isLiked = false;

    public boolean canShuffle = false;
    public boolean canRepeat = false;
    public boolean canSkip = false;
    public boolean canGoBack = false;
    public boolean canSeek = false;
}
