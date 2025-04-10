package com.tiji.media;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tiji.media.util.displayMode;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class MediaConfig {
    private String clientId = "";
    private String clientSecret = "";
    private String accessToken = "";
    private String authToken = "";
    private String refreshToken = "";
    private long lastRefresh = 0;
    private boolean shouldShowToasts = true;
    private byte imageIoThreadCount = 4;
    private displayMode displayColor = displayMode.THEME;
    private int brightnessFactor = 5;
    private int saturationFactor = 5;

    public String clientId() {return clientId;}
    public void clientId(String value) {clientId = value; writeToFile();}

    public String clientSecret() {return clientSecret;}
    public void clientSecret(String value) {clientSecret = value; writeToFile();}

    public String accessToken() {return accessToken;}
    public void accessToken(String value) {accessToken = value; writeToFile();}

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

    public displayMode displayColor() {return displayColor;}
    public void displayColor(displayMode value) {displayColor = value; writeToFile();}

    public int brightnessFactor() {return brightnessFactor;}
    public void brightnessFactor(int value) {brightnessFactor = value; writeToFile();}

    public int saturationFactor() {return saturationFactor;}
    public void saturationFactor(int value) {saturationFactor = value; writeToFile();}

    public void generate() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("media.json");
        if (Files.exists(configPath)) {
            try {
                String json = Files.readString(configPath);
                JsonObject config = new Gson().fromJson(json, JsonObject.class);
                clientId = config.get("clientId").getAsString();
                clientSecret = config.get("clientSecret").getAsString();
                accessToken = config.get("accessToken").getAsString();
                authToken = config.get("authToken").getAsString();
                refreshToken = config.get("refreshToken").getAsString();
                lastRefresh = config.get("lastRefresh").getAsLong();
                shouldShowToasts = config.get("shouldShowToasts").getAsBoolean();
                imageIoThreadCount = config.get("imageIoThreadCount").getAsByte();
                displayColor = displayMode.valueOf(config.get("displayColor").getAsString());
                brightnessFactor = config.get("brightnessFactor").getAsInt();
                saturationFactor = config.get("saturationFactor").getAsInt();
            }catch (Exception e) {
                writeToFile();
            }
        }else{
            writeToFile(false);
        }
    }
    public void writeToFile(boolean delete) {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("media.json");

        JsonObject config = new JsonObject();
        config.addProperty("clientId", clientId);
        config.addProperty("clientSecret", clientSecret);
        config.addProperty("accessToken", accessToken);
        config.addProperty("authToken", authToken);
        config.addProperty("refreshToken", refreshToken);
        config.addProperty("lastRefresh", lastRefresh);
        config.addProperty("shouldShowToasts", shouldShowToasts);
        config.addProperty("imageIoThreadCount", imageIoThreadCount);
        config.addProperty("displayColor", displayColor.name());
        config.addProperty("brightnessFactor", brightnessFactor);
        config.addProperty("saturationFactor", saturationFactor);
        try {
            Files.write(configPath, new Gson().toJson(config).getBytes(), delete ? StandardOpenOption.TRUNCATE_EXISTING : StandardOpenOption.CREATE_NEW);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void writeToFile() {
        writeToFile(true);
    }
    public void reset() {
        this.lastRefresh(0);
        this.clientId("");
        this.clientSecret("");
        this.accessToken("");
        this.authToken("");
        this.refreshToken("");
        this.shouldShowToasts(true);
        this.imageIoThreadCount((byte) 4);
    }
}