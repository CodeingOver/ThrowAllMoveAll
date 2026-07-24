package com.example.throwallmoveall;

import com.example.throwallmoveall.client.ComboKeyHandler;
import com.example.throwallmoveall.config.ModConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class điểm khởi chạy Client-side của Mod ThrowAll & MoveAll trên Fabric Minecraft 1.20.4.
 * Quản lý cấu hình tệp JSON độc lập (.minecraft/config/throwallmoveall.json) và các phím tổ hợp Combo (Alt+Q, Ctrl+Shift+V...).
 */
public class ThrowAllMoveAllMod implements ClientModInitializer {
    public static final String MOD_ID = "throwallmoveall";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("Khoi tao Mod ThrowAll & MoveAll (Minecraft 1.20.4)...");

        // 1. Đọc tệp cấu hình JSON ngoài (.minecraft/config/throwallmoveall.json)
        ModConfig.load();

        // 2. Lắng nghe sự kiện Client Tick để bắt thao tác nhấn phím tắt tổ hợp Combo
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ComboKeyHandler.checkInput(client);
        });
    }
}
