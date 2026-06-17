package com.tiji.spotify_controller.mixin;

import com.tiji.spotify_controller.Main;
import com.tiji.spotify_controller.api.SongData;
import com.tiji.spotify_controller.api.SongDataExtractor;
import com.tiji.spotify_controller.api.SpotifyApi;
import com.tiji.spotify_controller.util.ImageUsageTracker;
import com.tiji.spotify_controller.util.RequestManager;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class CallbackInject {
    @Unique private static boolean requestSent = false;

    @Inject(method = "runTick", at = @At("TAIL"))
    public void inject(boolean renderLevel, CallbackInfo ci) {
        if (Main.currentlyPlaying != null &&
                Main.playbackState.progressMs.getInterpolatedTime() > Main.currentlyPlaying.duration) {
            if (!requestSent) {
                RequestManager.putRequest(150);
                requestSent = true;
            }
        } else {
            requestSent = false;
        }
        if (!Main.isNotSetup() &&
                ((Util.getMillis() - Main.lastUploadMs) > Main.UPDATE_INTERVAL_MS || RequestManager.pollRequest()) &&
                Main.currentlyPlaying != null) {
            Main.lastUploadMs = Util.getMillis();
            ImageUsageTracker.runGC();
            if (Main.nowPlayingScreen != null) {
                SongDataExtractor.reloadData(false, Main.nowPlayingScreen::updateStatus, Main.nowPlayingScreen::updateNowPlaying, () -> {
                    Main.nowPlayingScreen.updateCoverImage();
                    if (Main.CONFIG.shouldShowToasts() && Main.isStarted) {
                        Main.showNewSongToast();
                    }
                });
            } else {
                SongDataExtractor.reloadData(false, () -> {}, () -> {}, () -> {
                    if (Main.CONFIG.shouldShowToasts() && Main.isStarted) {
                        Main.showNewSongToast();
                    }
                });
            }
            if (Main.CONFIG.lastRefresh() + 1.8e+6 < System.currentTimeMillis()) {
                SpotifyApi.refreshAccessToken();
            }
        }

        // Locale might not be loaded yet
        String id = "ui.spotify_controller.unknown_artist";
        //#if MC>=26200
        //if (I18n.get(id) == id) return;
        //#else
        if (!I18n.exists(id)) return;
        //#endif

        if (Main.currentlyPlaying == null) Main.currentlyPlaying = SongData.emptyData();
    }
}
