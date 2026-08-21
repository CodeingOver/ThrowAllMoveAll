package com.example.throwallmoveall.client;

import com.example.throwallmoveall.config.ModConfig;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

/**
 * Registers Fabric ScreenMouseEvents and ScreenKeyboardEvents listeners on every HandledScreen.
 *
 * Intercepts both mouse clicks and key presses BEFORE Minecraft's native HandledScreen
 * handlers run. Returning false cancels Minecraft's native handler, preventing Q from
 * dropping 1 item and emptying single-item stacks before ThrowAll can process them.
 */
public class ScreenMouseHandler {

    public static void register() {
        ScreenEvents.BEFORE_INIT.register((client, screen, w, h) -> {
            if (!(screen instanceof HandledScreen<?>) || screen instanceof ModConfigScreen) return;

            // Handle mouse clicks (e.g. ALT + LEFT_CLICK)
            ScreenMouseEvents.allowMouseClick(screen).register(
                    (s, mouseX, mouseY, button) -> handleClick(client, button));

            // Handle key presses (e.g. ALT + Q) BEFORE Minecraft's native HandledScreen.keyPressed runs
            ScreenKeyboardEvents.allowKeyPress(screen).register(
                    (s, key, scancode, modifiers) -> handleKeyPress(client, key));
        });
    }

    private static boolean handleKeyPress(MinecraftClient client, int key) {
        if (client.player == null) return true;

        ModConfig config = ModConfig.get();

        // Fast-exit if neither action uses a keyboard key
        if (config.throwAllKey <= 0 && config.moveAllKey <= 0) return true;

        boolean throwMatches = (config.throwAllKey == key);
        boolean moveMatches  = (config.moveAllKey  == key);
        if (!throwMatches && !moveMatches) return true;

        boolean alt   = Screen.hasAltDown();
        boolean ctrl  = Screen.hasControlDown();
        boolean shift = Screen.hasShiftDown();

        if (throwMatches
                && config.throwAllAlt   == alt
                && config.throwAllCtrl  == ctrl
                && config.throwAllShift == shift) {
            InventoryHelper.executeThrowAll(client);
            return false; // CANCEL Minecraft's native keyPressed (prevents Q from dropping 1 item first!)
        }

        if (moveMatches
                && config.moveAllAlt   == alt
                && config.moveAllCtrl  == ctrl
                && config.moveAllShift == shift) {
            InventoryHelper.executeMoveAll(client);
            return false; // CANCEL Minecraft's native keyPressed
        }

        return true;
    }

    private static boolean handleClick(MinecraftClient client, int button) {
        if (client.player == null) return true;

        ModConfig config = ModConfig.get();

        if (config.throwAllKey > 0 && config.moveAllKey > 0) return true;

        int code = -(100 - button);

        boolean throwMatches = (config.throwAllKey == code);
        boolean moveMatches  = (config.moveAllKey  == code);
        if (!throwMatches && !moveMatches) return true;

        boolean alt   = Screen.hasAltDown();
        boolean ctrl  = Screen.hasControlDown();
        boolean shift = Screen.hasShiftDown();

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
