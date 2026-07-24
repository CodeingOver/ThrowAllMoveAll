package com.example.throwallmoveall.client;

import com.example.throwallmoveall.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * Keyboard combo handler — runs every client tick.
 *
 * Mouse-button combos (e.g. ALT + LEFT_CLICK) are intentionally NOT polled here;
 * they are captured by the raw GLFW callback in {@link MouseComboHandler} so that
 * modifier keys such as ALT are read correctly from the GLFW mods bitmask before
 * Minecraft's screen system can consume the event.
 */
public class ComboKeyHandler {

    private static boolean wasThrowPressed = false;
    private static boolean wasMovePressed = false;

    public static void checkInput(MinecraftClient client) {
        if (client.player == null || client.getWindow() == null) return;

        // Strictly restrict execution to active inventory/container screen (HandledScreen)
        if (!(client.currentScreen instanceof HandledScreen<?>)) {
            wasThrowPressed = false;
            wasMovePressed = false;
            return;
        }

        long window = client.getWindow().getHandle();
        ModConfig config = ModConfig.get();

        // Detect active modifier key states
        boolean alt = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_ALT)
                || InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_ALT);
        boolean ctrl = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_CONTROL)
                || InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_CONTROL);
        boolean shift = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_SHIFT)
                || InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_SHIFT);

        // --- ThrowAll (keyboard keys only; negative codes are mouse → skip) ---
        if (config.throwAllKey > 0) {
            boolean keyIsDown = InputUtil.isKeyPressed(window, config.throwAllKey);
            boolean modifiersMatch = (config.throwAllAlt == alt)
                    && (config.throwAllCtrl == ctrl)
                    && (config.throwAllShift == shift);

            if (keyIsDown && modifiersMatch) {
                if (!wasThrowPressed) {
                    wasThrowPressed = true;
                    InventoryHelper.executeThrowAll();
                }
            } else {
                wasThrowPressed = false;
            }
        } else {
            wasThrowPressed = false;
        }

        // --- MoveAll (keyboard keys only; negative codes are mouse → handled by MouseComboHandler) ---
        if (config.moveAllKey > 0) {
            boolean keyIsDown = InputUtil.isKeyPressed(window, config.moveAllKey);
            boolean modifiersMatch = (config.moveAllAlt == alt)
                    && (config.moveAllCtrl == ctrl)
                    && (config.moveAllShift == shift);

            if (keyIsDown && modifiersMatch) {
                if (!wasMovePressed) {
                    wasMovePressed = true;
                    InventoryHelper.executeMoveAll();
                }
            } else {
                wasMovePressed = false;
            }
        } else {
            wasMovePressed = false;
        }
    }
}
