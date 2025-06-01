package com.tiji.media;

import com.sun.net.httpserver.*;
import net.minecraft.client.MinecraftClient;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.Executor;

public class WebGuideServer {
    public static HttpServer server;

    static {
        try {
            server = HttpServer.create(new InetSocketAddress(25566), 0);
        } catch (IOException e) {
            Media.LOGGER.error("Unexpected error: {}", e);
        }
    }

    public static void start() throws IOException {
        server.createContext("/callback", new callbackHandler());
        server.createContext("/secret", new secretHandler());
        server.createContext("/id", new idHandler());
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
            String response;
            int length;
            InputStream in;
            try{
                in = switch (MinecraftClient.getInstance().getLanguageManager().getLanguage()) {
                    case "ko_kr" -> getClass().getResourceAsStream("/guide/ko_kr.html");
                    default -> getClass().getResourceAsStream("/guide/en_us.html");
                };
                byte[] file = in.readAllBytes();
                length = file.length;
                response = new String(file);
            }catch(Exception e){
                e.printStackTrace();
                return;
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

            String response;
            int length;
            InputStream in;
            try{
                in = switch (MinecraftClient.getInstance().getLanguageManager().getLanguage()) {
                    case "ko_kr" -> getClass().getResourceAsStream("/allset/ko_kr.html");
                    default -> getClass().getResourceAsStream("/allset/en_us.html");
                };
                byte[] file = in.readAllBytes();
                length = file.length;
                response = new String(file);
            }catch(Exception e){
                e.printStackTrace();
                return;
            }
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();

            ApiCalls.convertAccessToken(Code);

            MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(null));

            SongDataExtractor.reloadData(true, () -> {}, () -> {}, () -> {});
            Media.LOGGER.info("Stopping Guide Server...");
            WebGuideServer.stop();
        }
    }
    private static class secretHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String Code = exchange.getRequestURI().getQuery().split("=")[1];
            MediaClient.CONFIG.clientSecret(Code);

            Media.LOGGER.info("Client Secret Received");

            String response = "Received";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    private static class idHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String Code = exchange.getRequestURI().getQuery().split("=")[1];
            MediaClient.CONFIG.clientId(Code);

            Media.LOGGER.info("Client ID Received");

            String response = "Received";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
