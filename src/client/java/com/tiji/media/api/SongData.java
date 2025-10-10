package com.tiji.media.api;

import com.tiji.media.util.imageWithColor;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.net.URI;

public class SongData {
    public Text title;
    public String artist;
    public imageWithColor coverImage = new imageWithColor(0xffffffff, Identifier.of("media", "ui/nothing.png")); // Avoid NullPointerException
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

    public static SongData emptyData() {
        SongData songData = new SongData();

        songData.title = Text.translatable("ui.media.nothing_playing");
        songData.artist = Text.translatable("ui.media.unknown_artist").toString();
        songData.durationLabel = "00:00";
        songData.duration = 0;
        songData.Id = "";
        songData.songURI = null;

        return songData;
    }
}
