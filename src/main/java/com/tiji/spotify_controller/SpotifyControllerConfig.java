package com.tiji.spotify_controller;

import com.google.gson.Gson;
import com.tiji.spotify_controller.widgets.BooleanToggleWidget;
import com.tiji.spotify_controller.widgets.IntInputWidget;
import com.tiji.spotify_controller.widgets.ValueHolder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

public class SpotifyControllerConfig {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface EditableField {
        String translationKey();
        Class<? extends ValueHolder> widget();
    }

    private String clientId = "";
    private String clientSecret = "";
    private String authToken = "";
    private String refreshToken = "";
    private long lastRefresh = 0;

    @EditableField(translationKey = "ui.spotify_controller.config.show_toast"       , widget = BooleanToggleWidget.class)
    private boolean shouldShowToasts = true;

    @EditableField(translationKey = "ui.spotify_controller.config.thread_image_io"  , widget = IntInputWidget.class)
    private int imageIoThreadCount  = 4;

    @EditableField(translationKey = "ui.spotify_controller.config.brightness_weight", widget = IntInputWidget.class)
    private int brightnessFactor     = 20;

    @EditableField(translationKey = "ui.spotify_controller.config.saturation_weight", widget = IntInputWidget.class)
    private int saturationFactor     = 5;

    @EditableField(translationKey = "ui.spotify_controller.config.target_brightness", widget = IntInputWidget.class)
    private int targetBrightness     = 70;

    @EditableField(translationKey = "ui.spotify_controller.config.sample_size"      , widget = IntInputWidget.class)
    private int sampleSize           = 10;

    public String clientId() {return clientId;}
    public void clientId(String value) {clientId = value; writeToFile();}

    public String clientSecret() {return clientSecret;}
    public void clientSecret(String value) {clientSecret = value; writeToFile();}

    public String authToken() {return authToken;}
    public void authToken(String value) {authToken = value; writeToFile();}

    public String refreshToken() {return refreshToken;}
    public void refreshToken(String value) {refreshToken = value; writeToFile();}

    public long lastRefresh() {return lastRefresh;}
    public void lastRefresh(long value) {lastRefresh = value; writeToFile();}

    public boolean shouldShowToasts() {return shouldShowToasts;}
    public void shouldShowToasts(boolean value) {shouldShowToasts = value; writeToFile();}

    public int imageIoThreadCount() {return imageIoThreadCount;}
    public void imageIoThreadCount(byte value) {imageIoThreadCount = value; writeToFile();}

    public int brightnessFactor() {return brightnessFactor;}
    public void brightnessFactor(int value) {brightnessFactor = value; writeToFile();}

    public int saturationFactor() {return saturationFactor;}
    public void saturationFactor(int value) {saturationFactor = value; writeToFile();}

    public int targetBrightness() {return targetBrightness;}
    public void targetBrightness(int value) {targetBrightness = value; writeToFile();}

    public int sampleSize() {return sampleSize;}
    public void sampleSize(int value) {sampleSize = value; writeToFile();}

    public static SpotifyControllerConfig generate() {
        Path configDir = FabricLoader.getInstance().getConfigDir();

        Path configPath = configDir.resolve("spotify_controller.json");
        Path oldPath = configDir.resolve("media.json");

        try {
            if (Files.exists(configPath)) {
                String json = Files.readString(configPath);

                return new Gson().fromJson(json, SpotifyControllerConfig.class);
            } else if (Files.exists(oldPath)) {
                Files.copy(oldPath, configPath, StandardCopyOption.REPLACE_EXISTING);

                return generate();
            } else {
                SpotifyControllerConfig newInstance = new SpotifyControllerConfig();
                newInstance.writeToFile(false);
                return newInstance;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeToFile(boolean delete) {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("spotify_controller.json");

        String config = new Gson().toJson(this);

        try {
            Files.writeString(configPath, config, delete ? StandardOpenOption.TRUNCATE_EXISTING : StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void writeToFile() {
        writeToFile(true);
    }
    public void resetConnection() {
        this.lastRefresh(0);
        this.clientId("");
        this.clientSecret("");
        this.authToken("");
        this.refreshToken("");
    }
}