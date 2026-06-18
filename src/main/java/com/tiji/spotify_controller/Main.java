package com.tiji.spotify_controller;

import com.tiji.spotify_controller.api.SpotifyApi;
import com.tiji.spotify_controller.api.ImageDownloader;
import com.tiji.spotify_controller.api.SongData;
import com.tiji.spotify_controller.api.SongDataExtractor;
import com.tiji.spotify_controller.ui.NowPlayingScreen;
import com.tiji.spotify_controller.ui.SetupScreen;
import com.tiji.spotify_controller.util.SafeScreenUtils;
import com.tiji.spotify_controller.util.TextUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class Main implements ClientModInitializer {
	public static final String            MOD_ID    = "spotify_controller";
	public static final Logger            LOGGER    = LoggerFactory.getLogger(MOD_ID);
	public static SpotifyControllerConfig CONFIG    = new SpotifyControllerConfig();
	public static final KeyMapping        SETUP_KEY =
			//#if MC<=12108
			new KeyMapping("key.spotify_controller.general", GLFW.GLFW_KEY_Z, "key.categories.misc");
			//#else
			//$$ new KeyMapping("key.spotify_controller.general", GLFW.GLFW_KEY_Z, KeyMapping.Category.MISC);
			//#endif

	public static long lastUploadMs = 0;
    public static final int UPDATE_INTERVAL_MS = 5000;
	public static NowPlayingScreen nowPlayingScreen = null;

    public static SongData currentlyPlaying;
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

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(
            new ReloadListener());

        CONFIG = SpotifyControllerConfig.generate();
		KeyBindingHelper
                //#if MC<=12111
                .registerKeyBinding(SETUP_KEY);
                //#else
                //$$ .registerKeyMapping(SETUP_KEY);
                //#endif
		ImageDownloader.startThreads();

		if (isNotSetup()) {
            WebGuideServer.start();
		} else {
			SpotifyApi.refreshAccessToken();
		}
		ClientLifecycleEvents.CLIENT_STARTED.register((unused) -> {
            isStarted = true;
            if (!isNotSetup()) {
                SongDataExtractor.reloadData(true, () -> {}, () -> {}, () -> {});
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            while (SETUP_KEY.consumeClick()) {
                if (isNotSetup()) {
                    SafeScreenUtils.setScreen(client, new SetupScreen());
                } else {
                    nowPlayingScreen = new NowPlayingScreen();
                    nowPlayingScreen.updateCoverImage();
                    nowPlayingScreen.updateNowPlaying();
                    SafeScreenUtils.setScreen(client, nowPlayingScreen);
                }
            }
        });
	}

    public static void showNewSongToast() {
        new SongToast(currentlyPlaying.coverImage, currentlyPlaying.artist, currentlyPlaying.title)
                .show(SafeScreenUtils.getToastManager(Minecraft.getInstance()));
    }

    public static boolean isNotSetup() {
		return CONFIG.clientId().isEmpty() || CONFIG.authToken().isEmpty() || CONFIG.refreshToken().isEmpty();
	}
    public static void showNotAllowedToast() {
        SafeScreenUtils.getToastManager(Minecraft.getInstance()).addToast(
                new SystemToast(new SystemToast.SystemToastId(),
                        Component.translatable("ui.spotify_controller.not_allowed.title"),
                        Component.translatable("ui.spotify_controller.not_allowed.message"))
        );
    }
}