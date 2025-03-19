package com.tiji.media;

import com.tiji.media.api.ApiCalls;
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

	public static String progressLabel;
	public static boolean isPlaying;
	public static double progressValue;

	public static String repeat;
	public static boolean shuffle;
	public static boolean isLiked;

	public void onInitializeClient(){
		CONFIG.generate();
		KeyBindingHelper.registerKeyBinding(SETUP_KEY);

		if (isNotSetup()){
			try{
				WebGuideServer.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			ApiCalls.refreshAccessToken();
		}
		ClientLifecycleEvents.CLIENT_STARTED.register((client) -> {
			SongDataExtractor.reloadData(true, () -> {}, () -> {}, () -> {});
		});
		ClientTickEvents.END_CLIENT_TICK.register((client) -> {
			while (SETUP_KEY.wasPressed()) {
				if (isNotSetup()) {
					client.setScreen(new CottonClientScreen(new SetupScreen()));
				}else{
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
						if (CONFIG.shouldShowToasts()) {
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
		return CONFIG.clientId().isEmpty() || CONFIG.authToken().isEmpty() || CONFIG.refreshToken().isEmpty();
	}
}