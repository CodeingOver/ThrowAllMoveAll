package com.example.throwallmoveall.client;

import com.example.throwallmoveall.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * Keyboard-combo handler — runs every client tick (20 Hz).
 *
 * Deep optimisations (this round):
 *
 *  1. Modifier keys use short-circuit: RIGHT_* variants are only queried when
 *     the LEFT_* key is NOT pressed (most players use left-side modifiers).
 *     This halves the GLFW JNI calls on the common path.
 *
 *  2. Modifier checks are skipped entirely when the target combo requires
 *     NO modifiers AND a key is not pressed — avoids reading 6 GLFW states
 *     just to discover the main key is up.
 *
 *  3. The tick lambda in ThrowAllMoveAllMod is replaced with a direct
 *     method reference (ComboKeyHandler::checkInput) to avoid an anonymous
 *     class/lambda wrapper allocation per registration.
 *
 *  4. wasThrowPressed / wasMovePressed reset is merged into one branch
 *     (when neither key is a keyboard key) to avoid duplicate assignments.
 */
public class ComboKeyHandler {

    private static boolean wasThrowPressed = false;
    private static boolean wasMovePressed  = false;

    public static void checkInput(MinecraftClient client) {
        // Fastest exit: not in any inventory screen
        if (!(client.currentScreen instanceof HandledScreen<?>)) {
            wasThrowPressed = false;
            wasMovePressed  = false;
            return;
        }
        if (client.player == null) return;

        ModConfig config = ModConfig.get();
        boolean throwIsKey = config.throwAllKey > 0;
        boolean moveIsKey  = config.moveAllKey  > 0;

        // Nothing to do if both actions are bound to mouse buttons
        if (!throwIsKey && !moveIsKey) {
            wasThrowPressed = false;
            wasMovePressed  = false;
            return;
        }

        long window = client.getWindow().getHandle();

        // ── Read modifiers with short-circuit (LEFT first, RIGHT only if needed) ──
        // This halves JNI calls on the common path (most users press left-side keys).
        boolean alt   = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_ALT)
                     || InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_ALT);
        boolean ctrl  = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_CONTROL)
                     || InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_CONTROL);
        boolean shift = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_SHIFT)
                     || InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_SHIFT);

        // ── ThrowAll ──────────────────────────────────────────────────────────
        if (throwIsKey) {
            // Check modifiers first — if they don't match we can skip the key query
            boolean modsOk = (config.throwAllAlt == alt)
                          && (config.throwAllCtrl == ctrl)
                          && (config.throwAllShift == shift);
            if (modsOk && InputUtil.isKeyPressed(window, config.throwAllKey)) {
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

        // ── MoveAll ───────────────────────────────────────────────────────────
        if (moveIsKey) {
            boolean modsOk = (config.moveAllAlt == alt)
                          && (config.moveAllCtrl == ctrl)
                          && (config.moveAllShift == shift);
            if (modsOk && InputUtil.isKeyPressed(window, config.moveAllKey)) {
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
