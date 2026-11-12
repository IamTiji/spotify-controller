package com.tiji.spotify_controller.api;

import com.tiji.spotify_controller.Main;
import com.tiji.spotify_controller.util.ImageWithColor;
import java.net.URL;
import java.util.Objects;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SongData {
    public Component title;
    public String raw_title;
    public String artist;
    public String main_artist;
    public ImageWithColor coverImage = new ImageWithColor(ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "ui/nothing.png")); // Avoid NPE
    public String album;
    public String durationLabel;
    public int duration;
    public String id = "";
    public URL songURI;

    public String toString() {
        return "songData{" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", coverImage=" + coverImage +
                ", duration_label='" + durationLabel + '\'' +
                ", id='" + id + '\'' +'"' +
                ", songURI=" + songURI +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SongData songData)) return false;
        return Objects.equals(title, songData.title) && Objects.equals(id, songData.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, id);
    }

    public static SongData emptyData() {
        SongData songData = new SongData();

        songData.title = Component.translatable("ui.spotify_controller.nothing_playing");
        songData.raw_title = I18n.get("ui.spotify_controller.nothing_playing");
        songData.artist = I18n.get("ui.spotify_controller.unknown_artist");
        songData.main_artist = I18n.get("ui.spotify_controller.unknown_artist");
        songData.album = "";
        songData.durationLabel = "00:00";
        songData.duration = 0;
        songData.id = "";
        songData.songURI = null;

        return songData;
    }
}
