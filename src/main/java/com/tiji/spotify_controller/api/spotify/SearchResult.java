package com.tiji.spotify_controller.api.spotify;

public class SearchResult {
    public record Track(TrackObject[] items) {}

    public Track tracks;
}
