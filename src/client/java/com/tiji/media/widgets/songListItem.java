package com.tiji.media.widgets;

import com.google.gson.JsonObject;
import com.tiji.media.api.ApiCalls;
import com.tiji.media.api.SongData;
import com.tiji.media.api.SongDataExtractor;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WSprite;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class songListItem extends WPlainPanel {
    SongData songData = new SongData();

    WSprite songIcon = new WSprite(songData.coverImage.image);
    WLabel songTitle = new WLabel(Text.empty());
    WLabel artist = new WLabel(Text.empty());

    final Style ICON = Style.EMPTY.withFont(Identifier.of("media", "icon"));

    public songListItem(JsonObject data) {
        super();

        songData = SongDataExtractor.getDataFor(data, () -> {
            songIcon.setImage(songData.coverImage.image);
        });
        songTitle.setText(songData.title);
        artist.setText(Text.literal(songData.artist));

        add(songIcon, 0, 0, 50, 50);
        add(songTitle, 65, 5, 200, 20);
        add(artist, 65, 15, 200, 20);

        add(new borderlessButtonWidget(Text.literal("e").setStyle(ICON)).setOnClick(() -> {
            ApiCalls.setPlayingSong(songData.Id);
        }).setAlignment(HorizontalAlignment.LEFT), 65, 30, 20, 20);
        add(new borderlessButtonWidget(Text.literal("f").setStyle(ICON)).setOnClick(() -> {
            ApiCalls.addSongToQueue(songData.Id);
        }).setAlignment(HorizontalAlignment.CENTER), 85, 30, 20, 20);
        setSize(230, 50);
    }
}
