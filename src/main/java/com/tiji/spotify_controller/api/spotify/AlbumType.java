package com.tiji.spotify_controller.api.spotify;

import com.google.gson.annotations.SerializedName;

public enum AlbumType {
    @SerializedName("album")       ALBUM,
    @SerializedName("single")      SINGLE,
    @SerializedName("compilation") COMPILATION
}
