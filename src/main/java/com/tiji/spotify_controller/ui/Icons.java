package com.tiji.spotify_controller.ui;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import com.tiji.spotify_controller.Main;

public class Icons {
    public static final Identifier ICON_ID = Identifier.of(Main.MOD_ID, "icon");
    private static final Style ICONS = Style.EMPTY.withFont(ICON_ID);
    private static final Text RESETTER = Text.literal("").setStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT_ID));


    public static final MutableText NEXT =             Text.literal("1").setStyle(ICONS).append(RESETTER);
    public static final MutableText PREVIOUS =         Text.literal("0").setStyle(ICONS).append(RESETTER);
    public static final MutableText PAUSE =            Text.literal("2").setStyle(ICONS).append(RESETTER);
    public static final MutableText RESUME =           Text.literal("3").setStyle(ICONS).append(RESETTER);
    public static final MutableText SHUFFLE =          Text.literal("4").setStyle(ICONS).append(RESETTER);
    public static final MutableText SHUFFLE_ON =       Text.literal("5").setStyle(ICONS).append(RESETTER);
    public static final MutableText REPEAT =           Text.literal("6").setStyle(ICONS).append(RESETTER);
    public static final MutableText REPEAT_ON =        Text.literal("7").setStyle(ICONS).append(RESETTER);
    public static final MutableText REPEAT_SINGLE =    Text.literal("8").setStyle(ICONS).append(RESETTER);
    public static final MutableText EXPLICT =          Text.literal("9").setStyle(ICONS).append(RESETTER).append(" ");
    public static final MutableText ADD_TO_FAV =       Text.literal("a").setStyle(ICONS).append(RESETTER);
    public static final MutableText REMOVE_FROM_FAV =  Text.literal("b").setStyle(ICONS).append(RESETTER);
    public static final MutableText SEARCH =           Text.literal("c").setStyle(ICONS).append(RESETTER);
    public static final MutableText ADD =              Text.literal("d").setStyle(ICONS).append(RESETTER);
    public static final MutableText PLAY =             Text.literal("e").setStyle(ICONS).append(RESETTER);
    public static final MutableText ADD_TO_QUEUE =     Text.literal("f").setStyle(ICONS).append(RESETTER);
    public static final MutableText POPUP_OPEN =       Text.literal("g").setStyle(ICONS).append(RESETTER).append(" ");
}
