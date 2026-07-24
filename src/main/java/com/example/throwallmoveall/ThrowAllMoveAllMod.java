package com.example.throwallmoveall;

import com.example.throwallmoveall.client.ComboKeyHandler;
import com.example.throwallmoveall.config.ModConfig;
import net.fabricmc.api.ClientModInitializer;
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

        // 2. Register end client tick event to handle combo key shortcuts
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ComboKeyHandler.checkInput(client);
        });
    }
}
