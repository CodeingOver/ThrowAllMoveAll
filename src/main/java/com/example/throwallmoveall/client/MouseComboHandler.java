package com.example.throwallmoveall.client;

import com.example.throwallmoveall.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

/**
 * Raw GLFW mouse-button callback.
 *
 * Installed once at startup via {@link #install(MinecraftClient)}.
 * Chains the original Minecraft callback so no mouse events are lost.
 *
 * Purpose: detect modifier keys (ALT / CTRL / SHIFT) from the GLFW {@code mods}
 * bitmask, which is reliably populated even when ALT suppresses click forwarding
 * inside Minecraft's screen system.  This fixes the "ALT + LEFT_CLICK has no
 * effect" bug where Minecraft consumed the event before our mod's tick-based
 * handler could see it.
 */
public class MouseComboHandler extends GLFWMouseButtonCallback {

    private static MouseComboHandler INSTANCE;
    private static GLFWMouseButtonCallback previousCallback;

    /** Debounce: was the combo already fired for the current mouse-button press? */
    private static boolean throwFired = false;
    private static boolean moveFired  = false;

    /** Install this callback on the GLFW window, preserving the existing callback. */
    public static void install(MinecraftClient client) {
        if (INSTANCE != null) return;
        long window = client.getWindow().getHandle();
        INSTANCE = new MouseComboHandler();
        previousCallback = GLFW.glfwSetMouseButtonCallback(window, INSTANCE);
    }

    @Override
    public void invoke(long window, int button, int action, int mods) {
        // 1. Always forward to Minecraft's original handler first.
        if (previousCallback != null) {
            previousCallback.invoke(window, button, action, mods);
        }

        // 2. Reset debounce flags on button release.
        if (action == GLFW.GLFW_RELEASE) {
            throwFired = false;
            moveFired  = false;
            return;
        }

        // 3. Only process PRESS events from here on.
        if (action != GLFW.GLFW_PRESS) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        // 4. Only fire inside inventory / container screens (not in the config GUI itself).
        if (!(client.currentScreen instanceof HandledScreen<?>)) return;
        if (client.currentScreen instanceof ModConfigScreen) return;

        // 5. Decode modifier state from the GLFW mods bitmask.
        boolean alt   = (mods & GLFW.GLFW_MOD_ALT)     != 0;
        boolean ctrl  = (mods & GLFW.GLFW_MOD_CONTROL) != 0;
        boolean shift = (mods & GLFW.GLFW_MOD_SHIFT)   != 0;

        // 6. Convert GLFW button index → our internal mouse code.
        //    ModConfig encodes mouse buttons as: MOUSE_LEFT=-100, MOUSE_RIGHT=-99, …
        //    So button index b → code = -100 - b + 0 = -(100 + b).
        //    Wait — check: button=0 (LEFT_CLICK) → -100-0 = -100 ✓
        //                  button=1 (RIGHT_CLICK) → -100-1 = -101 ≠ MOUSE_RIGHT(-99) ✗
        //
        //    The config stores: MOUSE_LEFT=-100, MOUSE_RIGHT=-99, MOUSE_MIDDLE=-98
        //    which correspond to GLFW buttons 0, 1, 2 respectively.
        //    Mapping: internalCode = -(100 - button)  → for button 0: -100 ✓, button 1: -99 ✓, button 2: -98 ✓
        int internalCode = -(100 - button);

        ModConfig config = ModConfig.get();

        // --- ThrowAll ---
        if (config.throwAllKey < 0 && config.throwAllKey == internalCode) {
            boolean modifiersMatch = (config.throwAllAlt == alt)
                    && (config.throwAllCtrl == ctrl)
                    && (config.throwAllShift == shift);
            if (modifiersMatch && !throwFired) {
                throwFired = true;
                InventoryHelper.executeThrowAll();
            }
        }

        // --- MoveAll ---
        if (config.moveAllKey < 0 && config.moveAllKey == internalCode) {
            boolean modifiersMatch = (config.moveAllAlt == alt)
                    && (config.moveAllCtrl == ctrl)
                    && (config.moveAllShift == shift);
            if (modifiersMatch && !moveFired) {
                moveFired = true;
                InventoryHelper.executeMoveAll();
            }
        }
    }
}
