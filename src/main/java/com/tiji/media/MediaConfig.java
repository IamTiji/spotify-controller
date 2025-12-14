package com.tiji.media;

import com.google.gson.Gson;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class MediaConfig {
    private String clientId = "";
    private String clientSecret = "";
    private String authToken = "";
    private String refreshToken = "";
    private long lastRefresh = 0;
    private boolean shouldShowToasts = true;
    private byte imageIoThreadCount = 4;
    private int brightnessFactor = 20;
    private int saturationFactor = 5;
    private int targetBrightness = 70;
    private int sampleSize = 10;

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

    public byte imageIoThreadCount() {return imageIoThreadCount;}
    public void imageIoThreadCount(byte value) {imageIoThreadCount = value; writeToFile();}

    public int brightnessFactor() {return brightnessFactor;}
    public void brightnessFactor(int value) {brightnessFactor = value; writeToFile();}

    public int saturationFactor() {return saturationFactor;}
    public void saturationFactor(int value) {saturationFactor = value; writeToFile();}

    public int targetBrightness() {return targetBrightness;}
    public void targetBrightness(int value) {targetBrightness = value; writeToFile();}

    public int sampleSize() {return sampleSize;}
    public void sampleSize(int value) {sampleSize = value; writeToFile();}

    public static MediaConfig generate() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("media.json");
        if (Files.exists(configPath)) {
            try {
                String json = Files.readString(configPath);

                return new Gson().fromJson(json, MediaConfig.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            MediaConfig newInstance = new MediaConfig();
            newInstance.writeToFile(false);
            return newInstance;
        }
    }

    public void writeToFile(boolean delete) {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("media.json");

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