package com.tiji.media;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.function.Consumer;

public class ApiCalls {
    public static void convertAccessToken(String accessToken) {
        call("https://accounts.spotify.com/api/token?grant_type=authorization_code&code=" + accessToken + "&redirect_uri=http://127.0.0.1:25566/callback",
                getAuthorizationHeader(),
                "application/x-www-form-urlencoded",
                body -> {
                    JsonObject data = new Gson().fromJson(body.body(), JsonObject.class);

                    if (!data.get("scope").getAsString().equals("user-modify-playback-state user-read-playback-state user-read-currently-playing")) return; // Authorization is modified

                    MediaClient.CONFIG.authToken(data.get("access_token").getAsString());
                    MediaClient.CONFIG.refreshToken(data.get("refresh_token").getAsString());
                    MediaClient.CONFIG.lastRefresh(System.currentTimeMillis());

                    Media.LOGGER.info("Successfully converted access token");
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
    private static void call(String endpoint, String Authorization, String ContentType, Consumer<HttpResponse<String>> consumer, String method) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .timeout(Duration.ofSeconds(10))
                .header("Authorization", Authorization);
        if (ContentType != null) request.header("Content-Type", ContentType);

        request = switch (method) {
            case "GET" -> request.GET();
            case "POST" -> request.POST(HttpRequest.BodyPublishers.ofString(""));
            case "PUT" -> request.PUT(HttpRequest.BodyPublishers.ofString(""));
            default -> request;
        };

        client.sendAsync(request.build(), HttpResponse.BodyHandlers.ofString())
                .exceptionally(e -> {
                    Media.LOGGER.error("Failed to call API: {}", e.getMessage());
                    return null;
                })
                .thenAccept(stringHttpResponse -> {
                    String body = stringHttpResponse.body();
                    JsonObject data = new Gson().fromJson(body, JsonObject.class);

                    if (data.has("reason")) {
                        if (data.get("reason").getAsString().equals("PREMIUM_REQUIRED")){
                            MinecraftClient.getInstance().getToastManager().add(
                                    new SystemToast(new SystemToast.Type(), Text.translatable("ui.media.premium_required.title"), Text.translatable("ui.media.premium_required.message"))
                            );
                        }
                    }
                    try{
                        consumer.accept(stringHttpResponse);
                    }catch (Exception e){
                        Media.LOGGER.error("Failed to consume API response: ");
                        e.printStackTrace();
                    }
                });
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