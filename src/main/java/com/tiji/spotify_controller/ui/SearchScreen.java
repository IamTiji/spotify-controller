package com.tiji.spotify_controller.ui;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tiji.spotify_controller.api.ApiCalls;
import com.tiji.spotify_controller.widgets.SongListItem;
import com.tiji.spotify_controller.widgets.StringInputWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.text.Text;

import java.util.ArrayList;

public class SearchScreen extends SecondaryBaseScreen {
    private static final int WIDTH = 300;
    private static final int MARGIN = 10;
    private static final int SCROLLBAR_WIDTH = 4;

    private final ArrayList<Drawable> searchResults = new ArrayList<>(20);
    private float scrollBarPos;
    private int offset;

    @Override
    protected void init() {
        super.init();

        StringInputWidget searchField = new StringInputWidget(textRenderer,
                MARGIN, MARGIN,
                WIDTH - MARGIN * 2, 20, Text.empty(),
                Icons.SEARCH, this::search);
        addDrawableChild(searchField);
        setFocused(searchField);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        int resultSpace     = height - MARGIN*2 - 20 - INFO_HEIGHT;
        int contentHeight   = Math.max(0, searchResults.size() * (SongListItem.HEIGHT + MARGIN) - MARGIN);
        int scrollBarSize   = (int) ((float) resultSpace / contentHeight * resultSpace);
        int scrollBarPos    = (int) ((resultSpace - scrollBarSize) * this.scrollBarPos);

        offset = 0;
        if (resultSpace < contentHeight) {
            context.fill(WIDTH - SCROLLBAR_WIDTH - MARGIN, MARGIN*2 + 20,
                    WIDTH - MARGIN, height - INFO_HEIGHT,
                    0xAAFFFFFF);
            context.fill(WIDTH - SCROLLBAR_WIDTH - MARGIN, MARGIN*2 + 20 + scrollBarPos,
                    WIDTH - MARGIN, MARGIN*2 + 20 + scrollBarPos + scrollBarSize,
                    0xFFFFFFFF);
            offset = -(int) ((contentHeight - resultSpace) * this.scrollBarPos);
        }

        //#if MC>=12102
        context.enableScissor(0, MARGIN*2 + 20, WIDTH - SCROLLBAR_WIDTH - MARGIN, height - INFO_HEIGHT);
        //#else
        //$$ context.enableScissor(0, MARGIN*2 + 20 + offset, WIDTH - SCROLLBAR_WIDTH - MARGIN, height - INFO_HEIGHT + offset);
        //#endif

        //#if MC>=12106
        //$$ context.getMatrices().pushMatrix();
        //$$ context.getMatrices().translate(0, offset);
        //#else
        context.getMatrices().push();
        context.getMatrices().translate(0, offset, 0);
        //#endif

        synchronized (searchResults) {
            for (Drawable searchResult : searchResults) {
                searchResult.render(context, mouseX, mouseY - offset, delta);
            }
        }

        //#if MC>=12106
        //$$ context.getMatrices().popMatrix();
        //#else
        context.getMatrices().pop();
        //#endif

        context.disableScissor();
    }

    private void search(String query) {
        if (query.isEmpty()) return;

        for (Drawable searchResult : searchResults) {
            remove((Element) searchResult);
        }
        synchronized (searchResults) {
            searchResults.clear();
            scrollBarPos = 0;
        }

        ApiCalls.getSearch(query, results -> {
            synchronized (searchResults) {
                int y = MARGIN*2 + 20;
                for (JsonElement result : results) {
                    JsonObject jsonObject = result.getAsJsonObject();
                    SongListItem item = new SongListItem(jsonObject, MARGIN, y);
                    searchResults.add(item);

                    y += SongListItem.HEIGHT + MARGIN;
                }
            }
        });
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scrollBarPos = (float) Math.clamp(scrollBarPos - verticalAmount * 0.03f, 0f, 1f);
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!super.mouseClicked(mouseX, mouseY, button)) {
            for (Drawable searchResult : searchResults) {
                Element selectable = (Element) searchResult;
                if (selectable.mouseClicked(mouseX, mouseY - offset, button))
                    return true;
            }
        }
        return false;
    }
}