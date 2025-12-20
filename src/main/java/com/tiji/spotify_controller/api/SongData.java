package com.tiji.spotify_controller.api;

import com.tiji.spotify_controller.util.ImageWithColor;
import com.tiji.spotify_controller.Main;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.net.URI;

public class SongData {
    public Text title;
    public String artist;
    public ImageWithColor coverImage = new ImageWithColor(0xffffffff, Identifier.of(Main.MOD_ID, "ui/nothing.png")); // Avoid NullPointerException
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

        songData.title = Text.translatable("ui.spotify_controller.nothing_playing");
        songData.artist = Text.translatable("ui.spotify_controller.unknown_artist").toString();
        songData.durationLabel = "00:00";
        songData.duration = 0;
        songData.Id = "";
        songData.songURI = null;

        return songData;
    }
}
