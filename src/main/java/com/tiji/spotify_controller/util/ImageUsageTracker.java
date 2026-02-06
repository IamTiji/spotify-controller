package com.tiji.spotify_controller.util;

import com.tiji.spotify_controller.Main;
import com.tiji.spotify_controller.api.ImageDownloader;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;

public class ImageUsageTracker {
    private static final int MAX_IMAGE_LIFETIME = 5 * 1000;

    private static final HashMap<ResourceLocation, Long> imageUsage = new HashMap<>();

    public static void registerImage(ResourceLocation id) {
        synchronized (imageUsage) {
            imageUsage.put(id, System.currentTimeMillis());
        }
    }

    public static void runGC() {
        Main.currentlyPlaying.coverImage.getImage(); // Update cover image so that it doesn't get gc'ed

        long currentTime = System.currentTimeMillis();

        ArrayList<ResourceLocation> toRemove = new ArrayList<>(imageUsage.size());

        synchronized (imageUsage) {
            for (ResourceLocation id : imageUsage.keySet()) {
                if (currentTime - imageUsage.get(id) > MAX_IMAGE_LIFETIME) {
                    toRemove.add(id);
                    cleanUpUsages(id);
                }
            }
            toRemove.forEach(imageUsage.keySet()::remove);
        }
    }

    public static void cleanUpUsages(ResourceLocation id) {
        ImageDownloader.removeFromCache(id);
        Minecraft instance = Minecraft.getInstance();
        instance.execute(() -> instance.getTextureManager().release(id));
    }
}
