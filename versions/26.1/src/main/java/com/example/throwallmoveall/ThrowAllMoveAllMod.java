package com.example.throwallmoveall;

import com.example.throwallmoveall.client.ComboKeyHandler;
import com.example.throwallmoveall.client.ScreenMouseHandler;
import com.example.throwallmoveall.config.ModConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main Client Mod EntryPoint for ThrowAll & MoveAll — Minecraft 26.x Mojang Mappings.
 */
public class ThrowAllMoveAllMod implements ClientModInitializer {
    public static final String MOD_ID = "throwallmoveall";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing ThrowAll & MoveAll Mod v1.5.2 (26.x)...");

        // 1. Load external JSON config
        ModConfig.load();

        // 2. Register Fabric ScreenMouseEvents handler for mouse-button combos
        ScreenMouseHandler.register();

        // 3. Register client tick event for keyboard combos
        ClientTickEvents.END_CLIENT_TICK.register(ComboKeyHandler::checkInput);
    }
}
