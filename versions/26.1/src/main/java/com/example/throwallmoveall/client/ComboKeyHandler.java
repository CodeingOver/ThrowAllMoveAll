package com.example.throwallmoveall.client;

import com.example.throwallmoveall.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.lwjgl.glfw.GLFW;

public class ComboKeyHandler {

    private static boolean wasThrowPressed = false;
    private static boolean wasMovePressed  = false;

    private static boolean isKeyDown(long window, int key) {
        return GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS;
    }

    public static void checkInput(Minecraft client) {
        if (!(client.screen instanceof AbstractContainerScreen<?>)) {
            wasThrowPressed = false;
            wasMovePressed  = false;
            return;
        }
        if (client.player == null) return;

        ModConfig config = ModConfig.get();
        boolean throwIsKey = config.throwAllKey > 0;
        boolean moveIsKey  = config.moveAllKey  > 0;

        if (!throwIsKey && !moveIsKey) {
            wasThrowPressed = false;
            wasMovePressed  = false;
            return;
        }

        long window = client.getWindow().handle();

        boolean alt   = isKeyDown(window, GLFW.GLFW_KEY_LEFT_ALT)
                     || isKeyDown(window, GLFW.GLFW_KEY_RIGHT_ALT);
        boolean ctrl  = isKeyDown(window, GLFW.GLFW_KEY_LEFT_CONTROL)
                     || isKeyDown(window, GLFW.GLFW_KEY_RIGHT_CONTROL);
        boolean shift = isKeyDown(window, GLFW.GLFW_KEY_LEFT_SHIFT)
                     || isKeyDown(window, GLFW.GLFW_KEY_RIGHT_SHIFT);

        if (throwIsKey) {
            boolean modsOk = (config.throwAllAlt == alt)
                          && (config.throwAllCtrl == ctrl)
                          && (config.throwAllShift == shift);
            if (modsOk && isKeyDown(window, config.throwAllKey)) {
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

        if (moveIsKey) {
            boolean modsOk = (config.moveAllAlt == alt)
                          && (config.moveAllCtrl == ctrl)
                          && (config.moveAllShift == shift);
            if (modsOk && isKeyDown(window, config.moveAllKey)) {
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
