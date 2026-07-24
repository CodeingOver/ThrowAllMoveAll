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
 *
 * Layout mimics Item Scroller's hotkey panel:
 *   [label text]   [← key combo button (wide) →]  [↔]  [RESET]
 *
 * Listening mode:  click the key combo button → it turns gold and waits
 *                  for the next key press or mouse click to bind.
 */
public class ModConfigScreen extends Screen {

    // ── Layout constants ────────────────────────────────────────────────────
    /** X position of the left edge of the keybind + reset button group. */
    private static final int COL_BIND_X   = 200; // offset from screen center-left area
    private static final int BIND_W       = 160;
    private static final int ICON_W       = 20;
    private static final int RESET_W      = 50;
    private static final int GAP          = 2;
    private static final int ROW_H        = 20;
    private static final int ROW_STRIDE   = 28;  // vertical distance between rows

    // ── Colours ─────────────────────────────────────────────────────────────
    private static final int COLOR_LABEL   = 0xFFE0E0E0;
    private static final int COLOR_HINT    = 0xFFFFD700; // gold
    private static final int COLOR_TITLE   = 0xFFFFFFFF;
    private static final int COLOR_HEADER  = 0xFFAAAAAA;

    // ── State ────────────────────────────────────────────────────────────────
    private final Screen parent;
    /** Which row is currently listening: 0=none, 1=throw, 2=move */
    private int listeningRow = 0;

    private ButtonWidget throwComboButton;
    private ButtonWidget moveComboButton;

    public ModConfigScreen(Screen parent) {
        super(Text.literal("ThrowAll & MoveAll Configuration"));
        this.parent = parent;
    }

    // ── Build widgets ────────────────────────────────────────────────────────

    @Override
    protected void init() {
        int cx = this.width / 2;
        // We anchor the button group at cx - some offset so the label stays on the left
        // The whole block (label + buttons) is centred horizontally.
        // Total width of button group: BIND_W + GAP + ICON_W + GAP + RESET_W = 234
        // Place button group starting at cx - (BIND_W/2 + ICON_W + GAP + RESET_W)/2 … simplest:
        int blockLeft = cx - 10; // start of keybind button
        int startY = this.height / 2 - ROW_STRIDE;

        ModConfig config = ModConfig.get();

        // ── Row 1 : ThrowAll ─────────────────────────────────────────────
        int y1 = startY;

        this.throwComboButton = ButtonWidget.builder(
                Text.literal(config.getComboDisplayString(
                        config.throwAllKey, config.throwAllAlt, config.throwAllCtrl, config.throwAllShift)),
                btn -> {
                    listeningRow = 1;
                    btn.setMessage(Text.literal("> Press key / click <"));
                }
        ).dimensions(blockLeft, y1, BIND_W, ROW_H).build();

        // icon button  "↔"  (non-interactive, visual only)
        ButtonWidget throwIconBtn = ButtonWidget.builder(
                Text.literal("\u2194"),
                btn -> {
                    listeningRow = 1;
                    this.throwComboButton.setMessage(Text.literal("> Press key / click <"));
                }
        ).dimensions(blockLeft + BIND_W + GAP, y1, ICON_W, ROW_H).build();

        ButtonWidget throwResetBtn = ButtonWidget.builder(
                Text.literal("RESET"),
                btn -> {
                    config.throwAllKey   = GLFW.GLFW_KEY_Q;
                    config.throwAllAlt   = true;
                    config.throwAllCtrl  = false;
                    config.throwAllShift = false;
                    listeningRow = 0;
                    refreshLabels();
                }
        ).dimensions(blockLeft + BIND_W + GAP + ICON_W + GAP, y1, RESET_W, ROW_H).build();

        this.addDrawableChild(this.throwComboButton);
        this.addDrawableChild(throwIconBtn);
        this.addDrawableChild(throwResetBtn);

        // ── Row 2 : MoveAll ───────────────────────────────────────────────
        int y2 = startY + ROW_STRIDE;

        this.moveComboButton = ButtonWidget.builder(
                Text.literal(config.getComboDisplayString(
                        config.moveAllKey, config.moveAllAlt, config.moveAllCtrl, config.moveAllShift)),
                btn -> {
                    listeningRow = 2;
                    btn.setMessage(Text.literal("> Press key / click <"));
                }
        ).dimensions(blockLeft, y2, BIND_W, ROW_H).build();

        ButtonWidget moveIconBtn = ButtonWidget.builder(
                Text.literal("\u2194"),
                btn -> {
                    listeningRow = 2;
                    this.moveComboButton.setMessage(Text.literal("> Press key / click <"));
                }
        ).dimensions(blockLeft + BIND_W + GAP, y2, ICON_W, ROW_H).build();

        ButtonWidget moveResetBtn = ButtonWidget.builder(
                Text.literal("RESET"),
                btn -> {
                    config.moveAllKey   = ModConfig.MOUSE_LEFT;
                    config.moveAllAlt   = true;
                    config.moveAllCtrl  = false;
                    config.moveAllShift = false;
                    listeningRow = 0;
                    refreshLabels();
                }
        ).dimensions(blockLeft + BIND_W + GAP + ICON_W + GAP, y2, RESET_W, ROW_H).build();

        this.addDrawableChild(this.moveComboButton);
        this.addDrawableChild(moveIconBtn);
        this.addDrawableChild(moveResetBtn);

        // ── Save & Close ──────────────────────────────────────────────────
        int y3 = y2 + ROW_STRIDE + 10;
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Save & Close"),
                btn -> {
                    ModConfig.save();
                    this.client.setScreen(this.parent);
                }
        ).dimensions(cx - 55, y3, 110, ROW_H).build());
    }

    // ── Mouse capture ────────────────────────────────────────────────────────

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (listeningRow != 0) {
            long window = this.client.getWindow().getHandle();
            boolean alt   = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_ALT)
                         || InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_ALT);
            boolean ctrl  = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_CONTROL)
                         || InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_CONTROL);
            boolean shift = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_SHIFT)
                         || InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_SHIFT);

            // Convert GLFW mouse button index → our internal code
            // GLFW button 0 (LEFT) → ModConfig.MOUSE_LEFT (-100), etc.
            int mouseCode = switch (button) {
                case 0 -> ModConfig.MOUSE_LEFT;
                case 1 -> ModConfig.MOUSE_RIGHT;
                case 2 -> ModConfig.MOUSE_MIDDLE;
                case 3 -> ModConfig.MOUSE_4;
                case 4 -> ModConfig.MOUSE_5;
                default -> -100 - button;
            };

            applyBind(mouseCode, alt, ctrl, shift);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    // ── Keyboard capture ─────────────────────────────────────────────────────

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (listeningRow != 0) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                listeningRow = 0;
                refreshLabels();
                return true;
            }

            if (isModifier(keyCode)) {
                // Show live preview while only modifiers are held
                showModifierPreview(keyCode, modifiers);
                return true;
            }

            long window = this.client.getWindow().getHandle();
            boolean alt   = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_ALT)
                         || InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_ALT);
            boolean ctrl  = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_CONTROL)
                         || InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_CONTROL);
            boolean shift = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_SHIFT)
                         || InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_SHIFT);

            applyBind(keyCode, alt, ctrl, shift);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
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
        if (listeningRow == 1) this.throwComboButton.setMessage(Text.literal(preview));
        else                    this.moveComboButton.setMessage(Text.literal(preview));
    }

    private void refreshLabels() {
        ModConfig config = ModConfig.get();
        this.throwComboButton.setMessage(Text.literal(
                config.getComboDisplayString(config.throwAllKey, config.throwAllAlt, config.throwAllCtrl, config.throwAllShift)));
        this.moveComboButton.setMessage(Text.literal(
                config.getComboDisplayString(config.moveAllKey, config.moveAllAlt, config.moveAllCtrl, config.moveAllShift)));
    }

    private boolean isModifier(int k) { return isAlt(k) || isCtrl(k) || isShift(k); }
    private boolean isAlt(int k)   { return k == GLFW.GLFW_KEY_LEFT_ALT   || k == GLFW.GLFW_KEY_RIGHT_ALT; }
    private boolean isCtrl(int k)  { return k == GLFW.GLFW_KEY_LEFT_CONTROL || k == GLFW.GLFW_KEY_RIGHT_CONTROL; }
    private boolean isShift(int k) { return k == GLFW.GLFW_KEY_LEFT_SHIFT  || k == GLFW.GLFW_KEY_RIGHT_SHIFT; }

    // ── Render ────────────────────────────────────────────────────────────────

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        this.renderBackground(ctx, mouseX, mouseY, delta);

        int cx = this.width / 2;
        int blockLeft = cx - 10;
        int startY = this.height / 2 - ROW_STRIDE;

        // ── Title ────────────────────────────────────────────────────────────
        ctx.drawCenteredTextWithShadow(this.textRenderer, this.title, cx, 18, COLOR_TITLE);

        // ── Column header "Hotkey" above the bind buttons ─────────────────
        ctx.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal("Hotkey"),
                blockLeft + BIND_W / 2,
                startY - 14,
                COLOR_HEADER
        );

        // ── Row labels (left-aligned, vertically centred with buttons) ─────
        int labelX = cx - 190;
        drawRowLabel(ctx, "dropAllMatching (ThrowAll):", labelX, startY);
        drawRowLabel(ctx, "moveAll (MoveAll):",           labelX, startY + ROW_STRIDE);

        // ── Listening hint at the bottom ──────────────────────────────────
        if (listeningRow != 0) {
            ctx.drawCenteredTextWithShadow(
                    this.textRenderer,
                    Text.literal("Press any key or click a mouse button to bind  |  ESC to cancel"),
                    cx,
                    this.height - 24,
                    COLOR_HINT
            );
        }

        super.render(ctx, mouseX, mouseY, delta);
    }

    /** Draws a right-aligned row label so the text hugs the button group. */
    private void drawRowLabel(DrawContext ctx, String text, int x, int buttonY) {
        // vertically centre the text within the button height
        int textY = buttonY + (ROW_H - this.textRenderer.fontHeight) / 2 + 1;
        ctx.drawTextWithShadow(this.textRenderer, Text.literal(text), x, textY, COLOR_LABEL);
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @Override
    public void close() {
        ModConfig.save();
        this.client.setScreen(this.parent);
    }
}
