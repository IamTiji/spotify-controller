package com.tiji.spotify_controller.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tiji.spotify_controller.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;

import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SpotifyApi {
    private static final ApiHandler API = new ApiHandler() {
        @Override
        protected void call(String endpoint,
                                   Map<String, String> headers,
                                   String ContentType,
                                   Consumer<HttpResponse<String>> onSuccess,
                                   String method,
                                   String requestBody) {
            API.call(endpoint, headers, ContentType, onSuccess, result -> {
                JsonObject errorData = new Gson().fromJson(result.body(), JsonObject.class);

                if (!handleError(errorData)) {
                    Main.LOGGER.error("Error response: {}", errorData);
                }
            }, method, requestBody);
        }
    };
    
    private static final List<String> REQUIRED_SCOPES = List.of(
            "user-read-playback-state",
            "user-modify-playback-state",
            "user-read-currently-playing",
            "user-read-private",
            "user-library-read",
            "user-library-modify"
    );

    public static void convertAccessToken(String accessToken) {
        API.call("https://accounts.spotify.com/api/token?grant_type=authorization_code&redirect_uri=http://127.0.0.1:25566/callback&code=" + accessToken,
                getAuthorizationHeader(),
                "application/x-www-form-urlencoded",
                body -> {
                    JsonObject data = new Gson().fromJson(body.body(), JsonObject.class);

                    verifyToken(data);

                    Main.CONFIG.authToken(data.get("access_token").getAsString());
                    Main.CONFIG.refreshToken(data.get("refresh_token").getAsString());
                    Main.CONFIG.lastRefresh(System.currentTimeMillis());

                    getSubscription();
                    SongDataExtractor.reloadData(true, () -> {}, () -> {}, () -> {});
                }, "POST");
    }

    private static void verifyToken(JsonObject data) {
        boolean valid = true;
        String scopes = data.get("scope").getAsString();
        for (String scope : REQUIRED_SCOPES) {
            valid &= scopes.contains(scope);
        }
        if (!valid) {
            throw new RuntimeException("Invalid token! (probably modified auth)");
        }
    }

    public static void refreshAccessToken() {
        API.call("https://accounts.spotify.com/api/token?grant_type=refresh_token&refresh_token=" + Main.CONFIG.refreshToken(),
                getAuthorizationHeader(),
                "application/x-www-form-urlencoded",
                body -> {
                    JsonObject data = new Gson().fromJson(body.body(), JsonObject.class);

                    if (data.has("error")) {
                        Main.LOGGER.warn("Failed to refresh access token; Normally caused when developer app is deleted. {}: {}", data.get("error"), data.get("error_description"));
                        Main.CONFIG.resetConnection();
                        return;
                    }

                    verifyToken(data);

                    Main.CONFIG.authToken(data.get("access_token").getAsString());
                    if (data.has("refresh_token")) Main.CONFIG.refreshToken(data.get("refresh_token").getAsString());
                    Main.CONFIG.lastRefresh(System.currentTimeMillis());

                    getSubscription();
                }, "POST");
    }

    public static void getNowPlayingTrack(Consumer<JsonObject> callback) {
        API.call("https://api.spotify.com/v1/me/player",
                getAuthorizationCode(),
                null,
                body -> callback.accept(new Gson().fromJson(body.body(), JsonObject.class)),
                "GET"
        );
    }

    public static void setPlaybackLoc(int position_ms) {
        API.call("https://api.spotify.com/v1/me/player/seek?position_ms=" + position_ms,
                getAuthorizationCode(),
                null,
                body -> {},
                "PUT"
        );
    }

    public static void playPause(boolean state) {
        String uri;
        if (state) {
            uri = "https://api.spotify.com/v1/me/player/play";
        }else {
            uri = "https://api.spotify.com/v1/me/player/pause";
        }
        API.call(uri, getAuthorizationCode(), null, body -> {}, "PUT");
    }

    public static void nextTrack() {
        if (!Main.playbackState.canSkip) {
            Main.showNotAllowedToast();
            return;
        }
        API.call("https://api.spotify.com/v1/me/player/next", getAuthorizationCode(), null, body -> {}, "POST");
    }

    public static void previousTrack() {
        if (!Main.playbackState.canGoBack) {
            Main.showNotAllowedToast();
            return;
        }
        API.call("https://api.spotify.com/v1/me/player/previous", getAuthorizationCode(), null, body -> {}, "POST");
    }

    public static void getUserName(Consumer<String> consumer) {
        API.call("https://api.spotify.com/v1/me", getAuthorizationCode(), null, body -> {
            String name = new Gson().fromJson(body.body(), JsonObject.class).get("display_name").getAsString();
            consumer.accept(name);
        }, "GET");
    }

    public static void getSubscription() {
        API.call("https://api.spotify.com/v1/me", getAuthorizationCode(), null, body -> {
            String name = new Gson().fromJson(body.body(), JsonObject.class).get("product").getAsString();
            Main.isPremium = name.equals("premium");
        }, "GET");
    }

    public static void setShuffle(boolean state) {
        if (!Main.playbackState.canShuffle) {
            Main.showNotAllowedToast();
            return;
        }
        API.call("https://api.spotify.com/v1/me/player/shuffle?state=" + (state ? "true" : "false"),
                getAuthorizationCode(),
                null,
                body -> {},
                "PUT"
        );
    }

    public static void setRepeat(String state) {
        if (!Main.playbackState.canRepeat) {
            Main.showNotAllowedToast();
            return;
        }
        API.call("https://api.spotify.com/v1/me/player/repeat?state=" + state,
                getAuthorizationCode(),
                null,
                body -> {},
                "PUT"
        );
    }

    private static boolean cachedLikeStatus;
    private static String cachedSongId;
    private static long cachedLikeStatusTime;

    private static final int LIKE_CACHE_LIFETIME = 15000;

    public static void isSongLiked(String trackId, Consumer<Boolean> consumer) {
        if (System.currentTimeMillis() - cachedLikeStatusTime < LIKE_CACHE_LIFETIME &&
                cachedSongId != null &&
                cachedSongId.equals(trackId)) {
            consumer.accept(cachedLikeStatus);
        }

        API.call("https://api.spotify.com/v1/me/tracks/contains?ids=" + trackId,
                getAuthorizationCode(),
                null,
                body -> {
                    cachedLikeStatus = new Gson().fromJson(body.body(), JsonArray.class).get(0).getAsBoolean();
                    cachedSongId = trackId;
                    cachedLikeStatusTime = System.currentTimeMillis();

                    consumer.accept(cachedLikeStatus);
                }
                , "GET"
        );
    }

    public static void toggleLikeSong(String trackId, boolean state) {
        API.call("https://api.spotify.com/v1/me/tracks?ids=" + trackId,
                getAuthorizationCode(),
                null,
                body -> {},
                state ? "PUT" : "DELETE"
        );
    }

    public static void getSearch(String query, Consumer<JsonArray> consumer) {
        API.call("https://api.spotify.com/v1/search?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8) + "&type=track",
                getAuthorizationCode(),
                null,
                body ->
                        consumer.accept(new Gson().fromJson(body.body(), JsonObject.class)
                            .getAsJsonObject("tracks")
                            .getAsJsonArray("items")),
                "GET"
        );
    }

    public static void setPlayingSong(String trackId) {
        API.call("https://api.spotify.com/v1/me/player/play",
                getAuthorizationCode(),
                body -> {},
                "PUT",
                "{\"uris\": [\"spotify:track:" + trackId + "\"]}"
        );
    }

    public static void addSongToQueue(String trackId) {
        API.call("https://api.spotify.com/v1/me/player/queue?uri=spotify:track:" + trackId,
                getAuthorizationCode(),
                null,
                body -> {},
                "POST"
        );
    }

    private static String getAuthorizationHeader() {
        String clientId = Main.CONFIG.clientId();
        String clientSecret = Main.CONFIG.clientSecret();

        String encoded = java.util.Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());

        return "Basic " + encoded;
    }

    private static String getAuthorizationCode() {
        return "Bearer " + Main.CONFIG.authToken();
    }

    private static final List<String> handledErrors = List.of("NO_ACTIVE_DEVICE", "PREMIUM_REQUIRED");
    protected static boolean handleError(JsonObject data) {
        if (!data.get("error").getAsJsonObject().has("reason")) return false;
        String reason = data.get("error").getAsJsonObject().get("reason").getAsString();

        if (handledErrors.contains(reason)) {
            Minecraft.getInstance().getToastManager().addToast(
                    new SystemToast(new SystemToast.SystemToastId(), Component.empty(), Component.translatable("api.spotify_controller.error."+reason))
            );
            return true;
        } else {
            Main.LOGGER.error("Unhandled error: {}", reason);
            return false;
        }
    }
}