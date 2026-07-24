package com.example.throwallmoveall.client;

import com.example.throwallmoveall.config.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/**
 * Clean English Configuration GUI Screen for ThrowAll & MoveAll Mod.
 * Supports intuitive combo key binding (automatically captures Alt, Ctrl, Shift modifiers while awaiting main key).
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
        super(Text.literal("ThrowAll & MoveAll Configuration"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = 50;
        ModConfig config = ModConfig.get();

        // --- Row 1: ThrowAll Options ---
        this.throwKeyButton = ButtonWidget.builder(
                Text.literal("Key: " + config.getKeyName(config.throwAllKey)),
                btn -> {
                    this.listeningForThrowKey = true;
                    this.listeningForMoveKey = false;
                    btn.setMessage(Text.literal("> Press Key... <"));
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

        // --- Row 2: MoveAll Options ---
        int moveY = startY + 45;
        this.moveKeyButton = ButtonWidget.builder(
                Text.literal("Key: " + config.getKeyName(config.moveAllKey)),
                btn -> {
                    this.listeningForMoveKey = true;
                    this.listeningForThrowKey = false;
                    btn.setMessage(Text.literal("> Press Key... <"));
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

        // --- Bottom Controls ---
        int bottomY = moveY + 45;
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Reset Default"),
                btn -> {
                    config.throwAllKey = GLFW.GLFW_KEY_V;
                    config.throwAllAlt = false;
                    config.throwAllCtrl = false;
                    config.throwAllShift = false;

                    config.moveAllKey = GLFW.GLFW_KEY_X;
                    config.moveAllAlt = false;
                    config.moveAllCtrl = false;
                    config.moveAllShift = false;

                    updateButtonLabels();
                }
        ).dimensions(centerX - 150, bottomY, 100, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Save & Close"),
                btn -> {
                    ModConfig.save();
                    this.client.setScreen(this.parent);
                }
        ).dimensions(centerX - 40, bottomY, 190, 20).build());
    }

    private void updateButtonLabels() {
        ModConfig config = ModConfig.get();
        this.throwKeyButton.setMessage(Text.literal("Key: " + config.getKeyName(config.throwAllKey)));
        this.throwAltButton.setMessage(Text.literal("Alt: " + (config.throwAllAlt ? "ON" : "OFF")));
        this.throwCtrlButton.setMessage(Text.literal("Ctrl: " + (config.throwAllCtrl ? "ON" : "OFF")));
        this.throwShiftButton.setMessage(Text.literal("Shift: " + (config.throwAllShift ? "ON" : "OFF")));

        this.moveKeyButton.setMessage(Text.literal("Key: " + config.getKeyName(config.moveAllKey)));
        this.moveAltButton.setMessage(Text.literal("Alt: " + (config.moveAllAlt ? "ON" : "OFF")));
        this.moveCtrlButton.setMessage(Text.literal("Ctrl: " + (config.moveAllCtrl ? "ON" : "OFF")));
        this.moveShiftButton.setMessage(Text.literal("Shift: " + (config.moveAllShift ? "ON" : "OFF")));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        ModConfig config = ModConfig.get();

        if (this.listeningForThrowKey) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                this.listeningForThrowKey = false;
                this.throwKeyButton.setMessage(Text.literal("Key: " + config.getKeyName(config.throwAllKey)));
                return true;
            }
            if (isModifier(keyCode)) {
                if (isAlt(keyCode)) config.throwAllAlt = !config.throwAllAlt;
                if (isCtrl(keyCode)) config.throwAllCtrl = !config.throwAllCtrl;
                if (isShift(keyCode)) config.throwAllShift = !config.throwAllShift;
                updateButtonLabels();
                this.throwKeyButton.setMessage(Text.literal("> Press Key... <"));
                return true;
            }
            config.throwAllKey = keyCode;
            this.listeningForThrowKey = false;
            updateButtonLabels();
            return true;
        }

        if (this.listeningForMoveKey) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                this.listeningForMoveKey = false;
                this.moveKeyButton.setMessage(Text.literal("Key: " + config.getKeyName(config.moveAllKey)));
                return true;
            }
            if (isModifier(keyCode)) {
                if (isAlt(keyCode)) config.moveAllAlt = !config.moveAllAlt;
                if (isCtrl(keyCode)) config.moveAllCtrl = !config.moveAllCtrl;
                if (isShift(keyCode)) config.moveAllShift = !config.moveAllShift;
                updateButtonLabels();
                this.moveKeyButton.setMessage(Text.literal("> Press Key... <"));
                return true;
            }
            config.moveAllKey = keyCode;
            this.listeningForMoveKey = false;
            updateButtonLabels();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private boolean isModifier(int keyCode) {
        return isAlt(keyCode) || isCtrl(keyCode) || isShift(keyCode);
    }

    private boolean isAlt(int keyCode) {
        return keyCode == GLFW.GLFW_KEY_LEFT_ALT || keyCode == GLFW.GLFW_KEY_RIGHT_ALT;
    }

    private boolean isCtrl(int keyCode) {
        return keyCode == GLFW.GLFW_KEY_LEFT_CONTROL || keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL;
    }

    private boolean isShift(int keyCode) {
        return keyCode == GLFW.GLFW_KEY_LEFT_SHIFT || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        int centerX = this.width / 2;

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, centerX, 15, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, Text.literal("ThrowAll Shortcut:"), centerX - 150, 35, 0xAAAAAA);
        context.drawTextWithShadow(this.textRenderer, Text.literal("MoveAll Shortcut:"), centerX - 150, 80, 0xAAAAAA);

        if (this.listeningForThrowKey || this.listeningForMoveKey) {
            context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Press any main key (e.g. Q, V, X). Press Alt/Ctrl/Shift to toggle modifiers."), centerX, this.height - 30, 0xFFD700);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        ModConfig.save();
        this.client.setScreen(this.parent);
    }
}
