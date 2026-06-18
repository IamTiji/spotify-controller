package com.tiji.spotify_controller.util;

import net.minecraft.Util;

import java.net.http.HttpResponse;
import java.util.ArrayList;

public class RequestManager {
    private static final ArrayList<Long> requestQueue = new ArrayList<>();

    public static void putRequest() {
        putRequest(500);
    }

    public static void putRequest(int delayMs) {
        requestQueue.add(Util.getMillis() + delayMs);
    }

    public static boolean pollRequest() {
        if (requestQueue.isEmpty()) return false;
        if (requestQueue.getFirst() < Util.getMillis()) {
            requestQueue.removeIf(time -> time < Util.getMillis());
            return true;
        }
        return false;
    }

    // Make it easier to put it after making a request
    public static void putRequest(@SuppressWarnings("unused") HttpResponse<String> unused) {
        putRequest();
    }
}
