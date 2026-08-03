package com.example.throwallmoveall.client;

import com.example.throwallmoveall.config.ModConfig;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

/**
 * Registers a Fabric ScreenMouseEvents.allowMouseClick listener on every HandledScreen.
 *
 * Deep optimisations (this round):
 *
 *  1. handleClick() now fast-exits immediately when both combos are keyboard-bound
 *     (config.throwAllKey > 0 && config.moveAllKey > 0) — the mouse handler has
 *     zero work to do in that common configuration.
 *
 *  2. The modifier state is read AFTER confirming the mouse button matches at
 *     least one combo's key code, avoiding three Screen.has*Down() calls on
 *     every click that doesn't match either combo button.
 *
 *  3. Modifier reads are shared between ThrowAll and MoveAll checks when both
 *     are bound to the same mouse button (edge case, but free).
 */
public class ScreenMouseHandler {

    public static void register() {
        ScreenEvents.BEFORE_INIT.register((client, screen, w, h) -> {
            if (!(screen instanceof HandledScreen<?>) || screen instanceof ModConfigScreen) return;
            ScreenMouseEvents.allowMouseClick(screen).register(
                    (s, mouseX, mouseY, button) -> handleClick(client, button));
        });
    }

    private static boolean handleClick(MinecraftClient client, int button) {
        if (client.player == null) return true;

        ModConfig config = ModConfig.get();

        // Fast-exit: both combos are keyboard-bound → nothing to do for mouse clicks
        if (config.throwAllKey > 0 && config.moveAllKey > 0) return true;

        // Arithmetic conversion: GLFW button 0 → -100, 1 → -99, 2 → -98 …
        int code = -(100 - button);

        // Check if this button is relevant to either combo BEFORE reading modifiers.
        // Screen.has*Down() is cheap (reads a volatile boolean), but we still avoid
        // 3 reads when neither combo is bound to this button.
        boolean throwMatches = (config.throwAllKey == code);
        boolean moveMatches  = (config.moveAllKey  == code);
        if (!throwMatches && !moveMatches) return true;

        // Read modifier state once — shared between both checks
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
