package com.tiji.spotify_controller.ui;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tiji.spotify_controller.api.SpotifyApi;
import com.tiji.spotify_controller.widgets.ScrollableArea;
import com.tiji.spotify_controller.widgets.SongListItem;
import com.tiji.spotify_controller.widgets.StringInputWidget;
import net.minecraft.network.chat.Component;

//#if MC>=12109
//$$ import net.minecraft.client.input.MouseButtonEvent;
//#endif

public class SearchScreen extends SecondaryBaseScreen {
    private static final int WIDTH = 300;
    private static final int MARGIN = 10;
    public static final int SEARCH_BAR_HEIGHT = 20;

    private ScrollableArea scrollArea;

    @Override
    protected void init() {
        super.init();

        StringInputWidget searchField = new StringInputWidget(font,
                MARGIN, MARGIN,
                WIDTH - MARGIN*2, SEARCH_BAR_HEIGHT, Component.empty(),
                Icons.SEARCH, this::search);
        addRenderableWidget(searchField);
        setFocused(searchField);

        scrollArea = new ScrollableArea(MARGIN, MARGIN*2 + SEARCH_BAR_HEIGHT,
                WIDTH - MARGIN*2, height - INFO_HEIGHT - MARGIN*2 - SEARCH_BAR_HEIGHT);
        addRenderableWidget(scrollArea);
    }

    private void search(String query) {
        if (query.isEmpty()) return;

        scrollArea.clearWidgets();

        SpotifyApi.getSearch(query, results -> {
                int y = 0;
                for (JsonElement result : results) {
                    JsonObject jsonObject = result.getAsJsonObject();
                    SongListItem item = new SongListItem(jsonObject, 0, y);
                    scrollArea.addWidget(item);

                    y += SongListItem.HEIGHT + MARGIN;
                }
        });
    }

    @Override
    public boolean mouseScrolled(double d, double e, double f, double g) {
        return super.mouseScrolled(d, e, f, g);
    }
}