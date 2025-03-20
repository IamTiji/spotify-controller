package com.tiji.media.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.tiji.media.Media;
import com.tiji.media.MediaClient;
import com.tiji.media.WebGuideServer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.function.Consumer;

public class ApiCalls {
    public static void convertAccessToken(String accessToken) {
        call("https://accounts.spotify.com/api/token?grant_type=authorization_code&redirect_uri=http://localhost:25566/callback&code=" + accessToken,
                getAuthorizationHeader(),
                "application/x-www-form-urlencoded",
                body -> {
                    JsonObject data = new Gson().fromJson(body.body(), JsonObject.class);

                    MediaClient.CONFIG.authToken(data.get("access_token").getAsString());
                    MediaClient.CONFIG.refreshToken(data.get("refresh_token").getAsString());
                    MediaClient.CONFIG.lastRefresh(System.currentTimeMillis());
                }, "POST");
    }
    public static void refreshAccessToken() {
        call("https://accounts.spotify.com/api/token?grant_type=refresh_token&refresh_token=" + MediaClient.CONFIG.refreshToken(),
                getAuthorizationHeader(),
                "application/x-www-form-urlencoded",
                body -> {
                    JsonObject data = new Gson().fromJson(body.body(), JsonObject.class);

                    if (data.has("error")) {
                        Media.LOGGER.warn("Failed to refresh access token; Normally caused when developer app is deleted. {}: {}", data.get("error"), data.get("error_description"));
                        MediaClient.CONFIG.reset();
                        try {
                            WebGuideServer.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return;
                    }

                    MediaClient.CONFIG.authToken(data.get("access_token").getAsString());
                    if (data.has("refresh_token")) MediaClient.CONFIG.refreshToken(data.get("refresh_token").getAsString());
                    MediaClient.CONFIG.lastRefresh(System.currentTimeMillis());
                }, "POST");
    }
    public static void getNowPlayingTrack(Consumer<JsonObject> callback) {
        call("https://api.spotify.com/v1/me/player",
                getAuthorizationCode(),
                null,
                body -> callback.accept(new Gson().fromJson(body.body(), JsonObject.class)),
                "GET"
        );
    }
    public static void setPlaybackLoc(int position_ms) {
        call("https://api.spotify.com/v1/me/player/seek?position_ms=" + position_ms,
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
        call(uri, getAuthorizationCode(), null, body -> {}, "PUT");
    }
    public static void nextTrack() {
        call("https://api.spotify.com/v1/me/player/next", getAuthorizationCode(), null, body -> {}, "POST");
    }
    public static void previousTrack() {
        call("https://api.spotify.com/v1/me/player/previous", getAuthorizationCode(), null, body -> {}, "POST");
    }
    public static void getUserName(Consumer<String> consumer) {
        call("https://api.spotify.com/v1/me", getAuthorizationCode(), null, body -> {
            String name = new Gson().fromJson(body.body(), JsonObject.class).get("display_name").getAsString();
            consumer.accept(name);
        }, "GET");
    }
    public static void setShuffle(boolean state) {
        call("https://api.spotify.com/v1/me/player/shuffle?state=" + (state ? "true" : "false"),
                getAuthorizationCode(),
                null,
                body -> {},
                "PUT"
        );
    }
    public static void setRepeat(String state) {
        call("https://api.spotify.com/v1/me/player/repeat?state=" + state,
                getAuthorizationCode(),
                null,
                body -> {},
                "PUT"
        );
    }
    public static void isSongLiked(String trackId, Consumer<Boolean> consumer) {
        call("https://api.spotify.com/v1/me/tracks/contains?ids=" + trackId,
                getAuthorizationCode(),
                null,
                body -> {
                    consumer.accept(new Gson().fromJson(body.body(), JsonArray.class).get(0).getAsBoolean());
                },
                "GET"
        );
    }
    public static void toggleLikeSong(String trackId, boolean state) {
        call("https://api.spotify.com/v1/me/tracks?ids=" + trackId,
                getAuthorizationCode(),
                null,
                body -> {},
                state ? "PUT" : "DELETE"
        );
    }
    public static void getSearch(String query, Consumer<JsonArray> consumer) {
        call("https://api.spotify.com/v1/search?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8) + "&type=track",
                getAuthorizationCode(),
                null,
                body -> consumer.accept(new Gson().fromJson(body.body(), JsonObject.class)
                        .getAsJsonObject("tracks")
                        .getAsJsonArray("items")),
                "GET"
        );
    }
    public static void setPlayingSong(String trackId) {
        call("https://api.spotify.com/v1/me/player/play",
                getAuthorizationCode(),
                null,
                body -> {},
                "PUT",
                "{\"uris\": [\"spotify:track:" + trackId + "\"]}"
        );
    }
    public static void addSongToQueue(String trackId){
        call("https://api.spotify.com/v1/me/player/queue?uri=spotify:track:" + trackId,
                getAuthorizationCode(),
                null,
                body -> {},
                "POST"
        );
    }
    private static void call(String endpoint, String Authorization, String ContentType, Consumer<HttpResponse<String>> consumer, String method, String requestBody) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .timeout(Duration.ofSeconds(10))
                .header("Authorization", Authorization);
        if (ContentType != null) request.header("Content-Type", ContentType);

        HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.ofString(requestBody);

        request = switch (method) {
            case "GET" -> request.GET();
            case "POST" -> request.POST(publisher);
            case "PUT" -> request.PUT(publisher);
            case "DELETE" -> request.DELETE();
            default -> request;
        };

        client.sendAsync(request.build(), HttpResponse.BodyHandlers.ofString())
                .exceptionally(e -> {
                    Media.LOGGER.error("Failed to call API: {}", e.getMessage());
                    return null;
                })
                .thenAccept(stringHttpResponse -> {
                    try {
                        String responseBody = stringHttpResponse.body();
                        try {
                            if (!responseBody.isEmpty()) {
                                JsonObject data = new Gson().fromJson(responseBody, JsonObject.class);

                                if (data.has("reason")) {
                                    if (data.get("reason").getAsString().equals("PREMIUM_REQUIRED")) {
                                        MinecraftClient.getInstance().getToastManager().add(
                                                new SystemToast(new SystemToast.Type(), Text.translatable("ui.media.premium_required.title"), Text.translatable("ui.media.premium_required.message"))
                                        );
                                    }
                                }
                            }
                        } catch (JsonSyntaxException e) {}
                        if (stringHttpResponse.statusCode() >= 400) {
                            Media.LOGGER.error("Failed to call API: {} (Status: {})", responseBody, stringHttpResponse.statusCode());
                            consumer.accept(null);
                            return;
                        }
                        consumer.accept(stringHttpResponse);
                    }
                    catch (Exception e){
                        Media.LOGGER.error("Failed to consume API response: ");
                        e.printStackTrace();
                    }
                });
    }
    private static void call(String endpoint, String Authorization, String ContentType, Consumer<HttpResponse<String>> consumer, String method) {
        call(endpoint, Authorization, ContentType, consumer, method, "");
    }
    private static String getAuthorizationHeader() {
        String clientId = MediaClient.CONFIG.clientId();
        String clientSecret = MediaClient.CONFIG.clientSecret();

        String encoded = java.util.Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());

        return "Basic " + encoded;
    }

    private static String getAuthorizationCode() {
        return "Bearer " + MediaClient.CONFIG.authToken();
    }
}