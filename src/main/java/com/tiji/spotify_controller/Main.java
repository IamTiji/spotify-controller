package com.tiji.spotify_controller;

import com.tiji.spotify_controller.api.ApiCalls;
import com.tiji.spotify_controller.api.ImageDownloader;
import com.tiji.spotify_controller.api.SongData;
import com.tiji.spotify_controller.api.SongDataExtractor;
import com.tiji.spotify_controller.ui.NowPlayingScreen;
import com.tiji.spotify_controller.ui.SetupScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ClientModInitializer {
	public static final String            MOD_ID    = "spotify_controller";
	public static final Logger            LOGGER    = LoggerFactory.getLogger(MOD_ID);
	public static SpotifyControllerConfig CONFIG    = new SpotifyControllerConfig();
	private static final KeyMapping       SETUP_KEY =
			//#if MC<=12108
			new KeyMapping("key.spotify_controller.general", GLFW.GLFW_KEY_Z, "key.categories.misc");
			//#else
			//$$ new KeyMapping("key.spotify_controller.general", GLFW.GLFW_KEY_Z, KeyMapping.Category.MISC);
			//#endif

	public static int tickCount = 0;
	public static NowPlayingScreen nowPlayingScreen = null;

	public static SongData currentlyPlaying = SongData.emptyData();
	public static PlaybackState playbackState = new PlaybackState();

	public static boolean isPremium = false;

	public static boolean isStarted = false;

	public void onInitializeClient(){
        FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(modContainer -> {
            if (!
                ResourceManagerHelper.registerBuiltinResourcePack(
                        ResourceLocation.fromNamespaceAndPath(MOD_ID, "higher_res"),
                        modContainer,
                        ResourcePackActivationType.NORMAL)) Main.LOGGER.error("High Resolution RP failed load!");
            }
        );

		CONFIG = SpotifyControllerConfig.generate();
		KeyBindingHelper.registerKeyBinding(SETUP_KEY);
		ImageDownloader.startThreads();

		if (isNotSetup()) {
            WebGuideServer.start();
		} else {
			ApiCalls.refreshAccessToken();
		}
		ClientLifecycleEvents.CLIENT_STARTED.register((client) -> {
            isStarted = true;
            if (!isNotSetup()) {
                SongDataExtractor.reloadData(true, () -> {}, () -> {}, () -> {});
            }
        });
		ClientTickEvents.END_CLIENT_TICK.register((client) -> {
			while (SETUP_KEY.consumeClick()) {
				if (isNotSetup()) {
					client.setScreen(new SetupScreen());
				} else {
					nowPlayingScreen = new NowPlayingScreen();
					nowPlayingScreen.updateCoverImage();
					nowPlayingScreen.updateNowPlaying();
					client.setScreen(nowPlayingScreen);
				}
			}
			if (!isNotSetup() && tickCount % 10 == 0){
				if (nowPlayingScreen != null) {
					SongDataExtractor.reloadData(false, nowPlayingScreen::updateStatus, nowPlayingScreen::updateNowPlaying, () -> {
						nowPlayingScreen.updateCoverImage();
						if (CONFIG.shouldShowToasts() && isStarted) {
                            showNewSongToast();
                        }
					});
				} else {
					SongDataExtractor.reloadData(false, () -> {}, () -> {}, () -> {
						if (CONFIG.shouldShowToasts() && isStarted) {
                            showNewSongToast();
                        }
					});
				}
				if (CONFIG.lastRefresh() + 1.8e+6 < System.currentTimeMillis()) {
					ApiCalls.refreshAccessToken();
				}
			}
			tickCount++;
		});
	}

    private static void showNewSongToast() {
        new SongToast(currentlyPlaying.coverImage, currentlyPlaying.artist, currentlyPlaying.title).show(Minecraft.getInstance().getToastManager());
    }

    public static boolean isNotSetup() {
		return CONFIG.clientId().isEmpty() || CONFIG.authToken().isEmpty() || CONFIG.refreshToken().isEmpty();
	}
    public static void showNotAllowedToast() {
        Minecraft.getInstance().getToastManager().addToast(
                new SystemToast(new SystemToast.SystemToastId(),
                        Component.translatable("ui.spotify_controller.not_allowed.title"),
                        Component.translatable("ui.spotify_controller.not_allowed.message"))
        );
    }
}