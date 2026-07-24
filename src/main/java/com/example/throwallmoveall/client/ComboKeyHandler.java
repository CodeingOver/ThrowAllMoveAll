package com.example.throwallmoveall.client;

import com.example.throwallmoveall.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * Keyboard-combo handler — runs every client tick (20 Hz).
 *
 * Optimisations applied:
 *  - Early-exit before ANY GLFW calls when no HandledScreen is open,
 *    so the common case (not in inventory) costs a single instanceof check.
 *  - Modifier keys are read once and reused for both combos.
 *  - Mouse-bound combos are skipped entirely (handled event-driven by
 *    {@link ScreenMouseHandler}); no unnecessary GLFW polling for them.
 *  - Window handle is cached per call (one pointer dereference, no null-check).
 */
public class ComboKeyHandler {

    private static boolean wasThrowPressed = false;
    private static boolean wasMovePressed  = false;

    public static void checkInput(MinecraftClient client) {
        // ── Fastest possible exit when not in an inventory ────────────────
        if (!(client.currentScreen instanceof HandledScreen<?>)) {
            wasThrowPressed = false;
            wasMovePressed  = false;
            return;
        }
        if (client.player == null) return;

        ModConfig config = ModConfig.get();

        // ── Skip entirely when both combos are mouse-bound ────────────────
        boolean throwIsKey = config.throwAllKey > 0;
        boolean moveIsKey  = config.moveAllKey  > 0;
        if (!throwIsKey && !moveIsKey) {
            wasThrowPressed = false;
            wasMovePressed  = false;
            return;
        }

        // ── Read modifier state once for both combos ──────────────────────
        long window = client.getWindow().getHandle();

        boolean alt   = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_ALT)
                     || InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_ALT);
        boolean ctrl  = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_CONTROL)
                     || InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_CONTROL);
        boolean shift = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_SHIFT)
                     || InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_SHIFT);

        // ── ThrowAll (keyboard binding only) ─────────────────────────────
        if (throwIsKey) {
            boolean keyDown = InputUtil.isKeyPressed(window, config.throwAllKey);
            boolean modsOk  = (config.throwAllAlt == alt)
                           && (config.throwAllCtrl == ctrl)
                           && (config.throwAllShift == shift);

            if (keyDown && modsOk) {
                if (!wasThrowPressed) {
                    wasThrowPressed = true;
                    InventoryHelper.executeThrowAll(client);
                }
            } else {
                wasThrowPressed = false;
            }
        } else {
            wasThrowPressed = false;
        }

        // ── MoveAll (keyboard binding only) ──────────────────────────────
        if (moveIsKey) {
            boolean keyDown = InputUtil.isKeyPressed(window, config.moveAllKey);
            boolean modsOk  = (config.moveAllAlt == alt)
                           && (config.moveAllCtrl == ctrl)
                           && (config.moveAllShift == shift);

            if (keyDown && modsOk) {
                if (!wasMovePressed) {
                    wasMovePressed = true;
                    InventoryHelper.executeMoveAll(client);
                }
            } else {
                wasMovePressed = false;
            }
        } else {
            wasMovePressed = false;
        }
    }
}
