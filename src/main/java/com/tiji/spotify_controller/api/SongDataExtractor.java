package com.tiji.spotify_controller.api;

import com.tiji.spotify_controller.Main;
import com.tiji.spotify_controller.api.spotify.SpotifyApi;
import com.tiji.spotify_controller.api.spotify.TrackObject;
import com.tiji.spotify_controller.ui.Icons;
import com.tiji.spotify_controller.util.ImageWithColor;
import com.tiji.spotify_controller.util.InterpolatedTime;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;

public class SongDataExtractor {
    public static String getFormattedTime(int time_ms) {
        time_ms /= 1000;

        int minutes = time_ms / 60;
        int seconds = time_ms % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    public static void reloadData(boolean forceFullReload, Runnable onNoUpdate, Runnable onDataUpdate, Runnable onImageLoad) {
        SpotifyApi.getNowPlayingTrack(data -> {
            if (data == null) {
                Main.currentlyPlaying = SongData.emptyData();
                Main.playbackState.canGoBack = false;
                Main.playbackState.canRepeat = false;
                Main.playbackState.canSeek = false;
                Main.playbackState.canSkip = false;
                Main.playbackState.canShuffle = false;
                return;
            }

            boolean isSongDifferent = !data.track.id.equals(Main.currentlyPlaying.id);

            Main.playbackState.isPlaying = data.isPlaying;
            Main.playbackState.progressMs = InterpolatedTime.optionalProgression(
                    data.progress.getInterpolatedTime(),
                    Main.playbackState.isPlaying);

            Main.playbackState.durationMs = data.track.duration;
            Main.playbackState.repeat = data.repeatState;
            Main.playbackState.shuffle = data.shuffleState;

            var allowedActions = data.allowedActions.getOrDefault("disallows", Map.of());
            Main.playbackState.canShuffle = !allowedActions.containsKey("toggling_shuffle");
            Main.playbackState.canRepeat = !(allowedActions.containsKey("toggling_repeat_context") ||
                                    allowedActions.containsKey("toggling_repeat_track"));
            Main.playbackState.canSkip = !allowedActions.containsKey("skipping_next");
            Main.playbackState.canGoBack = !allowedActions.containsKey("skipping_prev");
            Main.playbackState.canSeek = !allowedActions.containsKey("seeking");

            if (isSongDifferent || forceFullReload) {
                Main.currentlyPlaying = getDataFor(data.track, onImageLoad);
            }

            SpotifyApi.isSongLiked(Main.currentlyPlaying.id, isLiked ->
                Main.playbackState.isLiked = isLiked
            );
            if (isSongDifferent || forceFullReload) {
                onDataUpdate.run();
            } else {
                onNoUpdate.run();
            }
        });
    }
    public static SongData getDataFor(TrackObject data, @Nullable Runnable onImageLoad) {
        SongData song = new SongData();

        song.raw_title = data.name;
        song.title = Component.empty()
                .append(data.explicit ? Icons.EXPLICT : Component.literal(""))
                .append(Component.literal(song.raw_title));

        song.artist = "";
        Arrays.asList(data.artists).forEach(artist -> song.artist += artist.name + ", ");
        song.artist = song.artist.substring(0, song.artist.length() - 2);

        song.main_artist = data.artists[0].name;
        song.album = data.album.name;
        song.durationLabel = getFormattedTime(data.duration);
        song.id = data.id;
        song.duration = data.duration;
        song.songURI = data.spotifyUrl;

        if (!song.coverImage.getImage().getPath().equals("ui/nothing.png")) {
            song.coverImage = new ImageWithColor(0xffffffff, ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "ui/nothing.png"));
        }

        ImageDownloader.addDownloadTask(data, image -> {
            song.coverImage = image;
            if (onImageLoad != null) {
                Minecraft.getInstance().execute(onImageLoad);
            }
        });

        return song;
    }
}
