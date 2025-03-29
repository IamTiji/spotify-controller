package com.tiji.media.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tiji.media.Media;
import com.tiji.media.MediaClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.tiji.media.api.SongDataExtractor.*;
public class ImageDownloader {
    private static final ArrayList<Identifier> loadedCover = new ArrayList<>();
    private static final Map<JsonObject, Consumer<Identifier>> tasks = new HashMap<>(100);

    @SuppressWarnings("deprecation") // It will be re-visited
    private static Identifier getAlbumCover(JsonObject trackObj) {
        try {
            Identifier id = Identifier.of("media", getId(trackObj).toLowerCase());

            if (loadedCover.contains(id)) {
                return id;
            } else{
                loadedCover.add(id);
            }

            int wantedSize = 100 * MinecraftClient.getInstance().options.getGuiScale().getValue();
            int closest = Integer.MAX_VALUE;
            JsonArray ImageList = trackObj.getAsJsonObject("album")
                    .getAsJsonArray("images");
            String closestUrl = ImageList.get(0)
                    .getAsJsonObject().get("url").getAsString();

            for (int i = 0; i < ImageList.size(); i++) {
                int size = ImageList.get(i)
                        .getAsJsonObject().get("height").getAsInt();
                if (closest > size && size >= wantedSize) {
                    closest = size;
                    closestUrl = ImageList.get(i)
                            .getAsJsonObject().get("url").getAsString();
                }
            }
            InputStream albumCoverUrl = new URL(closestUrl).openStream();

            // Spotify provides JPEG image that Minecraft cannot handle
            // Convert to PNG
            BufferedImage jpegImage = ImageIO.read(albumCoverUrl);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(jpegImage, "png", outputStream);
            ByteArrayInputStream imageStream = new ByteArrayInputStream(outputStream.toByteArray());

            NativeImage image = NativeImage.read(imageStream);
            MinecraftClient.getInstance().getTextureManager().registerTexture(id,
                    new NativeImageBackedTexture(image));

            Thread.sleep(150); // Wait until the texture is loaded

            return id;
        }catch (IOException e) {
            Media.LOGGER.error("Failed to download album cover for {}: {}", getId(trackObj), e);
            Media.LOGGER.error(trackObj.toString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (NullPointerException e) {
            Media.LOGGER.error("Unexpected response from Spotify: {}\n{}", trackObj, e.getLocalizedMessage());
        }
        return Identifier.of("media", "ui/nothing.png");
    }

    public static void addDownloadTask(JsonObject data, Consumer<Identifier> callback) {
        tasks.put(data, callback);
        Media.LOGGER.debug("Added download task for {}", getId(data));
    }

    public static void startThreads() {
        for (int i = 0; i < MediaClient.CONFIG.imageIoThreadCount(); i++) {
            Thread thread = new Thread(null, () -> {
                while (!Thread.interrupted()) {
                    try {
                        while (tasks.isEmpty()) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                        JsonObject data;
                        Consumer<Identifier> callback;
                        try {
                            data = tasks.keySet().iterator().next();
                            callback = tasks.remove(data);
                        } catch (NullPointerException e) {
                            continue; // Other thread already took this task
                        }

                        if (callback == null) {
                            getAlbumCover(data);
                            return;
                        }
                        callback.accept(getAlbumCover(data));
                        Media.LOGGER.debug("Finished downloading cover for {}", getId(data));
                    } catch (Exception e) {
                        StringBuilder sb = new StringBuilder();
                        for (StackTraceElement element : e.getStackTrace()) {
                            sb.append("at ");
                            sb.append(element.toString()).append("\n");
                        }
                        Media.LOGGER.error("Error in Image-IO thread: {}\n{}", e.getLocalizedMessage(), sb);
                        // Exception shouldn't stop thread
                        // They are mostly not from IO
                    }
                }
            }, "Image-IO-" + i);
            thread.start();
        }
    }
}
