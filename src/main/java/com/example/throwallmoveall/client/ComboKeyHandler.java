package com.example.throwallmoveall.client;

import com.example.throwallmoveall.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * Low-level combo key & mouse button event handler (Item Scroller style).
 * Handles combinations like LEFT_ALT + Q, LEFT_SHIFT + LEFT_CLICK, BUTTON_3, etc.
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
        boolean alt = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_ALT) || InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_ALT);
        boolean ctrl = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_CONTROL) || InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_CONTROL);
        boolean shift = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_SHIFT) || InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_SHIFT);

        // 1. Check ThrowAll Shortcut Combo
        if (config.throwAllKey != GLFW.GLFW_KEY_UNKNOWN) {
            boolean keyIsDown = isTriggerPressed(window, config.throwAllKey);
            boolean modifiersMatch = (config.throwAllAlt == alt) && (config.throwAllCtrl == ctrl) && (config.throwAllShift == shift);

            if (keyIsDown && modifiersMatch) {
                if (!wasThrowPressed) {
                    wasThrowPressed = true;
                    InventoryHelper.executeThrowAll();
                }
            } else {
                wasThrowPressed = false;
            }
        }

        // 2. Check MoveAll Shortcut Combo
        if (config.moveAllKey != GLFW.GLFW_KEY_UNKNOWN) {
            boolean keyIsDown = isTriggerPressed(window, config.moveAllKey);
            boolean modifiersMatch = (config.moveAllAlt == alt) && (config.moveAllCtrl == ctrl) && (config.moveAllShift == shift);

            if (keyIsDown && modifiersMatch) {
                if (!wasMovePressed) {
                    wasMovePressed = true;
                    InventoryHelper.executeMoveAll();
                }
            } else {
                wasMovePressed = false;
            }
        }
    }

    /**
     * Checks if target keyboard key OR mouse button is pressed.
     */
    private static boolean isTriggerPressed(long window, int triggerCode) {
        if (triggerCode < 0) {
            // Negative triggerCode represents Mouse Button
            int mouseButton = -100 - triggerCode;
            return GLFW.glfwGetMouseButton(window, mouseButton) == GLFW.GLFW_PRESS;
        } else {
            // Positive triggerCode represents Keyboard Key
            return InputUtil.isKeyPressed(window, triggerCode);
        }
    }
}
