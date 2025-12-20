package com.tiji.spotify_controller.ui;

import com.tiji.spotify_controller.Main;
import com.tiji.spotify_controller.SpotifyControllerConfig;
import com.tiji.spotify_controller.WebGuideServer;
import com.tiji.spotify_controller.api.ApiCalls;
import com.tiji.spotify_controller.widgets.BorderedButtonWidget;
import com.tiji.spotify_controller.widgets.LabelWidget;
import com.tiji.spotify_controller.widgets.ValueHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class ConfigScreen extends BaseScreen {
    private enum ResetConfirmStatus {
        IDLE(Text.translatable("ui.spotify_controller.reset_config")),
        CONFIRM(Text.translatable("ui.spotify_controller.reset_config_confirm")),
        CONFIRMED(Text.translatable("ui.spotify_controller.reset_config_success"));

        public final Text text;
        ResetConfirmStatus(Text text) {
            this.text = text;
        }
    }

    private final Screen parent;
    private LabelWidget userNameWidget;
    private BorderedButtonWidget resetButton;
    private ResetConfirmStatus resetConfirmStatus = ResetConfirmStatus.IDLE;

    private String userName;

    private static final int WIDTH = 200;
    private static final int MARGIN = 10;
    private static final int FIELD_HEIGHT = 20;

    private final HashMap<Field, ValueHolder> map = new HashMap<>();

    public ConfigScreen(Screen parent) {
        super(true);

        this.parent = parent;
        if (!Main.isNotSetup()) {
            ApiCalls.getUserName(name -> {
                userName = name;
                if (userNameWidget != null) {
                    userNameWidget.setText(Text.translatable("ui.spotify_controller.status.setup", userName));
                }
            });
        }
    }

    public void init() {
        super.init();

        int y = MARGIN;

        Text statusText;
        if (Main.isNotSetup()) {
            statusText = Text.translatable("ui.spotify_controller.status.not_setup");
        } else if (userName == null) {
            statusText = Text.translatable("ui.spotify_controller.loading");
        } else {
            statusText = Text.translatable("ui.spotify_controller.status.setup", userName);
        }
        userNameWidget = new LabelWidget(MARGIN + widgetsOffset, y, statusText);
        addDrawableChild(userNameWidget);
        y += textRenderer.fontHeight + MARGIN;

        resetButton = new BorderedButtonWidget(resetConfirmStatus.text, MARGIN + widgetsOffset, y, this::onResetButtonPress, false, WIDTH);
        addDrawableChild(resetButton);
        y += resetButton.getHeight() + MARGIN*3;

        for (Field field : SpotifyControllerConfig.class.getDeclaredFields()) {
            SpotifyControllerConfig.EditableField metadata = field.getAnnotation(SpotifyControllerConfig.EditableField.class);
            if (metadata == null) continue;

            try {
                ValueHolder widget = metadata.widget()
                        .getConstructor(int.class, int.class, int.class, int.class)
                        .newInstance(MARGIN + widgetsOffset, y + textRenderer.fontHeight, WIDTH, FIELD_HEIGHT);

                field.setAccessible(true);
                widget.setValue(field.get(Main.CONFIG));

                addDrawableChild((Element & Drawable & Selectable) widget);

                addDrawableChild(new LabelWidget(MARGIN + widgetsOffset, y, Text.translatable(metadata.translationKey())));

                map.put(field, widget);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }

            y += FIELD_HEIGHT + MARGIN + textRenderer.fontHeight;
        }
    }

    private void onResetButtonPress() {
        if (Main.isNotSetup()) return;

        if (resetConfirmStatus == ResetConfirmStatus.IDLE) {
            resetConfirmStatus = ResetConfirmStatus.CONFIRM;
        } else if (resetConfirmStatus == ResetConfirmStatus.CONFIRM) {
            resetConfirmStatus = ResetConfirmStatus.CONFIRMED;
            Main.CONFIG.resetConnection();
            WebGuideServer.start();
        }
        resetButton.setLabel(resetConfirmStatus.text);
    }

    public void close() {
        for (Field field : map.keySet()) {
            field.setAccessible(true);
            try {
                field.set(Main.CONFIG, map.get(field).getValue());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        Main.CONFIG.writeToFile();

        MinecraftClient.getInstance().setScreen(parent);
    }
}
