package com.tiji.media;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyInput;
import static com.tiji.media.MediaClient.SETUP_KEY;

public class CustomCottonScreen extends CottonClientScreen {

    public CustomCottonScreen(GuiDescription description) {
        super(description);
    }

    /**
     * Handles closing the screen when the setup key is pressed.
     * @param input The key input pressed.
     * @return true if the key press was handled, parent method result otherwise.
     */
    @Override
    public boolean keyPressed(KeyInput input) {
        if (SETUP_KEY.matchesKey(input)) {
            MinecraftClient.getInstance().setScreen(null);
            return true;
        }
        return super.keyPressed(input);
    }
}
