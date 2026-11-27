package com.tiji.spotify_controller.api.spotify;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.tiji.spotify_controller.Main;
import net.minecraft.resources.ResourceLocation;

import java.net.URL;
import java.util.Objects;

public class TrackObject {
    public @SerializedName("album") Album album;
    public @SerializedName("artists") Artist[] artists;
    public @SerializedName("name") String name;

    public @SerializedName("external_url") @JsonAdapter(ExternalURLAdapter.class) URL spotifyUrl;

    public @SerializedName("duration_ms") int duration;
    public @SerializedName("explicit") boolean explicit;

    public @SerializedName("id") String id;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TrackObject that = (TrackObject) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public ResourceLocation getMcId() {
        return ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, id.toLowerCase());
    }
}
