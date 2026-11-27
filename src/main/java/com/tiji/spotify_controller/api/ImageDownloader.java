package com.tiji.spotify_controller.api;

import com.mojang.blaze3d.platform.NativeImage;
import com.tiji.spotify_controller.Main;
import com.tiji.spotify_controller.api.spotify.TrackObject;
import com.tiji.spotify_controller.util.ImageWithColor;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

//#if MC<=26300
import net.minecraft.client.renderer.texture.DynamicTexture;
//#else
//$$ import net.minecraft.client.renderer.texture.ExternalTextureProvider;
//$$ import net.minecraft.client.renderer.texture.TextureResources;
//#endif

public class ImageDownloader {
    private static final ArrayList<TrackObject> loadedCover = new ArrayList<>();
    private static final ArrayBlockingQueue<TrackObject> queue = new ArrayBlockingQueue<>(200);
    private static final HashMap<TrackObject, ArrayList<Consumer<ImageWithColor>>> onComplete = new HashMap<>();
    public static final Minecraft client = Minecraft.getInstance();

    private static ImageWithColor getAlbumCover(TrackObject track) {
        try {
            ResourceLocation id = track.mcId;

            InputStream albumCoverIS = track.album.albumCover.openStream();

            // Spotify provides JPEG image that Minecraft cannot handle
            // Convert to PNG first
            BufferedImage jpegImage = ImageIO.read(albumCoverIS);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(jpegImage, "png", outputStream);
            ByteArrayInputStream imageStream = new ByteArrayInputStream(outputStream.toByteArray());

            CountDownLatch latch = new CountDownLatch(1);

            NativeImage image = NativeImage.read(imageStream);
            client.execute(() -> {
                //#if MC<=26300

                //#if MC<=12104
                //$$ DynamicTexture texture = new DynamicTexture(image);
                //#elseif MC<=26300
                DynamicTexture texture = new DynamicTexture(id::getPath, image);
                //#endif

                client.getTextureManager().register(id, texture);

                //#else
                //$$ TextureResources texture = TextureResources.from2dImage(id::getPath, image);
                //$$ client.getTextureManager().registerProvider(id, new ExternalTextureProvider(texture));
                //#endif


                latch.countDown();
            });
            latch.await();

            //#if MC<=12101
            //$$ Thread.sleep(100); // Texture loading before 1.21.2 is really weird
            //#endif

            imageStream.close();
            albumCoverIS.close();

            loadedCover.add(track);
            return new ImageWithColor(image, id);
        } catch (IOException e) {
            Main.LOGGER.error("Failed to download album cover for {}: {}", track.id, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return new ImageWithColor(0xffffffff, ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "ui/nothing.png"));
    }

    public static void addDownloadTask(TrackObject track, Consumer<ImageWithColor> callback) {
        if (loadedCover.contains(track)){
            Main.LOGGER.debug("Cache hit for {}", track.id);
            CompletableFuture.runAsync(() ->
                callback.accept(new ImageWithColor(track.mcId))
            );
            return;
        }
        Main.LOGGER.debug("Adding download task listener for {}", track.id);
        if (onComplete.containsKey(track)) {
            onComplete.get(track).add(callback);
            return;
        }
        ArrayList<Consumer<ImageWithColor>> callbacks = new ArrayList<>();
        callbacks.add(callback);
        onComplete.put(track, callbacks);
        queue.add(track);
        Main.LOGGER.debug("Added download task for {} - Queue size: {}", track.id, queue.size());
    }

    public static void startThreads() {
        for (int i = 0; i < Main.CONFIG.imageIoThreadCount(); i++) {
            Thread thread = new Thread(null, ImageDownloader::threadWorker, "Image-IO-" + i);
            thread.setDaemon(true);
            thread.start();
        }
    }

    private static void threadWorker(){
        while (!Thread.interrupted()) {
            try {
                TrackObject task = queue.take();
                ImageWithColor coverId = getAlbumCover(task);

                for (Consumer<ImageWithColor> callback : onComplete.remove(task)) {
                    callback.accept(coverId);
                }
                Main.LOGGER.debug("Finished downloading cover for {}", task.id);
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

    public static void removeFromCache(ResourceLocation id) {
        loadedCover.removeIf(track -> track.mcId.equals(id));
    }
}
