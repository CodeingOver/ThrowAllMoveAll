package com.example.throwallmoveall.client;

import com.example.throwallmoveall.config.ModConfig;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.lwjgl.glfw.GLFW;

public class ScreenMouseHandler {

    public static void register() {
        ScreenEvents.BEFORE_INIT.register((client, screen, w, h) -> {
            if (!(screen instanceof AbstractContainerScreen<?>) || screen instanceof ModConfigScreen) return;

            // Handle mouse clicks (e.g. ALT + LEFT_CLICK)
            ScreenMouseEvents.allowMouseClick(screen).register(
                    (s, click) -> handleClick(client, click.button()));

            // Handle key presses (e.g. ALT + Q) BEFORE Minecraft's native AbstractContainerScreen.keyPressed runs
            ScreenKeyboardEvents.allowKeyPress(screen).register(
                    (s, input) -> handleKeyPress(client, input.key()));
        });
    }

    private static boolean isAltDown(long handle) {
        return GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_LEFT_ALT) == GLFW.GLFW_PRESS
            || GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_RIGHT_ALT) == GLFW.GLFW_PRESS;
    }

    private static boolean isCtrlDown(long handle) {
        return GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS
            || GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS;
    }

    private static boolean isShiftDown(long handle) {
        return GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
            || GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
    }

    private static boolean handleKeyPress(Minecraft client, int key) {
        if (client.player == null) return true;

        ModConfig config = ModConfig.get();

        if (config.throwAllKey <= 0 && config.moveAllKey <= 0) return true;

        boolean throwMatches = (config.throwAllKey == key);
        boolean moveMatches  = (config.moveAllKey  == key);
        if (!throwMatches && !moveMatches) return true;

        long handle = client.getWindow().handle();
        boolean alt   = isAltDown(handle);
        boolean ctrl  = isCtrlDown(handle);
        boolean shift = isShiftDown(handle);

        if (throwMatches
                && config.throwAllAlt   == alt
                && config.throwAllCtrl  == ctrl
                && config.throwAllShift == shift) {
            InventoryHelper.executeThrowAll(client);
            return false;
        }

        if (moveMatches
                && config.moveAllAlt   == alt
                && config.moveAllCtrl  == ctrl
                && config.moveAllShift == shift) {
            InventoryHelper.executeMoveAll(client);
            return false;
        }

        return true;
    }

    private static boolean handleClick(Minecraft client, int button) {
        if (client.player == null) return true;

        ModConfig config = ModConfig.get();

        if (config.throwAllKey > 0 && config.moveAllKey > 0) return true;

        int code = -(100 - button);

        boolean throwMatches = (config.throwAllKey == code);
        boolean moveMatches  = (config.moveAllKey  == code);
        if (!throwMatches && !moveMatches) return true;

        long handle = client.getWindow().handle();
        boolean alt   = isAltDown(handle);
        boolean ctrl  = isCtrlDown(handle);
        boolean shift = isShiftDown(handle);

        if (throwMatches
                && config.throwAllAlt   == alt
                && config.throwAllCtrl  == ctrl
                && config.throwAllShift == shift) {
            InventoryHelper.executeThrowAll(client);
            return false;
        }

        if (moveMatches
                && config.moveAllAlt   == alt
                && config.moveAllCtrl  == ctrl
                && config.moveAllShift == shift) {
            InventoryHelper.executeMoveAll(client);
            return false;
        }

        return true;
    }
}
