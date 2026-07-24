package com.example.throwallmoveall;

import com.example.throwallmoveall.client.InventoryHelper;
import com.example.throwallmoveall.client.KeyBindings;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class điểm khởi chạy Client-side của Mod ThrowAll & MoveAll trên Fabric Minecraft 1.20.4.
 */
public class ThrowAllMoveAllMod implements ClientModInitializer {
    public static final String MOD_ID = "throwallmoveall";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("Khoi tao Mod ThrowAll & MoveAll (Minecraft 1.20.4)...");

        // 1. Đăng ký phím tắt Hotkey
        KeyBindings.register();

        // 2. Lắng nghe sự kiện Client Tick để bắt thao tác nhấn phím tắt
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            // Xử lý sự kiện vứt toàn bộ vật phẩm (ThrowAll)
            while (KeyBindings.throwAllKeyBinding.wasPressed()) {
                InventoryHelper.executeThrowAll();
            }

            // Xử lý sự kiện di chuyển toàn bộ vật phẩm (MoveAll)
            while (KeyBindings.moveAllKeyBinding.wasPressed()) {
                InventoryHelper.executeMoveAll();
            }
        });
    }
}
