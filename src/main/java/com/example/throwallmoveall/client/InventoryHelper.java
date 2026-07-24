package com.example.throwallmoveall.client;

import com.example.throwallmoveall.mixin.HandledScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

/**
 * Lớp tối ưu hóa hiệu năng và an toàn mạng cho các thao tác kho đồ (ThrowAll & MoveAll).
 * Chỉ hoạt động khi trỏ chuột vào một ô chứa vật phẩm hợp lệ trong giao diện GUI.
 */
public class InventoryHelper {

    /**
     * Di chuyển nhanh tất cả vật phẩm CÙNG LOẠI với ô đang trỏ chuột.
     */
    public static void executeMoveAll() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        ClientPlayerInteractionManager interactionManager = client.interactionManager;

        if (player == null || interactionManager == null || player.isSpectator()) return;

        Screen currentScreen = client.currentScreen;
        if (!(currentScreen instanceof HandledScreen<?> handledScreen)) return;

        Slot focusedSlot = getFocusedSlotFast(handledScreen);
        // Chỉ hoạt động khi trỏ chuột vào ô chứa vật phẩm hợp lệ
        if (focusedSlot == null || !focusedSlot.hasStack() || !focusedSlot.canTakeItems(player)) return;

        ScreenHandler handler = handledScreen.getScreenHandler();
        ItemStack targetStack = focusedSlot.getStack();
        Item targetItem = targetStack.getItem();
        boolean sourceIsPlayerInv = isPlayerSlot(focusedSlot, player);

        for (Slot slot : handler.slots) {
            if (shouldSkipSlot(slot, player)) continue;

            if (isPlayerSlot(slot, player) == sourceIsPlayerInv) {
                ItemStack stack = slot.getStack();
                if (!stack.isEmpty() && stack.isOf(targetItem)) {
                    interactionManager.clickSlot(handler.syncId, slot.id, 0, SlotActionType.QUICK_MOVE, player);
                }
            }
        }
    }

    /**
     * Vứt toàn bộ các ô vật phẩm CÙNG LOẠI với ô đang trỏ chuột ra đất.
     */
    public static void executeThrowAll() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        ClientPlayerInteractionManager interactionManager = client.interactionManager;

        if (player == null || interactionManager == null || player.isSpectator()) return;

        Screen currentScreen = client.currentScreen;
        if (!(currentScreen instanceof HandledScreen<?> handledScreen)) return;

        Slot focusedSlot = getFocusedSlotFast(handledScreen);
        // Chỉ hoạt động khi trỏ chuột vào ô chứa vật phẩm hợp lệ
        if (focusedSlot == null || !focusedSlot.hasStack() || !focusedSlot.canTakeItems(player)) return;

        ScreenHandler handler = handledScreen.getScreenHandler();
        ItemStack targetStack = focusedSlot.getStack();
        Item targetItem = targetStack.getItem();

        for (Slot slot : handler.slots) {
            if (shouldSkipSlot(slot, player)) continue;

            ItemStack stack = slot.getStack();
            if (!stack.isEmpty() && stack.isOf(targetItem)) {
                // Button 1 = Vứt nguyên stack vật phẩm ra ngoài môi trường
                interactionManager.clickSlot(handler.syncId, slot.id, 1, SlotActionType.THROW, player);
            }
        }
    }

    /**
     * Lấy ô đang trỏ chuột cực nhanh thông qua Mixin Accessor (Tốc độ tối đa, 0 Reflection).
     */
    private static Slot getFocusedSlotFast(HandledScreen<?> screen) {
        if (screen instanceof HandledScreenAccessor accessor) {
            return accessor.getFocusedSlot();
        }
        return null;
    }

    /**
     * Bật bộ lọc an toàn: Kiểm tra ô có nên bỏ qua hay không.
     * Bỏ qua ô nếu:
     * - Ô thuộc ô kết quả chế tạo (CraftingResultInventory) -> Tránh lỗi server desync hoặc auto-craft ngoài ý muốn.
     * - Người chơi không có quyền rút đồ từ ô đó (canTakeItems = false).
     */
    private static boolean shouldSkipSlot(Slot slot, ClientPlayerEntity player) {
        if (slot == null) return true;
        if (slot.inventory instanceof CraftingResultInventory) return true;
        return !slot.canTakeItems(player);
    }

    /**
     * Kiểm tra Slot có phải thuộc kho cá nhân của Player hay không.
     */
    private static boolean isPlayerSlot(Slot slot, ClientPlayerEntity player) {
        return slot.inventory instanceof PlayerInventory;
    }
}
