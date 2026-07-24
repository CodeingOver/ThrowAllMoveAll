package com.example.throwallmoveall.client;

import com.example.throwallmoveall.config.ModConfig;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * Registers a Fabric ScreenMouseEvents.allowMouseClick listener on every HandledScreen.
 *
 * Why this approach (instead of polling in client tick or raw GLFW callback):
 *  - allowMouseClick fires BEFORE HandledScreen.mouseClicked(), so we can intercept
 *    ALT+click before Minecraft processes it.
 *  - Returning false cancels the original click so Minecraft doesn't double-act.
 *  - The HandledScreen's focusedSlot field is already populated at this point
 *    (it was updated during the previous mouseMoved / render call).
 *  - Screen.hasAltDown() / hasControlDown() / hasShiftDown() reliably detect
 *    modifier state at the exact moment of click.
 *
 * This is the same pattern used by Item Scroller and other inventory utility mods.
 */
public class ScreenMouseHandler {

    public static void register() {
        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            // Only hook into inventory / container screens, not our config screen
            if (!(screen instanceof HandledScreen<?>) || screen instanceof ModConfigScreen) return;

            ScreenMouseEvents.allowMouseClick(screen).register((s, mouseX, mouseY, button) ->
                    handleClick(client, button)
            );
        });
    }

    /**
     * Returns true  → allow the click to proceed normally.
     * Returns false → cancel the click (our combo action was fired instead).
     */
    private static boolean handleClick(MinecraftClient client, int button) {
        if (client.player == null) return true;
        if (!(client.currentScreen instanceof HandledScreen<?>)) return true;

        ModConfig config = ModConfig.get();

        // Convert GLFW mouse button index → our internal code
        // GLFW: 0=LEFT, 1=RIGHT, 2=MIDDLE  →  ModConfig: MOUSE_LEFT=-100, MOUSE_RIGHT=-99, MOUSE_MIDDLE=-98
        int internalCode = -(100 - button); // button=0→-100, button=1→-99, button=2→-98 ✓

        boolean alt   = Screen.hasAltDown();
        boolean ctrl  = Screen.hasControlDown();
        boolean shift = Screen.hasShiftDown();

        boolean consumed = false;

        // --- ThrowAll mouse combo ---
        if (config.throwAllKey < 0 && config.throwAllKey == internalCode) {
            boolean modifiersMatch = (config.throwAllAlt == alt)
                    && (config.throwAllCtrl == ctrl)
                    && (config.throwAllShift == shift);
            if (modifiersMatch) {
                InventoryHelper.executeThrowAll();
                consumed = true;
            }
        }

        // --- MoveAll mouse combo ---
        if (config.moveAllKey < 0 && config.moveAllKey == internalCode) {
            boolean modifiersMatch = (config.moveAllAlt == alt)
                    && (config.moveAllCtrl == ctrl)
                    && (config.moveAllShift == shift);
            if (modifiersMatch) {
                InventoryHelper.executeMoveAll();
                consumed = true;
            }
        }

        // Return false to cancel the original click when we handled it
        return !consumed;
    }
}
