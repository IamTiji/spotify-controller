package com.tiji.spotify_controller.api;

import java.util.Collections;
import java.util.List;

public class Lyrics {
    public final List<String> lines;
    public final List<Integer> timestamps;

    private static final String LINE_BREAK_REGEX = "(\\r\\n|\\r|\\n)";

    private Lyrics(List<String> lines, List<Integer> timestamps) {
        this.lines = lines;
        this.timestamps = timestamps;
    }

    public static Lyrics synced(String data) {
        data = data.strip();
        String[] linesAndTimestamps = data.split(LINE_BREAK_REGEX);

        String[] lines = new String[linesAndTimestamps.length];
        Integer[] timestamps = new Integer[linesAndTimestamps.length];

        for (int i = 0; i < linesAndTimestamps.length; i++) {
            String line = linesAndTimestamps[i];

            int time     = Integer.parseInt(line.substring(1, 3)) * 60 * 1000;
            time        += Integer.parseInt(line.substring(4, 6)) * 1000;
            time        += Integer.parseInt(line.substring(7, 9)) * 10;

            String text = line.substring(10).trim();

            lines[i] = text;
            timestamps[i] = time;
        }

        return new Lyrics(List.of(lines), List.of(timestamps));
    }

    public static Lyrics plain(String data) {
        data = data.strip();
        String[] lines = data.split(LINE_BREAK_REGEX);

        return new Lyrics(List.of(lines), Collections.nCopies(lines.length, 0));
    }
}
