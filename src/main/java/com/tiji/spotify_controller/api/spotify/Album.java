package com.tiji.spotify_controller.api.spotify;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import java.net.URL;

public class Album {
    public @SerializedName("album_type") AlbumType albumType;
    public @SerializedName("total_tracks") int trackCount;
    public @SerializedName("name") String name;

    public @SerializedName("images") @JsonAdapter(ImageAdapter.class) URL albumCover;
}
