package com.tiji.spotify_controller.api;

import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DevStateProvider {
    public static String getDevState() {
        StringBuilder sb = new StringBuilder();

        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            sb.append(getGitState());
        } else {
            sb.append("public-release");
        }

        return sb.toString();
    }

    private static String getGitState() {
        try {
            String branch       = git("rev-parse", "--abbrev-ref", "HEAD").trim();
            boolean diff        = git("diff-index", "HEAD").isBlank();
            String latestCommit = git("log", "--pretty=format:%h", "-1").trim();

            return String.format("%s@%s%s", branch, latestCommit, diff ? "" : "; modified");
        } catch (IOException e) {
            return "unknown; git failed";
        }
    }

    private static String git(String... args) throws IOException {
        String[] command = new String[args.length + 1];
        command[0] = "git";

        System.arraycopy(args, 0, command, 1, args.length);

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        StringBuilder output = new StringBuilder();

        while (process.isAlive() || reader.ready()) {
            int tmp = reader.read();
            if (tmp == -1) break;
            char c = (char) tmp;

            output.append(c);
        }

        return output.toString().trim();
    }
}
