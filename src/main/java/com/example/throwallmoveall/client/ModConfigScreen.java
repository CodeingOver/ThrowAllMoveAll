package com.example.throwallmoveall.client;

import com.example.throwallmoveall.config.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/**
 * Item-Scroller style Configuration GUI Screen.
 * Allows binding keyboard keys OR mouse clicks (LEFT_CLICK, RIGHT_CLICK, BUTTON_3) combined with Alt/Ctrl/Shift modifiers.
 */
public class ModConfigScreen extends Screen {
    private final Screen parent;
    private boolean listeningForThrow = false;
    private boolean listeningForMove = false;

    private ButtonWidget throwComboButton;
    private ButtonWidget moveComboButton;

    public ModConfigScreen(Screen parent) {
        super(Text.literal("ThrowAll & MoveAll Configuration"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = 60;
        ModConfig config = ModConfig.get();

        // --- Row 1: ThrowAll Combo Button ---
        this.throwComboButton = ButtonWidget.builder(
                Text.literal(config.getComboDisplayString(config.throwAllKey, config.throwAllAlt, config.throwAllCtrl, config.throwAllShift)),
                btn -> {
                    this.listeningForThrow = true;
                    this.listeningForMove = false;
                    btn.setMessage(Text.literal("> Press Key / Click Mouse <"));
                }
        ).dimensions(centerX - 100, startY, 200, 20).build();

        ButtonWidget throwResetButton = ButtonWidget.builder(
                Text.literal("RESET"),
                btn -> {
                    config.throwAllKey = GLFW.GLFW_KEY_V;
                    config.throwAllAlt = false;
                    config.throwAllCtrl = false;
                    config.throwAllShift = false;
                    updateButtonLabels();
                }
        ).dimensions(centerX + 110, startY, 50, 20).build();

        this.addDrawableChild(this.throwComboButton);
        this.addDrawableChild(throwResetButton);

        // --- Row 2: MoveAll Combo Button ---
        int moveY = startY + 45;
        this.moveComboButton = ButtonWidget.builder(
                Text.literal(config.getComboDisplayString(config.moveAllKey, config.moveAllAlt, config.moveAllCtrl, config.moveAllShift)),
                btn -> {
                    this.listeningForMove = true;
                    this.listeningForThrow = false;
                    btn.setMessage(Text.literal("> Press Key / Click Mouse <"));
                }
        ).dimensions(centerX - 100, moveY, 200, 20).build();

        ButtonWidget moveResetButton = ButtonWidget.builder(
                Text.literal("RESET"),
                btn -> {
                    config.moveAllKey = GLFW.GLFW_KEY_X;
                    config.moveAllAlt = false;
                    config.moveAllCtrl = false;
                    config.moveAllShift = false;
                    updateButtonLabels();
                }
        ).dimensions(centerX + 110, moveY, 50, 20).build();

        this.addDrawableChild(this.moveComboButton);
        this.addDrawableChild(moveResetButton);

        // --- Row 3: Save & Close Button ---
        int bottomY = moveY + 50;
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Save & Close"),
                btn -> {
                    ModConfig.save();
                    this.client.setScreen(this.parent);
                }
        ).dimensions(centerX - 75, bottomY, 150, 20).build());
    }

    private void updateButtonLabels() {
        ModConfig config = ModConfig.get();
        this.throwComboButton.setMessage(Text.literal(config.getComboDisplayString(config.throwAllKey, config.throwAllAlt, config.throwAllCtrl, config.throwAllShift)));
        this.moveComboButton.setMessage(Text.literal(config.getComboDisplayString(config.moveAllKey, config.moveAllAlt, config.moveAllCtrl, config.moveAllShift)));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.listeningForThrow || this.listeningForMove) {
            long window = this.client.getWindow().getHandle();
            boolean alt = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_ALT) || InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_ALT);
            boolean ctrl = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_CONTROL) || InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_CONTROL);
            boolean shift = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_SHIFT) || InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_SHIFT);

            int mouseCode;
            switch (button) {
                case 0: mouseCode = ModConfig.MOUSE_LEFT; break;
                case 1: mouseCode = ModConfig.MOUSE_RIGHT; break;
                case 2: mouseCode = ModConfig.MOUSE_MIDDLE; break;
                case 3: mouseCode = ModConfig.MOUSE_4; break;
                case 4: mouseCode = ModConfig.MOUSE_5; break;
                default: mouseCode = -100 + button; break;
            }

            ModConfig config = ModConfig.get();
            if (this.listeningForThrow) {
                config.throwAllKey = mouseCode;
                config.throwAllAlt = alt;
                config.throwAllCtrl = ctrl;
                config.throwAllShift = shift;
                this.listeningForThrow = false;
            } else {
                config.moveAllKey = mouseCode;
                config.moveAllAlt = alt;
                config.moveAllCtrl = ctrl;
                config.moveAllShift = shift;
                this.listeningForMove = false;
            }

            updateButtonLabels();
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.listeningForThrow || this.listeningForMove) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                this.listeningForThrow = false;
                this.listeningForMove = false;
                updateButtonLabels();
                return true;
            }

            long window = this.client.getWindow().getHandle();
            boolean alt = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_ALT) || InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_ALT);
            boolean ctrl = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_CONTROL) || InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_CONTROL);
            boolean shift = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_SHIFT) || InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_SHIFT);

            if (isModifier(keyCode)) {
                // Keep listening while modifier key is held
                StringBuilder preview = new StringBuilder("> ");
                if (ctrl || isCtrl(keyCode)) preview.append("LEFT_CONTROL + ");
                if (alt || isAlt(keyCode)) preview.append("LEFT_ALT + ");
                if (shift || isShift(keyCode)) preview.append("LEFT_SHIFT + ");
                preview.append("... <");

                if (this.listeningForThrow) this.throwComboButton.setMessage(Text.literal(preview.toString()));
                else this.moveComboButton.setMessage(Text.literal(preview.toString()));

                return true;
            }

            ModConfig config = ModConfig.get();
            if (this.listeningForThrow) {
                config.throwAllKey = keyCode;
                config.throwAllAlt = alt;
                config.throwAllCtrl = ctrl;
                config.throwAllShift = shift;
                this.listeningForThrow = false;
            } else {
                config.moveAllKey = keyCode;
                config.moveAllAlt = alt;
                config.moveAllCtrl = ctrl;
                config.moveAllShift = shift;
                this.listeningForMove = false;
            }

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
        context.drawTextWithShadow(this.textRenderer, Text.literal("dropAllMatching (ThrowAll):"), centerX - 230, 65, 0xE0E0E0);
        context.drawTextWithShadow(this.textRenderer, Text.literal("moveAll (MoveAll):"), centerX - 230, 110, 0xE0E0E0);

        if (this.listeningForThrow || this.listeningForMove) {
            context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Press any Key OR Click Mouse Button (LEFT_CLICK, RIGHT_CLICK...) to bind combo."), centerX, this.height - 30, 0xFFD700);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        ModConfig.save();
        this.client.setScreen(this.parent);
    }
}
