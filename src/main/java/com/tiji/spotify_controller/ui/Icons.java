package com.tiji.spotify_controller.ui;

import com.tiji.spotify_controller.Main;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

public class Icons {
    public static final ResourceLocation ICON_ID = ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "icon");
    private static final Style ICONS = Style.EMPTY.withFont(ICON_ID);
    private static final Component RESETTER = Component.literal("").setStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT));


    public static final MutableComponent NEXT =             Component.literal("1").setStyle(ICONS).append(RESETTER);
    public static final MutableComponent PREVIOUS =         Component.literal("0").setStyle(ICONS).append(RESETTER);
    public static final MutableComponent PAUSE =            Component.literal("2").setStyle(ICONS).append(RESETTER);
    public static final MutableComponent RESUME =           Component.literal("3").setStyle(ICONS).append(RESETTER);
    public static final MutableComponent SHUFFLE =          Component.literal("4").setStyle(ICONS).append(RESETTER);
    public static final MutableComponent SHUFFLE_ON =       Component.literal("5").setStyle(ICONS).append(RESETTER);
    public static final MutableComponent REPEAT =           Component.literal("6").setStyle(ICONS).append(RESETTER);
    public static final MutableComponent REPEAT_ON =        Component.literal("7").setStyle(ICONS).append(RESETTER);
    public static final MutableComponent REPEAT_SINGLE =    Component.literal("8").setStyle(ICONS).append(RESETTER);
    public static final MutableComponent EXPLICT =          Component.literal("9").setStyle(ICONS).append(RESETTER).append(" ");
    public static final MutableComponent ADD_TO_FAV =       Component.literal("a").setStyle(ICONS).append(RESETTER);
    public static final MutableComponent REMOVE_FROM_FAV =  Component.literal("b").setStyle(ICONS).append(RESETTER);
    public static final MutableComponent SEARCH =           Component.literal("c").setStyle(ICONS).append(RESETTER);
    public static final MutableComponent ADD =              Component.literal("d").setStyle(ICONS).append(RESETTER);
    public static final MutableComponent PLAY =             Component.literal("e").setStyle(ICONS).append(RESETTER);
    public static final MutableComponent ADD_TO_QUEUE =     Component.literal("f").setStyle(ICONS).append(RESETTER);
    public static final MutableComponent POPUP_OPEN =       Component.literal("g").setStyle(ICONS).append(RESETTER).append(" ");
}
