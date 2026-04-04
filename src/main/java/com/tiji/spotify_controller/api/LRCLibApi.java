package com.tiji.spotify_controller.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Consumer;

public class LRCLibApi {
    private static final ApiHandler API = new ApiHandler();

    public static void getLyric(SongData songData, Consumer<Lyrics> callback, Consumer<String> onFail) {
        String songName   = URLEncoder.encode(songData.raw_title, StandardCharsets.UTF_8);
        String artistName = URLEncoder.encode(songData.artist   , StandardCharsets.UTF_8);
        String albumName  = URLEncoder.encode(songData.album    , StandardCharsets.UTF_8);
        int duration = songData.duration / 1000;

        API.call(
                "https://lrclib.net/api/get?track_name=%s&artist_name=%s&duration=%d&album_name=%s".formatted(songName, artistName, duration, albumName),
                Map.of(),
                null,
                stringHttpResponse -> {
                    JsonObject response = new Gson().fromJson(stringHttpResponse.body(), JsonObject.class);

                    Lyrics lyrics;
                    try {
                        if (!response.get("syncedLyrics").isJsonNull()) {
                            lyrics = Lyrics.synced(response.get("syncedLyrics").getAsString());
                        } else {
                            lyrics = Lyrics.plain(response.get("plainLyrics").getAsString());
                        }
                    } catch (Exception e) {
                        onFail.accept("Failed to parse lyrics: " + e.getMessage());
                        return;
                    }

                    callback.accept(lyrics);
                },
                stringHttpResponse -> {
                    JsonObject response = new Gson().fromJson(stringHttpResponse.body(), JsonObject.class);

                    String error = response.get("message").getAsString();

                    onFail.accept(error);
                },
                "GET",
                ""
        );
    }
}
