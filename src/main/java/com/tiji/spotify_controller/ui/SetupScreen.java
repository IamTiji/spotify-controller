package com.tiji.spotify_controller.ui;

import com.tiji.spotify_controller.util.TextUtils;
import com.tiji.spotify_controller.widgets.BorderlessButtonWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class SetupScreen extends BaseScreen {
    private static final int MARGIN = 10;
    private static final Style LINK = Style.EMPTY.withFont(Style.DEFAULT_FONT_ID).withUnderline(true);

    public SetupScreen() {
        super(true);
    }

    @Override
    protected void init() {
        super.init();

        addDrawableChild(
                new BorderlessButtonWidget(Icons.POPUP_OPEN.copy().append(Text.literal("http://127.0.0.1:25566").setStyle(LINK)),
                        MARGIN + widgetsOffset, MARGIN*3 + textRenderer.fontHeight*3,
                        () -> Util.getOperatingSystem().open("http://127.0.0.1:25566"),
                        false)
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.drawText(textRenderer, Text.translatable("ui.spotify_controller.welcome"), MARGIN + widgetsOffset, MARGIN, 0xFFFFFFFF, false);

        String rawText = I18n.translate("ui.spotify_controller.welcome.subtext");
        String[] warpedText = TextUtils.warpText(rawText, 200);
        int y = MARGIN*2 + textRenderer.fontHeight;

        for (String line : warpedText) {
            context.drawText(textRenderer, line, MARGIN + widgetsOffset, y, 0xFFFFFFFF, false);
            y += textRenderer.fontHeight;
        }
    }
}