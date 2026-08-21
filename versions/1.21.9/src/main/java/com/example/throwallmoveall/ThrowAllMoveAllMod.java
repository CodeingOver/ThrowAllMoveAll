package com.example.throwallmoveall;

import com.example.throwallmoveall.client.ComboKeyHandler;
import com.example.throwallmoveall.client.ScreenMouseHandler;
import com.example.throwallmoveall.config.ModConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main Client Mod EntryPoint for ThrowAll & MoveAll — supports Fabric Minecraft 1.19 – 26.x.
 */
public class ThrowAllMoveAllMod implements ClientModInitializer {
    public static final String MOD_ID = "throwallmoveall";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing ThrowAll & MoveAll Mod v1.5.1...");

        // 1. Load external JSON config
        ModConfig.load();

        // 2. Register Fabric ScreenMouseEvents handler for mouse-button combos (e.g. ALT + LEFT_CLICK).
        //    This fires BEFORE HandledScreen.mouseClicked(), allowing us to intercept ALT+click
        //    and cancel the original click so Minecraft doesn't double-act on it.
        ScreenMouseHandler.register();

        // 3. Register client tick event for keyboard combos (method reference — no lambda wrapper).
        ClientTickEvents.END_CLIENT_TICK.register(ComboKeyHandler::checkInput);
    }
}
