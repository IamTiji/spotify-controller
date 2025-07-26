package com.tiji.media;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

public class SetupScreen extends LightweightGuiDescription {
    public SetupScreen() {
        WPlainPanel root = new WPlainPanel();
        root.setSize(300, 200);
        root.setInsets(Insets.NONE);

        WLabel welcomeLabel = new WLabel(Text.translatable("ui.media.welcome"));
        welcomeLabel.setHorizontalAlignment(HorizontalAlignment.CENTER);
        welcomeLabel.setColor(0x000000).setDarkmodeColor(0xFFFFFF);
        root.add(welcomeLabel, 100, 20, 100, 20);

        WLabel setupLabel1 = new WLabel(Text.translatable("ui.media.welcome.subtext1"));
        setupLabel1.setHorizontalAlignment(HorizontalAlignment.CENTER);
        setupLabel1.setColor(0x000000).setDarkmodeColor(0xFFFFFF);
        root.add(setupLabel1, 100, 50, 100, 20);

        WLabel setupLabel2 = new WLabel(Text.translatable("ui.media.welcome.subtext2"));
        setupLabel2.setHorizontalAlignment(HorizontalAlignment.CENTER);
        setupLabel2.setColor(0x000000).setDarkmodeColor(0xFFFFFF);
        root.add(setupLabel2, 100, 65, 100, 20);

        WButton linkLabel = new WButton(Text.literal("http://127.0.0.1:25566").formatted(Formatting.UNDERLINE));
        linkLabel.setOnClick(() -> Util.getOperatingSystem().open("http://127.0.0.1:25566"));
        root.add(linkLabel, 75, 100, 150, 20);

        root.validate(this);
        setRootPanel(root);
    }
}