package com.tiji.media;

import com.tiji.media.api.ApiCalls;
import com.tiji.media.api.ImageDownloader;
import com.tiji.media.api.SongData;
import com.tiji.media.api.SongDataExtractor;
import com.tiji.media.ui.NowPlayingScreen;
import com.tiji.media.ui.SetupScreen;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;

public class MediaClient implements ClientModInitializer {
	public static final MediaConfig CONFIG = new MediaConfig();
	private static final KeyBinding SETUP_KEY = new KeyBinding("key.media.general", GLFW.GLFW_KEY_Z, "key.categories.misc");
	public static int tickCount = 0;
	public static NowPlayingScreen nowPlayingScreen = null;

	public static SongData currentlyPlaying = new SongData();

	public static String progressLabel = "00:00";
	public static boolean isPlaying = false;
	public static double progressValue = 0;

	public static String repeat = "off";
	public static boolean shuffle = false;
	public static boolean isLiked = false;

	public static boolean isStarted = false;

	public void onInitializeClient(){
		CONFIG.generate();
		KeyBindingHelper.registerKeyBinding(SETUP_KEY);
		ImageDownloader.startThreads();

		if (isNotSetup()) {
            WebGuideServer.start();
		} else {
			ApiCalls.refreshAccessToken();
		}
		ClientLifecycleEvents.CLIENT_STARTED.register((client) -> {
			SongDataExtractor.reloadData(true, () -> {}, () -> {}, () -> {});
			isStarted = true;
		});
		ClientTickEvents.END_CLIENT_TICK.register((client) -> {
			while (SETUP_KEY.wasPressed()) {
				if (isNotSetup()) {
					client.setScreen(new CottonClientScreen(new SetupScreen()));
				} else {
					nowPlayingScreen = new NowPlayingScreen();
					nowPlayingScreen.updateCoverImage();
					nowPlayingScreen.updateNowPlaying();
					client.setScreen(new CottonClientScreen(nowPlayingScreen));
				}
			}
			if (!isNotSetup() && tickCount % 10 == 0){
				if (nowPlayingScreen != null) {
					SongDataExtractor.reloadData(false, nowPlayingScreen::updateStatus, nowPlayingScreen::updateNowPlaying, () -> {
						nowPlayingScreen.updateCoverImage();
						if (CONFIG.shouldShowToasts() && isStarted) {
							new SongToast(currentlyPlaying.coverImage, currentlyPlaying.artist, currentlyPlaying.title).show(MinecraftClient.getInstance().getToastManager());
						}
					});
				} else {
					SongDataExtractor.reloadData(false, () -> {}, () -> {}, () -> {
						if (CONFIG.shouldShowToasts() && isStarted) {
							new SongToast(currentlyPlaying.coverImage, currentlyPlaying.artist, currentlyPlaying.title).show(MinecraftClient.getInstance().getToastManager());
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
	public static boolean isNotSetup() {
		return CONFIG.clientId().isEmpty() || CONFIG.accessToken().isEmpty() || CONFIG.refreshToken().isEmpty();
	}
}