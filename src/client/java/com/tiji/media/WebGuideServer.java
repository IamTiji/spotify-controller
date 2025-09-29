package com.tiji.media;

import com.sun.net.httpserver.*;
import com.tiji.media.api.ApiCalls;
import com.tiji.media.api.SongDataExtractor;
import net.minecraft.client.MinecraftClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class WebGuideServer {
    public static HttpServer server;

    public static void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(25566), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        server.createContext("/callback", new callbackHandler());
        server.createContext("/data", new dataHandler());
        server.createContext("/", new rootHandler());

        server.setExecutor(null);
        server.start();
    }
    public static void stop() {
        server.stop(0);
    }
    private static class rootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String filepath = switch (MinecraftClient.getInstance().getLanguageManager().getLanguage()) {
                case "ko_kr" -> "/guide/ko_kr.html";
                default -> "/guide/en_us.html";
            };

            int length;
            String response;
            try (InputStream in = WebGuideServer.class.getResourceAsStream(filepath)) {
                if (in == null) throw new RuntimeException("Guide file is not found!");
                byte[] file = in.readAllBytes();
                length = file.length;
                response = new String(file);
            }

            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, length);

            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    private static class callbackHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String Code = exchange.getRequestURI().getQuery().split("=")[1];
            MediaClient.CONFIG.accessToken(Code);

            Media.LOGGER.info("Callback Received: {}", Code);

            String filepath = switch (MinecraftClient.getInstance().getLanguageManager().getLanguage()) {
                case "ko_kr" -> "/allset/ko_kr.html";
                default -> "/allset/en_us.html";
            };

            int length;
            String response;
            try (InputStream in = WebGuideServer.class.getResourceAsStream(filepath)) {
                if (in == null) throw new RuntimeException("Guide file is not found!");
                byte[] file = in.readAllBytes();
                length = file.length;
                response = new String(file);
            }

            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, length);

            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();

            ApiCalls.convertAccessToken(Code);

            MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(null));

            Media.LOGGER.info("Stopping Guide Server...");
            WebGuideServer.stop();
        }
    }
    private static class dataHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            MediaClient.CONFIG.clientSecret(exchange.getRequestHeaders().getFirst("Secret"));
            MediaClient.CONFIG.clientId(exchange.getRequestHeaders().getFirst("Client-Id"));

            Media.LOGGER.info("Client Information Received");

            String response = "Received";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
