package com.tiji.media.api;

import com.google.gson.JsonObject;
import com.tiji.media.MediaClient;
import com.tiji.media.ui.Icons;
import com.tiji.media.util.imageWithColor;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.net.URI;

public class SongDataExtractor {
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
            if (data == null) {
                MediaClient.currentlyPlaying = SongData.emptyData();
                MediaClient.canGoBack = false;
                MediaClient.canRepeat = false;
                MediaClient.canSeek = false;
                MediaClient.canSkip = false;
                MediaClient.canShuffle = false;
                return;
            }
            boolean isSongDifferent = !getId(data.getAsJsonObject("item")).equals(MediaClient.currentlyPlaying.Id);

            MediaClient.progressLabel = getProgressLabel(data);
            MediaClient.isPlaying = isPlaying(data);
            MediaClient.progressValue = getDuration(data);
            MediaClient.repeat = getRepeatState(data);
            MediaClient.shuffle = getShuffleState(data);

            JsonObject disallows = data.getAsJsonObject("actions").getAsJsonObject("disallows");
            MediaClient.canShuffle = !disallows.has("toggling_shuffle");
            MediaClient.canRepeat = !(disallows.has("toggling_repeat_context") ||
                                    disallows.has("toggling_repeat_track"));
            MediaClient.canSkip = !disallows.has("skipping_next");
            MediaClient.canGoBack = !disallows.has("skipping_prev");
            MediaClient.canSeek = !disallows.has("seeking");

            if (isSongDifferent || forceFullReload) {
                MediaClient.currentlyPlaying = getDataFor(data.getAsJsonObject("item"), onImageLoad);
            }

            ApiCalls.isSongLiked(MediaClient.currentlyPlaying.Id, isLiked ->
                MediaClient.isLiked = isLiked
            );
            if (isSongDifferent || forceFullReload) {
                onDataUpdate.run();
            } else {
                onNoUpdate.run();
            }
        });
    }
    public static SongData getDataFor(JsonObject data, @Nullable Runnable onImageLoad) {
        SongData song = new SongData();

        song.title = Text.empty()
                .append(isExplicit(data) ? Icons.EXPLICT : Text.literal(""))
                .append(Text.literal(getName(data)));
        song.artist = getArtist(data);
        song.durationLabel = getDurationLabel(data);
        song.Id = getId(data);
        song.duration = getMaxDuration(data);
        song.songURI = getSpotifyLink(data);

        if (!song.coverImage.image.getPath().equals("ui/nothing.png")) {
            //MinecraftClient.getInstance().getTextureManager().destroyTexture(SongData.coverImage);        //Deleted line as they are used on toasts. Will be re-visited
            song.coverImage = new imageWithColor(0xffffffff, Identifier.of("media", "ui/nothing.png"));
        }

        ImageDownloader.addDownloadTask(data, image -> {
            song.coverImage = image;
            if (onImageLoad != null) {
                onImageLoad.run();
            }
        });

        return song;
    }
}
