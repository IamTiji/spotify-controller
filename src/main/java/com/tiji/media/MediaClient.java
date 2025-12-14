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
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MediaClient implements ClientModInitializer {
	public static final MediaConfig CONFIG = new MediaConfig();
	private static final KeyBinding SETUP_KEY = new KeyBinding("key.media.general", GLFW.GLFW_KEY_Z, "key.categories.misc");
	public static int tickCount = 0;
	public static NowPlayingScreen nowPlayingScreen = null;
    public static final String MOD_ID = "media";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static SongData currentlyPlaying = new SongData();

	public static String progressLabel = "00:00";
	public static boolean isPlaying = false;
	public static double progressValue = 0;

	public static String repeat = "off";
	public static boolean shuffle = false;
	public static boolean isLiked = false;

    public static boolean canShuffle = false;
    public static boolean canRepeat = false;
    public static boolean canSkip = false;
    public static boolean canGoBack = false;
    public static boolean canSeek = false;

    public static boolean isPremium = false;

	public static boolean isStarted = false;

	public void onInitializeClient(){
        FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(modContainer -> {
            if (!
                ResourceManagerHelper.registerBuiltinResourcePack(
                        Identifier.of("media", "higher_res"),
                        modContainer,
                        Text.translatable("rp.media.highres.title"),
                        ResourcePackActivationType.NORMAL)) MediaClient.LOGGER.error("High Resolution RP failed load!");
            }
        );

		CONFIG.generate();
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
			while (SETUP_KEY.wasPressed()) {
				if (isNotSetup()) {
					client.setScreen(new CottonClientScreen(new SetupScreen()));
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
		return CONFIG.clientId().isEmpty() || CONFIG.authToken().isEmpty() || CONFIG.refreshToken().isEmpty();
	}
    public static void showNotAllowedToast() {
        MinecraftClient.getInstance().getToastManager().add(
                new SystemToast(new SystemToast.Type(),
                        Text.translatable("ui.media.not_allowed.title"),
                        Text.translatable("ui.media.not_allowed.message"))
        );
    }
}