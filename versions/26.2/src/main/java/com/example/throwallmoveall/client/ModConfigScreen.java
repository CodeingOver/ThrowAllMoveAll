package com.example.throwallmoveall.client;

import com.example.throwallmoveall.config.ModConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

/**
 * Item-Scroller style Configuration GUI Screen for Minecraft 26.x.
 */
public class ModConfigScreen extends Screen {

    // ── Default values (must match reset-button logic) ───────────────────────
    private static final int     DEF_THROW_KEY   = GLFW.GLFW_KEY_Q;
    private static final boolean DEF_THROW_ALT   = true;
    private static final boolean DEF_THROW_CTRL  = false;
    private static final boolean DEF_THROW_SHIFT = false;

    private static final int     DEF_MOVE_KEY    = ModConfig.MOUSE_LEFT;
    private static final boolean DEF_MOVE_ALT    = true;
    private static final boolean DEF_MOVE_CTRL   = false;
    private static final boolean DEF_MOVE_SHIFT  = false;

    // ── Layout constants ─────────────────────────────────────────────────────
    private static final int BIND_W     = 160;
    private static final int ICON_W     = 20;
    private static final int RESET_W    = 50;
    private static final int GAP        = 2;
    private static final int ROW_H      = 20;
    private static final int ROW_STRIDE = 28;

    // ── Colours ──────────────────────────────────────────────────────────────
    private static final int COLOR_LABEL  = 0xFFE0E0E0;
    private static final int COLOR_HINT   = 0xFFFFD700;
    private static final int COLOR_TITLE  = 0xFFFFFFFF;
    private static final int COLOR_HEADER = 0xFFAAAAAA;

    // ── State ────────────────────────────────────────────────────────────────
    private final Screen parent;
    /** 0 = not listening, 1 = listening for ThrowAll, 2 = listening for MoveAll */
    private int listeningRow = 0;

    private Button throwComboButton;
    private Button moveComboButton;
    private Button throwResetBtn;
    private Button moveResetBtn;

    private boolean throwResetActive = false;
    private boolean moveResetActive  = false;

    public ModConfigScreen(Screen parent) {
        super(Component.literal("ThrowAll & MoveAll Configuration"));
        this.parent = parent;
    }

    // ── Build widgets ────────────────────────────────────────────────────────

    @Override
    protected void init() {
        int cx        = this.width  / 2;
        int blockLeft = cx - 10;
        int startY    = this.height / 2 - ROW_STRIDE;

        ModConfig config = ModConfig.get();

        // ── Row 1 : ThrowAll ─────────────────────────────────────────────────
        int y1 = startY;

        this.throwComboButton = Button.builder(
                Component.literal(config.getComboDisplayString(
                        config.throwAllKey, config.throwAllAlt, config.throwAllCtrl, config.throwAllShift)),
                btn -> {
                    listeningRow = 1;
                    btn.setMessage(Component.literal("> Press key / click <"));
                }
        ).bounds(blockLeft, y1, BIND_W, ROW_H).build();

        Button throwIconBtn = Button.builder(
                Component.literal("\u2194"),
                btn -> {
                    listeningRow = 1;
                    throwComboButton.setMessage(Component.literal("> Press key / click <"));
                }
        ).bounds(blockLeft + BIND_W + GAP, y1, ICON_W, ROW_H).build();

        this.throwResetBtn = Button.builder(
                Component.literal("RESET"),
                btn -> {
                    config.throwAllKey   = DEF_THROW_KEY;
                    config.throwAllAlt   = DEF_THROW_ALT;
                    config.throwAllCtrl  = DEF_THROW_CTRL;
                    config.throwAllShift = DEF_THROW_SHIFT;
                    listeningRow = 0;
                    refreshLabels();
                }
        ).bounds(blockLeft + BIND_W + GAP + ICON_W + GAP, y1, RESET_W, ROW_H).build();

        this.addRenderableWidget(this.throwComboButton);
        this.addRenderableWidget(throwIconBtn);
        this.addRenderableWidget(this.throwResetBtn);

        // ── Row 2 : MoveAll ───────────────────────────────────────────────────
        int y2 = startY + ROW_STRIDE;

        this.moveComboButton = Button.builder(
                Component.literal(config.getComboDisplayString(
                        config.moveAllKey, config.moveAllAlt, config.moveAllCtrl, config.moveAllShift)),
                btn -> {
                    listeningRow = 2;
                    btn.setMessage(Component.literal("> Press key / click <"));
                }
        ).bounds(blockLeft, y2, BIND_W, ROW_H).build();

        Button moveIconBtn = Button.builder(
                Component.literal("\u2194"),
                btn -> {
                    listeningRow = 2;
                    moveComboButton.setMessage(Component.literal("> Press key / click <"));
                }
        ).bounds(blockLeft + BIND_W + GAP, y2, ICON_W, ROW_H).build();

        this.moveResetBtn = Button.builder(
                Component.literal("RESET"),
                btn -> {
                    config.moveAllKey   = DEF_MOVE_KEY;
                    config.moveAllAlt   = DEF_MOVE_ALT;
                    config.moveAllCtrl  = DEF_MOVE_CTRL;
                    config.moveAllShift = DEF_MOVE_SHIFT;
                    listeningRow = 0;
                    refreshLabels();
                }
        ).bounds(blockLeft + BIND_W + GAP + ICON_W + GAP, y2, RESET_W, ROW_H).build();

        this.addRenderableWidget(this.moveComboButton);
        this.addRenderableWidget(moveIconBtn);
        this.addRenderableWidget(this.moveResetBtn);

        // ── Save & Close ──────────────────────────────────────────────────────
        int y3 = y2 + ROW_STRIDE + 10;
        this.addRenderableWidget(Button.builder(
                Component.literal("Save & Close"),
                btn -> {
                    ModConfig.save();
                    this.minecraft.gui.setScreen(this.parent);
                }
        ).bounds(cx - 55, y3, 110, ROW_H).build());

        refreshLabels();
    }

    // ── Mouse capture ────────────────────────────────────────────────────────

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean bl) {
        if (listeningRow != 0) {
            int button = click.button();
            int mouseCode = switch (button) {
                case 0 -> ModConfig.MOUSE_LEFT;
                case 1 -> ModConfig.MOUSE_RIGHT;
                case 2 -> ModConfig.MOUSE_MIDDLE;
                case 3 -> ModConfig.MOUSE_4;
                case 4 -> ModConfig.MOUSE_5;
                default -> -100 - button;
            };

            boolean alt   = click.hasAltDown();
            boolean ctrl  = click.hasControlDown();
            boolean shift = click.hasShiftDown();

            applyBind(mouseCode, alt, ctrl, shift);
            return true;
        }
        return super.mouseClicked(click, bl);
    }

    // ── Keyboard capture ─────────────────────────────────────────────────────

    @Override
    public boolean keyPressed(KeyEvent input) {
        if (listeningRow != 0) {
            int keyCode = input.key();
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                listeningRow = 0;
                refreshLabels();
                return true;
            }

            if (isModifier(keyCode)) {
                showModifierPreview(keyCode, input.modifiers());
                return true;
            }

            boolean alt   = input.hasAltDown();
            boolean ctrl  = input.hasControlDown();
            boolean shift = input.hasShiftDown();

            applyBind(keyCode, alt, ctrl, shift);
            return true;
        }
        return super.keyPressed(input);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void applyBind(int code, boolean alt, boolean ctrl, boolean shift) {
        ModConfig config = ModConfig.get();
        if (listeningRow == 1) {
            config.throwAllKey   = code;
            config.throwAllAlt   = alt;
            config.throwAllCtrl  = ctrl;
            config.throwAllShift = shift;
        } else {
            config.moveAllKey   = code;
            config.moveAllAlt   = alt;
            config.moveAllCtrl  = ctrl;
            config.moveAllShift = shift;
        }
        listeningRow = 0;
        refreshLabels();
    }

    private void showModifierPreview(int pressedKey, int mods) {
        StringBuilder sb = new StringBuilder("> ");
        if ((mods & GLFW.GLFW_MOD_CONTROL) != 0 || isCtrl(pressedKey))  sb.append("CTRL + ");
        if ((mods & GLFW.GLFW_MOD_ALT)     != 0 || isAlt(pressedKey))   sb.append("ALT + ");
        if ((mods & GLFW.GLFW_MOD_SHIFT)   != 0 || isShift(pressedKey)) sb.append("SHIFT + ");
        sb.append("...");

        String preview = sb.toString();
        if (listeningRow == 1) throwComboButton.setMessage(Component.literal(preview));
        else                   moveComboButton.setMessage(Component.literal(preview));
    }

    private void refreshLabels() {
        ModConfig config = ModConfig.get();
        throwComboButton.setMessage(Component.literal(
                config.getComboDisplayString(config.throwAllKey, config.throwAllAlt, config.throwAllCtrl, config.throwAllShift)));
        moveComboButton.setMessage(Component.literal(
                config.getComboDisplayString(config.moveAllKey, config.moveAllAlt, config.moveAllCtrl, config.moveAllShift)));
        throwResetActive = !isThrowDefault(config);
        moveResetActive  = !isMoveDefault(config);
        if (throwResetBtn != null) throwResetBtn.active = throwResetActive;
        if (moveResetBtn  != null) moveResetBtn.active  = moveResetActive;
    }

    private boolean isThrowDefault(ModConfig config) {
        return config.throwAllKey   == DEF_THROW_KEY
            && config.throwAllAlt   == DEF_THROW_ALT
            && config.throwAllCtrl  == DEF_THROW_CTRL
            && config.throwAllShift == DEF_THROW_SHIFT;
    }

    private boolean isMoveDefault(ModConfig config) {
        return config.moveAllKey   == DEF_MOVE_KEY
            && config.moveAllAlt   == DEF_MOVE_ALT
            && config.moveAllCtrl  == DEF_MOVE_CTRL
            && config.moveAllShift == DEF_MOVE_SHIFT;
    }

    private boolean isModifier(int k) { return isAlt(k) || isCtrl(k) || isShift(k); }
    private boolean isAlt(int k)   { return k == GLFW.GLFW_KEY_LEFT_ALT   || k == GLFW.GLFW_KEY_RIGHT_ALT; }
    private boolean isCtrl(int k)  { return k == GLFW.GLFW_KEY_LEFT_CONTROL || k == GLFW.GLFW_KEY_RIGHT_CONTROL; }
    private boolean isShift(int k) { return k == GLFW.GLFW_KEY_LEFT_SHIFT  || k == GLFW.GLFW_KEY_RIGHT_SHIFT; }

    // ── Render ────────────────────────────────────────────────────────────────

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        try {
            this.extractBackground(extractor, mouseX, mouseY, delta);
        } catch (Exception ignored) {
        }

        int cx        = this.width  / 2;
        int blockLeft = cx - 10;
        int startY    = this.height / 2 - ROW_STRIDE;

        // ── Title ─────────────────────────────────────────────────────────
        extractor.centeredText(this.font, this.title, cx, 18, COLOR_TITLE);

        // ── Column header "Hotkey" ─────────────────────────────────────────
        extractor.centeredText(
                this.font,
                Component.literal("Hotkey"),
                blockLeft + BIND_W / 2,
                startY - 14,
                COLOR_HEADER
        );

        // ── Row labels ────────────────────────────────────────────────────
        int labelX = cx - 190;
        drawRowLabel(extractor, "dropAllMatching (ThrowAll):", labelX, startY);
        drawRowLabel(extractor, "moveAll (MoveAll):",           labelX, startY + ROW_STRIDE);

        // ── Listening hint at bottom ──────────────────────────────────────
        if (listeningRow != 0) {
            extractor.centeredText(
                    this.font,
                    Component.literal("Press any key or click a mouse button to bind  |  ESC to cancel"),
                    cx,
                    this.height - 24,
                    COLOR_HINT
            );
        }

        super.extractRenderState(extractor, mouseX, mouseY, delta);
    }

    private void drawRowLabel(GuiGraphicsExtractor extractor, String text, int x, int buttonY) {
        int textY = buttonY + (ROW_H - this.font.lineHeight) / 2 + 1;
        extractor.text(this.font, Component.literal(text), x, textY, COLOR_LABEL, true);
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @Override
    public void onClose() {
        ModConfig.save();
        this.minecraft.gui.setScreen(this.parent);
    }
}
