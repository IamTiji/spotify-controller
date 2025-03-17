package com.tiji.media;

import net.minecraft.util.Identifier;

import java.net.URI;

public class SongData {
    public String title;
    public String artist;
    public Identifier coverImage = Identifier.of("media", "ui/nothing.png"); // Avoid NullPointerException
    public String progressLabel;
    public String durationLabel;
    public Double progressValue;
    public Integer duration;
    public boolean isPlaying = false;
    public String Id = "";
    public URI songURI;

    public String toString() {
        return "songData{" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", coverImage=" + coverImage +
                ", progress_label='" + progressLabel + '\'' +
                ", duration_label='" + durationLabel + '\'' +
                ", progress_value=" + progressValue +
                ", isPlaying=" + isPlaying +
                ", Id='" + Id + '\'' +'"' +
                ", songURI=" + songURI +
                '}';
    }
}
