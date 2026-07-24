package com.example.throwallmoveall.client;

import com.example.throwallmoveall.config.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/**
 * Giao diện cấu hình In-Game cho Mod (ModConfigScreen) cho phép bấm tùy chỉnh các nút Combo.
 */
public class ModConfigScreen extends Screen {
    private final Screen parent;
    private boolean listeningForThrowKey = false;
    private boolean listeningForMoveKey = false;

    private ButtonWidget throwKeyButton;
    private ButtonWidget throwAltButton;
    private ButtonWidget throwCtrlButton;
    private ButtonWidget throwShiftButton;

    private ButtonWidget moveKeyButton;
    private ButtonWidget moveAltButton;
    private ButtonWidget moveCtrlButton;
    private ButtonWidget moveShiftButton;

    public ModConfigScreen(Screen parent) {
        super(Text.literal("ThrowAll & MoveAll Config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = 50;
        ModConfig config = ModConfig.get();

        // --- Hàng 1: ThrowAll Options ---
        this.throwKeyButton = ButtonWidget.builder(
                Text.literal("Key: " + config.getKeyName(config.throwAllKey)),
                btn -> {
                    this.listeningForThrowKey = true;
                    this.listeningForMoveKey = false;
                    btn.setMessage(Text.literal("> Press Key <"));
                }
        ).dimensions(centerX - 150, startY, 100, 20).build();

        this.throwAltButton = ButtonWidget.builder(
                Text.literal("Alt: " + (config.throwAllAlt ? "ON" : "OFF")),
                btn -> {
                    config.throwAllAlt = !config.throwAllAlt;
                    btn.setMessage(Text.literal("Alt: " + (config.throwAllAlt ? "ON" : "OFF")));
                }
        ).dimensions(centerX - 45, startY, 60, 20).build();

        this.throwCtrlButton = ButtonWidget.builder(
                Text.literal("Ctrl: " + (config.throwAllCtrl ? "ON" : "OFF")),
                btn -> {
                    config.throwAllCtrl = !config.throwAllCtrl;
                    btn.setMessage(Text.literal("Ctrl: " + (config.throwAllCtrl ? "ON" : "OFF")));
                }
        ).dimensions(centerX + 20, startY, 60, 20).build();

        this.throwShiftButton = ButtonWidget.builder(
                Text.literal("Shift: " + (config.throwAllShift ? "ON" : "OFF")),
                btn -> {
                    config.throwAllShift = !config.throwAllShift;
                    btn.setMessage(Text.literal("Shift: " + (config.throwAllShift ? "ON" : "OFF")));
                }
        ).dimensions(centerX + 85, startY, 65, 20).build();

        this.addDrawableChild(this.throwKeyButton);
        this.addDrawableChild(this.throwAltButton);
        this.addDrawableChild(this.throwCtrlButton);
        this.addDrawableChild(this.throwShiftButton);

        // --- Hàng 2: MoveAll Options ---
        int moveY = startY + 45;
        this.moveKeyButton = ButtonWidget.builder(
                Text.literal("Key: " + config.getKeyName(config.moveAllKey)),
                btn -> {
                    this.listeningForMoveKey = true;
                    this.listeningForThrowKey = false;
                    btn.setMessage(Text.literal("> Press Key <"));
                }
        ).dimensions(centerX - 150, moveY, 100, 20).build();

        this.moveAltButton = ButtonWidget.builder(
                Text.literal("Alt: " + (config.moveAllAlt ? "ON" : "OFF")),
                btn -> {
                    config.moveAllAlt = !config.moveAllAlt;
                    btn.setMessage(Text.literal("Alt: " + (config.moveAllAlt ? "ON" : "OFF")));
                }
        ).dimensions(centerX - 45, moveY, 60, 20).build();

        this.moveCtrlButton = ButtonWidget.builder(
                Text.literal("Ctrl: " + (config.moveAllCtrl ? "ON" : "OFF")),
                btn -> {
                    config.moveAllCtrl = !config.moveAllCtrl;
                    btn.setMessage(Text.literal("Ctrl: " + (config.moveAllCtrl ? "ON" : "OFF")));
                }
        ).dimensions(centerX + 20, moveY, 60, 20).build();

        this.moveShiftButton = ButtonWidget.builder(
                Text.literal("Shift: " + (config.moveAllShift ? "ON" : "OFF")),
                btn -> {
                    config.moveAllShift = !config.moveAllShift;
                    btn.setMessage(Text.literal("Shift: " + (config.moveAllShift ? "ON" : "OFF")));
                }
        ).dimensions(centerX + 85, moveY, 65, 20).build();

        this.addDrawableChild(this.moveKeyButton);
        this.addDrawableChild(this.moveAltButton);
        this.addDrawableChild(this.moveCtrlButton);
        this.addDrawableChild(this.moveShiftButton);

        // --- Nút Save & Close ---
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Save & Close"),
                btn -> {
                    ModConfig.save();
                    this.client.setScreen(this.parent);
                }
        ).dimensions(centerX - 75, moveY + 45, 150, 20).build());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        ModConfig config = ModConfig.get();

        if (this.listeningForThrowKey) {
            if (keyCode != GLFW.GLFW_KEY_ESCAPE) {
                config.throwAllKey = keyCode;
            }
            this.listeningForThrowKey = false;
            this.throwKeyButton.setMessage(Text.literal("Key: " + config.getKeyName(config.throwAllKey)));
            return true;
        }

        if (this.listeningForMoveKey) {
            if (keyCode != GLFW.GLFW_KEY_ESCAPE) {
                config.moveAllKey = keyCode;
            }
            this.listeningForMoveKey = false;
            this.moveKeyButton.setMessage(Text.literal("Key: " + config.getKeyName(config.moveAllKey)));
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        int centerX = this.width / 2;

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, centerX, 15, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, Text.literal("ThrowAll Shortcut:"), centerX - 150, 35, 0xAAAAAA);
        context.drawTextWithShadow(this.textRenderer, Text.literal("MoveAll Shortcut:"), centerX - 150, 80, 0xAAAAAA);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        ModConfig.save();
        this.client.setScreen(this.parent);
    }
}
