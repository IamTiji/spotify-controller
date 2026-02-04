package com.tiji.spotify_controller.api;

import com.tiji.spotify_controller.Main;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.client.ClientBrandRetriever;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ApiHandler {
    static {
        String version = FabricLoader.getInstance()
                .getModContainer(Main.MOD_ID).orElseThrow()
                .getMetadata()
                .getVersion().getFriendlyString();

        String devState = DevStateProvider.getDevState();

        String loaderBrand = ClientBrandRetriever.getClientModName();

        String minecraftVersion =
                //#if MC>=12106
                //$$ SharedConstants.getCurrentVersion().name();
                //#else
                SharedConstants.getCurrentVersion().getName();
                //#endif

        String ua = "Spotify-Controller/%s (Minecraft/%s; %s; %s) +https://modrinth.com/mod/spotify-controller"
                .formatted(version, minecraftVersion, loaderBrand, devState);
        userAgent = ua;

        Main.LOGGER.info("API initialized with user-agent: {}", ua);
    }

    private static final String userAgent;
    private static final HttpClient client = HttpClient.newHttpClient();

    private final HashMap<String, Long> rateLimited = new HashMap<>();
    protected void call(String endpoint,
                               Map<String, String> headers,
                               String ContentType,
                               Consumer<HttpResponse<String>> onSuccess,
                               Consumer<HttpResponse<String>> onError,
                               String method,
                               String requestBody) {
        if (rateLimited.containsKey(endpoint)) {
            if (rateLimited.get(endpoint) > System.currentTimeMillis()) {
                return; // Drop calls that will be rate-limited
            } else {
                rateLimited.remove(endpoint);
            }
        }

        HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .timeout(Duration.ofSeconds(30))
                .header("User-Agent", userAgent);

        for (Map.Entry<String, String> header : headers.entrySet()) {
            request.header(header.getKey(), header.getValue());
        }

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
                        onSuccess.accept(null);
                        return;
                    }
                    if (stringHttpResponse.statusCode() == 429) {
                        long retryAfter = stringHttpResponse.headers().firstValueAsLong("Retry-After").orElse(Long.MAX_VALUE);
                        Main.LOGGER.error("Rate limit hit for: endpoint: {}, retryAfter: {}", endpoint, retryAfter);
                        rateLimited.put(endpoint, System.currentTimeMillis() + retryAfter * 1000);
                        return;
                    }
                    else if (stringHttpResponse.statusCode() >= 400) {
                        onError.accept(stringHttpResponse);
                        return;
                    }
                    onSuccess.accept(stringHttpResponse);
                });
    }

    protected void call(String endpoint, String authorization, Consumer<HttpResponse<String>> consumer, String method, String requestBody) {
        call(endpoint, Map.of("Authorization", authorization), null, consumer, method, requestBody);
    }

    protected void call(String endpoint, String authorization, String ContentType, Consumer<HttpResponse<String>> consumer, String method) {
        call(endpoint, Map.of("Authorization", authorization), ContentType, consumer, method, "");
    }

    protected void call(String endpoint, Consumer<HttpResponse<String>> consumer, String method, String requestBody) {
        call(endpoint, Map.of(), null, consumer, method, requestBody);
    }

    protected void call(String endpoint,
                               Map<String, String> headers,
                               String ContentType,
                               Consumer<HttpResponse<String>> onSuccess,
                               String method,
                               String requestBody) {
        call(endpoint, headers, ContentType, onSuccess, result -> {}, method, requestBody);
    }
}
