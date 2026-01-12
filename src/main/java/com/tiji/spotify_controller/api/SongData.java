package com.tiji.spotify_controller.api;

import com.tiji.spotify_controller.util.ImageWithColor;
import com.tiji.spotify_controller.Main;
import java.net.URI;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SongData {
    public Component title;
    public String artist;
    public ImageWithColor coverImage = new ImageWithColor(ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "ui/nothing.png")); // Avoid NPE
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

        songData.title = Component.translatable("ui.spotify_controller.nothing_playing");
        songData.artist = Component.translatable("ui.spotify_controller.unknown_artist").toString();
        songData.durationLabel = "00:00";
        songData.duration = 0;
        songData.Id = "";
        songData.songURI = null;

        return songData;
    }
}
