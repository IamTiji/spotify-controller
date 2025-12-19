package com.tiji.media;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.tiji.media.api.ApiCalls;
import net.minecraft.client.MinecraftClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Path;

public class WebGuideServer {
    public static HttpServer server;

    static String getMIMEType(String filename) {
        String extension = filename.substring(filename.lastIndexOf('.') + 1);
        return switch (extension) {
            case "html" -> "text/html";
            case "css" -> "text/css";
            case "png" -> "image/png";
            default -> "application/octet-stream";
        };
    }

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
            URI path = exchange.getRequestURI();
            String filepath;
            if (path.getPath().equals("/")) {
                 filepath = switch (MinecraftClient.getInstance().getLanguageManager().getLanguage()) {
                    case "ko_kr" -> "/guide/ko_kr.html";
                    default -> "/guide/en_us.html";
                };
            } else {
                filepath = String.valueOf(Path.of("/guide/", path.getPath()));
                filepath = filepath.replaceAll("\\\\", "/");
            }

            byte[] data;
            try (InputStream in = WebGuideServer.class.getResourceAsStream(filepath)) {
                if (in == null) throw new RuntimeException("Guide file is not found!");
                data = in.readAllBytes();
            }

            exchange.getResponseHeaders().set("Content-Type", getMIMEType(filepath));
            exchange.sendResponseHeaders(200, data.length);

            OutputStream os = exchange.getResponseBody();
            os.write(data);
            os.close();
        }
    }
    private static class callbackHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String Code = exchange.getRequestURI().getQuery().split("=")[1];

            MediaClient.LOGGER.info("Callback Received: {}", Code);

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

            MediaClient.LOGGER.info("Stopping Guide Server...");
            WebGuideServer.stop();
        }
    }
    private static class dataHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            MediaClient.CONFIG.clientSecret(exchange.getRequestHeaders().getFirst("Secret"));
            MediaClient.CONFIG.clientId(exchange.getRequestHeaders().getFirst("Client-Id"));

            MediaClient.LOGGER.info("Client Information Received");

            String response = "Received";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
