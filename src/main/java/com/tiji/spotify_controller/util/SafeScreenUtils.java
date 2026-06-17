package com.tiji.spotify_controller.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.gui.screens.Screen;

public class SafeScreenUtils {
    public static void setScreen(Minecraft mc, Screen screen) {
        //#if MC>=26200
        //$$ mc.gui.setScreen(screen);
        //#else
        mc.setScreen(screen);
        //#endif
    }

    public static Screen getScreen(Minecraft mc) {
        //#if MC>=26200
        //$$ return mc.gui.screen();
        //#else
        return mc.screen;
        //#endif
    }

    public static ToastManager getToastManager(Minecraft mc) {
        //#if MC>=26200
        //$$ return mc.gui.toastManager();
        //#else
        return mc.getToastManager();
        //#endif
    }
}
