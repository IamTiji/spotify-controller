package com.tiji.spotify_controller.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tiji.spotify_controller.Main;
import com.tiji.spotify_controller.WebGuideServer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class ApiCalls {
    private static final HttpClient client = HttpClient.newHttpClient();

    private static final List<String> REQUIRED_SCOPES = List.of(
            "user-read-playback-state",
            "user-modify-playback-state",
            "user-read-currently-playing",
            "user-read-private",
            "user-library-read",
            "user-library-modify"
    );

    public static void convertAccessToken(String accessToken) {
        call("https://accounts.spotify.com/api/token?grant_type=authorization_code&redirect_uri=http://127.0.0.1:25566/callback&code=" + accessToken,
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
        call("https://accounts.spotify.com/api/token?grant_type=refresh_token&refresh_token=" + Main.CONFIG.refreshToken(),
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
        if (!Main.playbackState.canSkip) {
            Main.showNotAllowedToast();
            return;
        }
        call("https://api.spotify.com/v1/me/player/next", getAuthorizationCode(), null, body -> {}, "POST");
    }
    public static void previousTrack() {
        if (!Main.playbackState.canGoBack) {
            Main.showNotAllowedToast();
            return;
        }
        call("https://api.spotify.com/v1/me/player/previous", getAuthorizationCode(), null, body -> {}, "POST");
    }
    public static void getUserName(Consumer<String> consumer) {
        call("https://api.spotify.com/v1/me", getAuthorizationCode(), null, body -> {
            String name = new Gson().fromJson(body.body(), JsonObject.class).get("display_name").getAsString();
            consumer.accept(name);
        }, "GET");
    }
    public static void getSubscription() {
        call("https://api.spotify.com/v1/me", getAuthorizationCode(), null, body -> {
            String name = new Gson().fromJson(body.body(), JsonObject.class).get("product").getAsString();
            Main.isPremium = name.equals("premium");
        }, "GET");
    }
    public static void setShuffle(boolean state) {
        if (!Main.playbackState.canShuffle) {
            Main.showNotAllowedToast();
            return;
        }
        call("https://api.spotify.com/v1/me/player/shuffle?state=" + (state ? "true" : "false"),
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
        call("https://api.spotify.com/v1/me/player/repeat?state=" + state,
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

        call("https://api.spotify.com/v1/me/tracks/contains?ids=" + trackId,
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

    private static final HashMap<String, Long> rateLimited = new HashMap<>();
    private static void call(String endpoint, String Authorization, String ContentType, Consumer<HttpResponse<String>> consumer, String method, String requestBody) {
        if (rateLimited.containsKey(endpoint)) {
            if (rateLimited.get(endpoint) > System.currentTimeMillis()) {
                return; // Drop calls that will be rate-limited
            } else {
                rateLimited.remove(endpoint);
            }
        }

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
            default -> null;
        };
        if (request == null) throw new RuntimeException("Invalid request method");

        client.sendAsync(request.build(), HttpResponse.BodyHandlers.ofString())
        .exceptionally(e -> {
            Main.LOGGER.error("Failed to call API: {}", e.getMessage());
            return null;
        })
        .thenAccept(stringHttpResponse -> {
            if (stringHttpResponse == null) {
                Main.LOGGER.warn("Empty response");
                return;
            }
            String responseBody = stringHttpResponse.body();
            if (responseBody.isEmpty()) {
                consumer.accept(null);
                return;
            }
            if (stringHttpResponse.statusCode() == 429) {
                long retryAfter = stringHttpResponse.headers().firstValueAsLong("Retry-After").orElse(Long.MAX_VALUE);
                Main.LOGGER.error("Rate limit hit for: endpoint: {}, retryAfter: {}", endpoint, retryAfter);
                rateLimited.put(endpoint, System.currentTimeMillis() + retryAfter * 1000);
                return;
            }
            else if (stringHttpResponse.statusCode() >= 400) {
                JsonObject error = new Gson().fromJson(responseBody, JsonObject.class);
                if (!handleError(error)) {
                    Main.CONFIG.resetConnection();
                    WebGuideServer.start();
                }
                return;
            }
            consumer.accept(stringHttpResponse);
        });
    }

    private static final List<String> handledErrors = List.of("NO_ACTIVE_DEVICE", "PREMIUM_REQUIRED");
    private static boolean handleError(JsonObject data) {
        if (!data.get("error").getAsJsonObject().has("reason")) return false;
        String reason = data.get("error").getAsJsonObject().get("reason").getAsString();

        if (handledErrors.contains(reason)) {
            MinecraftClient.getInstance().getToastManager().add(
                    new SystemToast(new SystemToast.Type(), Text.empty(), Text.translatable("api.spotify_controller.error."+reason))
            );
            return true;
        } else {
            Main.LOGGER.error("Unhandled error: {}", reason);
            return false;
        }
    }

    private static void call(String endpoint, String Authorization, String ContentType, Consumer<HttpResponse<String>> consumer, String method) {
        call(endpoint, Authorization, ContentType, consumer, method, "");
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
}