package com.example.throwallmoveall;

import com.example.throwallmoveall.client.ComboKeyHandler;
import com.example.throwallmoveall.client.MouseComboHandler;
import com.example.throwallmoveall.config.ModConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main Client Mod EntryPoint for ThrowAll & MoveAll Mod on Fabric Minecraft 1.20.4.
 * Manages external JSON config (.minecraft/config/throwallmoveall.json) and combo shortcut listeners.
 */
public class ThrowAllMoveAllMod implements ClientModInitializer {
    public static final String MOD_ID = "throwallmoveall";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing ThrowAll & MoveAll Mod (Minecraft 1.20.4)...");

        // 1. Load external JSON config (.minecraft/config/throwallmoveall.json)
        ModConfig.load();

        // 2. Install raw GLFW mouse callback once window is ready.
        //    This fixes ALT+click combos being consumed by Minecraft's screen system
        //    before our mod can see them.
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            MouseComboHandler.install(client);
            LOGGER.info("MouseComboHandler installed on GLFW window.");
        });

        // 3. Register end client tick event to handle keyboard combo shortcuts only.
        //    Mouse-button combos are now handled via raw GLFW callback in MouseComboHandler.
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ComboKeyHandler.checkInput(client);
        });
    }
}
