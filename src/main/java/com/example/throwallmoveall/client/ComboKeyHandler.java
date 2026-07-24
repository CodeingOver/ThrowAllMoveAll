package com.example.throwallmoveall.client;

import com.example.throwallmoveall.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * Xử lý kiểm tra các tổ hợp phím Combo (Alt + Q, Ctrl + Shift + V, Shift + X...) ở mức thấp.
 */
public class ComboKeyHandler {

    private static boolean wasThrowPressed = false;
    private static boolean wasMovePressed = false;

    public static void checkInput(MinecraftClient client) {
        if (client.player == null || client.getWindow() == null) return;

        long window = client.getWindow().getHandle();
        ModConfig config = ModConfig.get();

        // Lấy trạng thái các phím Modifier
        boolean alt = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_ALT) || InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_ALT);
        boolean ctrl = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_CONTROL) || InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_CONTROL);
        boolean shift = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_SHIFT) || InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_SHIFT);

        // 1. Kiểm tra tổ hợp phím ThrowAll
        if (config.throwAllKey != GLFW.GLFW_KEY_UNKNOWN) {
            boolean keyIsDown = InputUtil.isKeyPressed(window, config.throwAllKey);
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

        // 2. Kiểm tra tổ hợp phím MoveAll
        if (config.moveAllKey != GLFW.GLFW_KEY_UNKNOWN) {
            boolean keyIsDown = InputUtil.isKeyPressed(window, config.moveAllKey);
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
}
