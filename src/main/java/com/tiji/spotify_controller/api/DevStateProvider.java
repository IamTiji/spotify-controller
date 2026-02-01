package com.tiji.spotify_controller.api;

public class DevStateProvider {
    public static String getDevState() {
        StringBuilder sb = new StringBuilder();

        if (System.getProperty("com.tiji.development") != null) {
            sb.append("dev ");
            sb.append(System.getProperty("com.tiji.git_state"));
            sb.append(" ");
        }

        return sb.toString();
    }
}
