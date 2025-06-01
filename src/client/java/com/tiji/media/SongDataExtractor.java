package com.tiji.media;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class SongDataExtractor {
    private static final ArrayList<Identifier> loadedCover = new ArrayList<>();

    public static String getName(JsonObject trackObj) {
        return trackObj.getAsJsonObject("item").get("name").getAsString();
    }
    public static String getArtist(JsonObject trackObj) {
        StringBuilder artists = new StringBuilder();
        for (var artist : trackObj.getAsJsonObject("item").getAsJsonArray("artists")) {
            artists.append(artist.getAsJsonObject().get("name").getAsString()).append(", ");
        }
        artists.setLength(artists.length() - 2); // Remove trailing comma and space
        return artists.toString();
    }
    public static String getId(JsonObject trackObj) {
        return trackObj.getAsJsonObject("item").get("id").getAsString().toLowerCase();
    }
    public static URI getSpotifyLink(JsonObject trackObj) {
        return URI.create(
                trackObj.getAsJsonObject("item").getAsJsonObject("external_urls").get("spotify").getAsString()
        );
    }

    public static Identifier getAlbumCover(JsonObject trackObj) {
        try {
            Identifier id = Identifier.of("media", getId(trackObj));

            if (loadedCover.contains(id)) {
                return id;
            } else{
                loadedCover.add(id);
            }

            int wantedSize = 100 * MinecraftClient.getInstance().options.getGuiScale().getValue();
            int closest = Integer.MAX_VALUE;
            JsonArray images = trackObj.getAsJsonObject("item")
                    .getAsJsonObject("album")
                    .getAsJsonArray("images");
            String closestUrl = images.get(0)
                    .getAsJsonObject().get("url").getAsString();

            for (int i = 0; i < images.size(); i++) {
                int size = images.get(i)
                        .getAsJsonObject().get("height").getAsInt();
                if (closest > size && size >= wantedSize) {
                    closest = size;
                    closestUrl = images.get(i)
                            .getAsJsonObject().get("url").getAsString();
                }
            }
            InputStream albumCoverUrl = URI.create(closestUrl).toURL().openStream();

            // Spotify provides JPEG image that Minecraft cannot handle
            // Convert to PNG
            BufferedImage jpegImage = ImageIO.read(albumCoverUrl);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(jpegImage, "png", outputStream);
            ByteArrayInputStream imageStream = new ByteArrayInputStream(outputStream.toByteArray());

            NativeImage image = NativeImage.read(imageStream);
            MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().getTextureManager().registerTexture(id,
                    new NativeImageBackedTexture(id::toString, image)));

            return id;
        }catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static double getDuration(JsonObject trackObj) {
        return trackObj.get("progress_ms").getAsDouble()/
                trackObj.getAsJsonObject("item")
                        .get("duration_ms").getAsDouble();
    }
    public static String getDurationLabel(JsonObject trackObj) {
        int duration = trackObj.getAsJsonObject("item").get("duration_ms").getAsInt();

        duration /= 1000;

        Integer minutes_duration = duration / 60;
        Integer seconds_duration = duration % 60;

        return String.format("%02d:%02d", minutes_duration, seconds_duration);
    }
    public static String getProgressLabel(JsonObject trackObj) {
        int progress = trackObj.get("progress_ms").getAsInt();

        progress /= 1000;

        Integer minutes_progress = progress / 60;
        Integer seconds_progress = progress % 60;

        return String.format("%02d:%02d", minutes_progress, seconds_progress);
    }
    public static boolean isPlaying(JsonObject trackObj) {
        return trackObj.get("is_playing").getAsBoolean();
    }
    public static int getMaxDuration(JsonObject trackObj) {
        return trackObj.getAsJsonObject("item").get("duration_ms").getAsInt();
    }
    public static boolean isExplicit(JsonObject trackObj) {
        return trackObj.get("item").getAsJsonObject().get("explicit").getAsBoolean();
    }
    public static void reloadData(boolean forceFullReload, Runnable onNoUpdate, Runnable onDataUpdate, Runnable onImageLoad) {
        ApiCalls.getNowPlayingTrack(data -> {
            boolean isSongDifferent = !getId(data).equals(SongData.Id);

            SongData.progressLabel = getProgressLabel(data);
            SongData.isPlaying = isPlaying(data);
            SongData.progressValue = getDuration(data);

            if (isSongDifferent || forceFullReload) {
                SongData.title = (isExplicit(data) ? "\uD83C\uDD74 " : "") + getName(data);
                SongData.artist = getArtist(data);
                SongData.durationLabel = getDurationLabel(data);
                SongData.Id = getId(data);
                SongData.duration = getMaxDuration(data);
                SongData.songURI = getSpotifyLink(data);

                if (!SongData.coverImage.getPath().equals("ui/nothing.png")) {
                    //MinecraftClient.getInstance().getTextureManager().destroyTexture(SongData.coverImage);        //Deleted line as they are used on toasts. Will be re-visited
                    SongData.coverImage = Identifier.of("media", "ui/nothing.png");
                }
                CompletableFuture<Identifier> ImageIOFuture = CompletableFuture.supplyAsync(() -> getAlbumCover(data));
                ImageIOFuture.thenAccept(id -> {
                    SongData.coverImage = id;
                    onImageLoad.run();
                });
            }
            if (isSongDifferent || forceFullReload) {
                onDataUpdate.run();
            }else{
                onNoUpdate.run();
            }
        });
    }
}
