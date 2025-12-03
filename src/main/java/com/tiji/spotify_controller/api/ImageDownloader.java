package com.tiji.spotify_controller.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tiji.spotify_controller.Main;
import com.tiji.spotify_controller.util.ImageWithColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import static com.tiji.spotify_controller.api.SongDataExtractor.getId;
public class ImageDownloader {
    private static final ArrayList<Identifier> loadedCover = new ArrayList<>();
    private static final ArrayBlockingQueue<JsonObject> queue = new ArrayBlockingQueue<>(200);
    private static final HashMap<JsonObject, ArrayList<Consumer<ImageWithColor>>> onComplete = new HashMap<>();
    public static final MinecraftClient client = MinecraftClient.getInstance();

    private static ImageWithColor getAlbumCover(JsonObject trackObj) {
        try {
            Identifier id = Identifier.of(Main.MOD_ID, getId(trackObj).toLowerCase());

            int wantedSize = 100 * client.options.getGuiScale().getValue();
            if (wantedSize == 0) wantedSize = Integer.MAX_VALUE;
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
            InputStream albumCoverUrl = new URI(closestUrl).toURL().openStream();

            // Spotify provides JPEG image that Minecraft cannot handle
            // Convert to PNG
            BufferedImage jpegImage = ImageIO.read(albumCoverUrl);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(jpegImage, "png", outputStream);
            ByteArrayInputStream imageStream = new ByteArrayInputStream(outputStream.toByteArray());

            CountDownLatch latch = new CountDownLatch(1);

            NativeImage image = NativeImage.read(imageStream);
            client.execute(() -> {
                //#if MC<=12104
                //$$ client.getTextureManager().registerTexture(id,
                //$$             new NativeImageBackedTexture(image));
                //#else
                client.getTextureManager().registerTexture(id,
                        new NativeImageBackedTexture(id::getPath, image));
                //#endif

                latch.countDown();
            });
            latch.await();

            //#if MC<=12101
            //$$ Thread.sleep(100); // Texture loading before 1.21.2 is really weird
            //#endif

            loadedCover.add(id);
            return new ImageWithColor(image, id);
        } catch (IOException e) {
            Main.LOGGER.error("Failed to download album cover for {}: {}", getId(trackObj), e);
            Main.LOGGER.error(trackObj.toString());
        } catch (NullPointerException e) {
            Main.LOGGER.error("Unexpected response from Spotify: {}\n{}", trackObj, e.getLocalizedMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return new ImageWithColor(0xffffffff, Identifier.of(Main.MOD_ID, "ui/nothing.png"));
    }

    public static void addDownloadTask(JsonObject data, Consumer<ImageWithColor> callback) {
        if (loadedCover.contains(Identifier.of(Main.MOD_ID, getId(data).toLowerCase()))){
            Main.LOGGER.debug("Cache hit for {}", getId(data));
            CompletableFuture.runAsync(() ->
                callback.accept(new ImageWithColor(Identifier.of(Main.MOD_ID, getId(data).toLowerCase())))
            );
            return;
        }
        Main.LOGGER.debug("Adding download task lister for {}", getId(data));
        if (onComplete.containsKey(data)) {
            onComplete.get(data).add(callback);
            return;
        }
        ArrayList<Consumer<ImageWithColor>> callbacks = new ArrayList<>();
        callbacks.add(callback);
        onComplete.put(data, callbacks);
        queue.add(data);
        Main.LOGGER.debug("Added download task for {} - Queue size: {}", getId(data), queue.size());
    }

    public static void startThreads() {
        for (int i = 0; i < Main.CONFIG.imageIoThreadCount(); i++) {
            Thread thread = new Thread(null, ImageDownloader::threadWorker, "Image-IO-" + i);
            thread.start();
        }
    }

    private static void threadWorker(){
        while (!Thread.interrupted()) {
            try {
                JsonObject task = queue.take();
                ImageWithColor coverId = getAlbumCover(task);

                for (Consumer<ImageWithColor> callback : onComplete.remove(task)) {
                    callback.accept(coverId);
                }
                Main.LOGGER.debug("Finished downloading cover for {}", getId(task));
            } catch (Exception e) {
                StringBuilder sb = new StringBuilder();
                for (StackTraceElement element : e.getStackTrace()) {
                    sb.append("at ");
                    sb.append(element.toString()).append("\n");
                }
                Main.LOGGER.error("Error in Image-IO thread: {}\n{}", e.getLocalizedMessage(), sb);
                // Exception shouldn't stop thread
                // They are mostly not from IO
            }
        }
    }
}
