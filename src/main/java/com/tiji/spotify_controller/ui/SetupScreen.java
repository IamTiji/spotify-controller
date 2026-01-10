package com.tiji.spotify_controller.ui;

import com.tiji.spotify_controller.util.TextUtils;
import com.tiji.spotify_controller.widgets.BorderlessButtonWidget;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

public class SetupScreen extends BaseScreen {
    private static final int MARGIN = 10;
    private static final Style LINK = Style.EMPTY.withFont(Style.DEFAULT_FONT).withUnderlined(true);

    public SetupScreen() {
        super(true);
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(
                new BorderlessButtonWidget(Icons.POPUP_OPEN.copy().append(Component.literal("http://127.0.0.1:25566").setStyle(LINK)),
                        MARGIN + widgetsOffset, MARGIN*3 + font.lineHeight*3,
                        () -> Util.getPlatform().openUri("http://127.0.0.1:25566"),
                        false)
        );
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.drawString(font, Component.translatable("ui.spotify_controller.welcome"), MARGIN + widgetsOffset, MARGIN, 0xFFFFFFFF, false);

        String rawText = I18n.get("ui.spotify_controller.welcome.subtext");
        String[] warpedText = TextUtils.warpText(rawText, 200);
        int y = MARGIN*2 + font.lineHeight;

        for (String line : warpedText) {
            context.drawString(font, line, MARGIN + widgetsOffset, y, 0xFFFFFFFF, false);
            y += font.lineHeight;
        }
    }
}