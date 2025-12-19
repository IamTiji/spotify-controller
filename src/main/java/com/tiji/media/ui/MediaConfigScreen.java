package com.tiji.media.ui;

import com.tiji.media.MediaClient;
import com.tiji.media.MediaConfig;
import com.tiji.media.api.ApiCalls;
import com.tiji.media.widgets.BorderedButtonWidget;
import com.tiji.media.widgets.LabelWidget;
import com.tiji.media.widgets.ValueHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class MediaConfigScreen extends BaseScreen {
    private enum ResetConfirmStatus {
        IDLE(Text.translatable("ui.media.reset_config")),
        CONFIRM(Text.translatable("ui.media.reset_config_confirm")),
        CONFIRMED(Text.translatable("ui.media.reset_config_success"));

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

    public MediaConfigScreen(Screen parent) {
        super(true);

        this.parent = parent;
        ApiCalls.getUserName(name -> {
            userName = name;
            if (userNameWidget != null) {
                userNameWidget.setText(Text.translatable("ui.media.status.setup", userName));
            }
        });
    }

    public void init() {
        super.init();

        int y = MARGIN;

        Text statusText;
        if (MediaClient.isNotSetup()) {
            statusText = Text.translatable("ui.media.status.not_setup");
        } else {
            statusText = Text.translatable("ui.media.status.setup", Text.translatable("ui.media.loading"));
        }
        userNameWidget = new LabelWidget(MARGIN + widgetsOffset, y, statusText);
        addDrawableChild(userNameWidget);
        y += textRenderer.fontHeight + MARGIN;

        resetButton = new BorderedButtonWidget(resetConfirmStatus.text, MARGIN + widgetsOffset, y, this::onResetButtonPress, false, WIDTH);
        addDrawableChild(resetButton);
        y += resetButton.getHeight() + MARGIN*3;

        for (Field field : MediaConfig.class.getDeclaredFields()) {
            MediaConfig.EditableField metadata = field.getAnnotation(MediaConfig.EditableField.class);
            if (metadata == null) continue;

            try {
                ValueHolder widget = metadata.widget()
                        .getConstructor(int.class, int.class, int.class, int.class)
                        .newInstance(MARGIN + widgetsOffset, y + textRenderer.fontHeight, WIDTH, FIELD_HEIGHT);

                field.setAccessible(true);
                widget.setValue(field.get(MediaClient.CONFIG));

                addDrawableChild((Element & Drawable & Selectable) widget);

                addDrawableChild(new LabelWidget(MARGIN + widgetsOffset, y, Text.translatable(metadata.translationKey())));

                map.put(field, widget);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }

            y += FIELD_HEIGHT + MARGIN + textRenderer.fontHeight;
        }

        userNameWidget.setText(Text.translatable("ui.media.status.setup", userName));
    }

    private void onResetButtonPress() {
        if (resetConfirmStatus == ResetConfirmStatus.IDLE) {
            resetConfirmStatus = ResetConfirmStatus.CONFIRM;
        } else if (resetConfirmStatus == ResetConfirmStatus.CONFIRM) {
            resetConfirmStatus = ResetConfirmStatus.CONFIRMED;
            MediaClient.CONFIG.resetConnection();
        }
        resetButton.setLabel(resetConfirmStatus.text);
    }

    public void close() {
        for (Field field : map.keySet()) {
            field.setAccessible(true);
            try {
                field.set(MediaClient.CONFIG, map.get(field).getValue());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        MediaClient.CONFIG.writeToFile();

        MinecraftClient.getInstance().setScreen(parent);
    }
}
