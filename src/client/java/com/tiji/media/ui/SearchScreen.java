package com.tiji.media.ui;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tiji.media.MediaClient;
import com.tiji.media.api.ApiCalls;
import com.tiji.media.widgets.songListItem;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SearchScreen extends LightweightGuiDescription {
    private static class RootPanel extends WPlainPanel {
        public void onHidden() {
            MediaClient.nowPlayingScreen = null;
        }
    }
    private static class SongList extends WPlainPanel {
        private Runnable scheduledTask;
        public void clear() {
            if (children.isEmpty()) return;
            children.clear();
        }
        public void addTask(Runnable task) {
            scheduledTask = task;
        }
        public void paint(DrawContext context, int x, int y, int mouseX, int mouseY){
            super.paint(context, x, y, mouseX, mouseY);
            if (scheduledTask!= null) {
                scheduledTask.run();
                scheduledTask = null;
            }
        }
    }
    private static class SearchInput extends WTextField {
        private @Nullable Consumer<String> consumer;
        public SearchInput() {
            super();
        }

        @Override
        public InputResult onKeyReleased(int ch, int key, int modifiers) {
            InputResult result = super.onKeyReleased(ch, key, modifiers);
            if (consumer != null && !getText().isEmpty()) consumer.accept(getText());
            return result;
        }
        public void setOnSearch(Consumer<String> consumer) {
            this.consumer = consumer;
        }
    }

    SearchInput searchField = new SearchInput();
    SongList listBox = new SongList();
    WScrollPanel scrollPanel = new WScrollPanel(listBox)
            .setScrollingVertically(TriState.TRUE)
            .setScrollingHorizontally(TriState.FALSE);

    public SearchScreen() {
        WPlainPanel root = new RootPanel();

        setRootPanel(root);
        root.setSize(300, 200);
        root.setInsets(Insets.NONE);

        searchField.setMaxLength(100);
        searchField.setOnSearch((q) -> {
            ApiCalls.getSearch(q, results -> {
                listBox.addTask(() -> {
                    listBox.clear();
                    if (results.isEmpty()) return;
                    int y = 0;
                    for (JsonElement result : results) {
                        JsonObject jsonObject = result.getAsJsonObject();
                        songListItem item = new songListItem(jsonObject);
                        listBox.add(item, 0, y);
                        y += 50;
                        root.validate(this);
                    }
                });
            });
        });
        root.add(searchField, 10, 10, 280, 20);

        listBox.setInsets(Insets.NONE);
        root.add(scrollPanel, 10, 40, 280, 150);

        root.validate(this);
    }
}
