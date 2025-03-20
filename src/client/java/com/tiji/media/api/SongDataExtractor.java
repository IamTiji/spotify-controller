package com.tiji.media.api;

import com.google.gson.JsonObject;
import com.tiji.media.MediaClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

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
    private static final Style ICON = Style.EMPTY.withFont(Identifier.of("media", "icon"));
    private static final Style DEFAULT = Style.EMPTY.withFont(Identifier.ofVanilla("default"));

    public static String getName(JsonObject trackObj) {
        return trackObj.get("name").getAsString();
    }
    public static String getArtist(JsonObject trackObj) {
        StringBuilder artists = new StringBuilder();
        for (var artist : trackObj.getAsJsonArray("artists")) {
            artists.append(artist.getAsJsonObject().get("name").getAsString()).append(", ");
        }
        artists.setLength(artists.length() - 2); // Remove trailing comma and space
        return artists.toString();
    }
    public static String getId(JsonObject trackObj) {
        return trackObj.get("id").getAsString();
    }
    public static URI getSpotifyLink(JsonObject trackObj) {
        return URI.create(
                trackObj.getAsJsonObject("external_urls").get("spotify").getAsString()
        );
    }
    @SuppressWarnings("deprecation") // It will be re-visited
    public static Identifier getAlbumCover(JsonObject trackObj) {
        try {
            Identifier id = Identifier.of("media", getId(trackObj).toLowerCase());

            if (loadedCover.contains(id)) {
                return id;
            } else{
                loadedCover.add(id);
            }

            int wantedSize = 100 * MinecraftClient.getInstance().options.getGuiScale().getValue();
            int closest = Integer.MAX_VALUE;
            String closestUrl = trackObj.getAsJsonObject("album")
                    .getAsJsonArray("images").get(0)
                    .getAsJsonObject().get("url").getAsString();

            for (int i = 0; i < trackObj.getAsJsonObject("album")
                    .getAsJsonArray("images").size(); i++) {
                int size = trackObj.getAsJsonObject("album")
                        .getAsJsonArray("images").get(i)
                        .getAsJsonObject().get("height").getAsInt();
                if (closest > size && size >= wantedSize) {
                    closest = size;
                    closestUrl = trackObj.getAsJsonObject("album")
                            .getAsJsonArray("images").get(i)
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
        int duration = trackObj.get("duration_ms").getAsInt();

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
        return trackObj.get("duration_ms").getAsInt();
    }
    public static boolean isExplicit(JsonObject trackObj) {
        return trackObj.get("explicit").getAsBoolean();
    }
    public static boolean getShuffleState(JsonObject trackObj) {
        return trackObj.get("shuffle_state").getAsBoolean();
    }
    public static String getRepeatState(JsonObject trackObj) {
        return trackObj.get("repeat_state").getAsString();
    }
    public static void reloadData(boolean forceFullReload, Runnable onNoUpdate, Runnable onDataUpdate, Runnable onImageLoad) {
        ApiCalls.getNowPlayingTrack(data -> {
            if (data == null) return;
            boolean isSongDifferent = !getId(data.getAsJsonObject("item")).equals(MediaClient.currentlyPlaying.Id);

            MediaClient.progressLabel = getProgressLabel(data);
            MediaClient.isPlaying = isPlaying(data);
            MediaClient.progressValue = getDuration(data);
            MediaClient.repeat = getRepeatState(data);
            MediaClient.shuffle = getShuffleState(data);

            if (isSongDifferent || forceFullReload) {
                MediaClient.currentlyPlaying = getDataFor(data.getAsJsonObject("item"), onImageLoad);
            }

            ApiCalls.isSongLiked(MediaClient.currentlyPlaying.Id, isLiked -> {
                MediaClient.isLiked = isLiked;
            });
            if (isSongDifferent || forceFullReload) {
                onDataUpdate.run();
            }else{
                onNoUpdate.run();
            }
        });
    }
    public static SongData getDataFor(JsonObject data, @Nullable Runnable onImageLoad) {
        SongData song = new SongData();

        song.title = Text.literal(isExplicit(data) ? "9 " : "").setStyle(ICON).append(Text.literal(getName(data)).setStyle(DEFAULT));
        song.artist = getArtist(data);
        song.durationLabel = getDurationLabel(data);
        song.Id = getId(data);
        song.duration = getMaxDuration(data);
        song.songURI = getSpotifyLink(data);

        if (!song.coverImage.getPath().equals("ui/nothing.png")) {
            //MinecraftClient.getInstance().getTextureManager().destroyTexture(SongData.coverImage);        //Deleted line as they are used on toasts. Will be re-visited
            song.coverImage = Identifier.of("media", "ui/nothing.png");
        }

        CompletableFuture<Identifier> ImageIOFuture = CompletableFuture.supplyAsync(() -> getAlbumCover(data));
        ImageIOFuture.thenAccept(id -> {
            song.coverImage = id;
            if (onImageLoad != null) {
                onImageLoad.run();
            }
        });

        return song;
    }
}
