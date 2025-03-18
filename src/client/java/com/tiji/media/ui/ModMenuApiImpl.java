package com.tiji.media.ui;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import net.minecraft.client.MinecraftClient;

public class ModMenuApiImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> new CottonClientScreen(new MediaConfigScreen() {
            @Override
            public void close() {
                MinecraftClient.getInstance().setScreen(screen);
            }
        });
    }
}
