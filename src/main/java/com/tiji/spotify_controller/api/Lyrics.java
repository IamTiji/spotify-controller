package com.tiji.spotify_controller.api;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lyrics {
    public final List<String> lines;
    public final List<Integer> timestamps;
    public final boolean isLineSynced;

    private static final String LINE_BREAK_REGEX = "(\\r\\n|\\r|\\n)";
    private static final Pattern LRC_LINE_REGEX = Pattern.compile("\\[(?<hour>\\d{1,3}):(?<minute>\\d{1,3}).(?<second>\\d{1,3})] ?(?<content>.*)");

    private static final String EMPTY_LINE_REPLACEMENT = "♪";

    private Lyrics(List<String> lines, List<Integer> timestamps, boolean isLineSynced) {
        this.lines = lines;
        this.timestamps = timestamps;
        this.isLineSynced = isLineSynced;
    }

    public static Lyrics synced(String data) {
        data = data.strip();
        String[] linesAndTimestamps = data.split(LINE_BREAK_REGEX);

        String[] lines = new String[linesAndTimestamps.length];
        Integer[] timestamps = new Integer[linesAndTimestamps.length];

        for (int i = 0; i < linesAndTimestamps.length; i++) {
            String line = linesAndTimestamps[i];
            if (line.charAt(0) == '\uFEFF') { line = line.substring(1); } // why bom not whitespace
            Matcher match = LRC_LINE_REGEX.matcher(line);
            if (!match.find()) throw new IllegalStateException("Failed to parse lyric");

            int time     = Integer.parseInt(match.group(1).strip()) * 60 * 1000;
            time        += Integer.parseInt(match.group(2).strip()) * 1000;
            time        += Integer.parseInt(String.format("%-3s", match.group(3).strip())
                                                .replace(" ", "0"));

            String text = match.group(4).strip();

            if (text.isEmpty()) text = EMPTY_LINE_REPLACEMENT;

            lines[i] = text;
            timestamps[i] = time;
        }

        int sizeOffset = lines[lines.length-1].equals(EMPTY_LINE_REPLACEMENT) ? 1 : 0;

        return new Lyrics(
            Arrays.asList(lines     ).subList(0, lines     .length - sizeOffset),
            Arrays.asList(timestamps).subList(0, timestamps.length - sizeOffset),
            true
        );
    }

    public static Lyrics plain(String data) {
        data = data.strip();
        String[] lines = data.split(LINE_BREAK_REGEX);

        int sizeOffset = lines[lines.length-1].isEmpty() ? 1 : 0;

        return new Lyrics(
            Arrays.asList(lines).subList(0, lines.length - sizeOffset),
            Collections.nCopies(lines.length - sizeOffset, 0),
            false
        );
    }

    public static Lyrics empty() {
        return new Lyrics(List.of(), List.of(), false);
    }
}
