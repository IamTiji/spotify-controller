package com.tiji.media;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
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
    private int brightnessFactor = 20;
    private int saturationFactor = 5;
    private int targetBrightness = 70;
    private int sampleSize = 10;

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

    public int brightnessFactor() {return brightnessFactor;}
    public void brightnessFactor(int value) {brightnessFactor = value; writeToFile();}

    public int saturationFactor() {return saturationFactor;}
    public void saturationFactor(int value) {saturationFactor = value; writeToFile();}

    public int targetBrightness() {return targetBrightness;}
    public void targetBrightness(int value) {targetBrightness = value; writeToFile();}

    public int sampleSize() {return sampleSize;}
    public void sampleSize(int value) {sampleSize = value; writeToFile();}

    public void generate() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("media.json");
        if (Files.exists(configPath)) {
            try {
                String json = Files.readString(configPath);
                JsonObject config = new Gson().fromJson(json, JsonObject.class);

                clientId =              config.get("clientId")          .getAsString();
                clientSecret =          config.get("clientSecret")      .getAsString();
                accessToken =           config.get("accessToken")       .getAsString();
                authToken =             config.get("authToken")         .getAsString();
                refreshToken =          config.get("refreshToken")      .getAsString();
                lastRefresh =           config.get("lastRefresh")       .getAsLong();
                shouldShowToasts =      config.get("shouldShowToasts")  .getAsBoolean();
                imageIoThreadCount =    config.get("imageIoThreadCount").getAsByte();
                brightnessFactor =      config.get("brightnessFactor")  .getAsInt();
                saturationFactor =      config.get("saturationFactor")  .getAsInt();
                targetBrightness =      config.get("targetBrightness")  .getAsInt();
                sampleSize =            config.get("sampleSize")        .getAsInt();
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
        config.addProperty("clientId",              clientId);
        config.addProperty("clientSecret",          clientSecret);
        config.addProperty("accessToken",           accessToken);
        config.addProperty("authToken",             authToken);
        config.addProperty("refreshToken",          refreshToken);
        config.addProperty("lastRefresh",           lastRefresh);
        config.addProperty("shouldShowToasts",      shouldShowToasts);
        config.addProperty("imageIoThreadCount",    imageIoThreadCount);
        config.addProperty("brightnessFactor",      brightnessFactor);
        config.addProperty("saturationFactor",      saturationFactor);
        config.addProperty("targetBrightness",      targetBrightness);
        config.addProperty("sampleSize",            sampleSize);

        try {
            Files.write(configPath, new Gson().toJson(config).getBytes(), delete ? StandardOpenOption.TRUNCATE_EXISTING : StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
        this.brightnessFactor(20);
        this.saturationFactor(5);
        this.targetBrightness(70);
        this.sampleSize(10);
    }
}