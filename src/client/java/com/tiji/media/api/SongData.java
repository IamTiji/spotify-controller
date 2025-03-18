package com.tiji.media.api;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.net.URI;

public class SongData {
    public Text title;
    public String artist;
    public Identifier coverImage = Identifier.of("media", "ui/nothing.png"); // Avoid NullPointerException
    public String durationLabel;
    public Integer duration;
    public String Id = "";
    public URI songURI;

    public String toString() {
        return "songData{" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", coverImage=" + coverImage +
                ", duration_label='" + durationLabel + '\'' +
                ", Id='" + Id + '\'' +'"' +
                ", songURI=" + songURI +
                '}';
    }
}
