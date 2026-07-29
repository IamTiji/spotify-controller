package com.tiji.spotify_controller;

import com.tiji.spotify_controller.util.SafeScreenUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Deque;

public class DiagnosticData {
    public static class CappedList<T> extends ArrayDeque<T> {
        private final int capacity;

        public CappedList(int capacity) {
            this.capacity = capacity;
        }

        @Override
        public boolean add(@NotNull T t) {
            if (capacity <= super.size()) {
                super.removeFirst();
            }

            return super.add(t);
        }
    }

    public static final Deque<String> lastRequests = new CappedList<>(20);
    public static final Deque<String> lastFailedRequests = new CappedList<>(20);

    public static void dump() {
        SafeScreenUtils.getToastManager(Minecraft.getInstance())
                        .addToast(new SystemToast(Main.SYSTEM_TOAST_ID,
                                Component.translatable("ui.spotify_controller.dump_diagnostic"),
                                Component.translatable("ui.spotify_controller.dump_diagnostic.response")));

        Main.LOGGER.info("=========== Spotify Controller Diagnostic Dump ===========");

        Main.LOGGER.info("Last {} requests:", lastRequests.size());
        for (String element : lastRequests) {
            Main.LOGGER.info("{}\n", element);
        }

        Main.LOGGER.info("Last {} failed requests", lastFailedRequests.size());
        for (String element : lastFailedRequests) {
            Main.LOGGER.info("{}\n", element);
        }
    }
}
