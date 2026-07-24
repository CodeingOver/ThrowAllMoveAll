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
 * Optimisations applied:
 *  - The redundant `client.currentScreen instanceof HandledScreen` guard is removed;
 *    BEFORE_INIT already ensures we only hook HandledScreens.
 *  - Mouse→internal-code conversion uses arithmetic instead of a switch table.
 *  - Modifier state is read via Screen.has*Down() (single boolean read, no GLFW call).
 *  - MinecraftClient is passed through rather than retrieved via getInstance().
 *
 * Why this works for ALT+click (see ScreenMouseHandler for full rationale):
 *  allowMouseClick fires BEFORE HandledScreen.mouseClicked(), so we can execute
 *  our action and return false to cancel the original Minecraft click.
 */
public class ScreenMouseHandler {

    public static void register() {
        ScreenEvents.BEFORE_INIT.register((client, screen, w, h) -> {
            if (!(screen instanceof HandledScreen<?>) || screen instanceof ModConfigScreen) return;

            ScreenMouseEvents.allowMouseClick(screen).register((s, mouseX, mouseY, button) ->
                    handleClick(client, button)
            );
        });
    }

    /**
     * @return true  → let Minecraft handle the click normally.
     *         false → we handled it; cancel Minecraft's click.
     */
    private static boolean handleClick(MinecraftClient client, int button) {
        if (client.player == null) return true;

        ModConfig config = ModConfig.get();

        // Arithmetic conversion: GLFW button 0 → -100, 1 → -99, 2 → -98 …
        // matches ModConfig.MOUSE_LEFT=-100, MOUSE_RIGHT=-99, MOUSE_MIDDLE=-98
        int code = -(100 - button);

        // Read modifier state once (Screen.has*Down() reads a cached boolean, no GLFW overhead)
        boolean alt   = Screen.hasAltDown();
        boolean ctrl  = Screen.hasControlDown();
        boolean shift = Screen.hasShiftDown();

        // --- ThrowAll mouse combo ---
        if (config.throwAllKey == code
                && config.throwAllAlt   == alt
                && config.throwAllCtrl  == ctrl
                && config.throwAllShift == shift) {
            InventoryHelper.executeThrowAll(client);
            return false;
        }

        // --- MoveAll mouse combo ---
        if (config.moveAllKey == code
                && config.moveAllAlt   == alt
                && config.moveAllCtrl  == ctrl
                && config.moveAllShift == shift) {
            InventoryHelper.executeMoveAll(client);
            return false;
        }

        return true;
    }
}
