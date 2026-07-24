package com.example.throwallmoveall.client;

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

import java.lang.reflect.Field;

/**
 * Lớp xử lý kho đồ Client-side cho ThrowAll & MoveAll.
 * Sử dụng Reflection tự động dò tìm field ô slot (focusedSlot) có Caching hiệu năng cao.
 * Hoàn toàn KHÔNG dùng Mixin -> Loại bỏ 100% nguy cơ crash với Sodium và mọi Mod khác.
 */
public class InventoryHelper {

    private static Field focusedSlotField = null;

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

        Slot focusedSlot = getFocusedSlot(handledScreen);
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

        Slot focusedSlot = getFocusedSlot(handledScreen);
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
     * Lấy ô slot đang được trỏ chuột bằng Reflection (có cache Field, tốc độ cực nhanh, 0 tốn Mixin).
     */
    private static Slot getFocusedSlot(HandledScreen<?> screen) {
        try {
            if (focusedSlotField == null) {
                for (Field field : HandledScreen.class.getDeclaredFields()) {
                    if (field.getType() == Slot.class) {
                        field.setAccessible(true);
                        focusedSlotField = field;
                        break;
                    }
                }
            }
            if (focusedSlotField != null) {
                return (Slot) focusedSlotField.get(screen);
            }
        } catch (Throwable ignored) {}
        return null;
    }

    /**
     * Kiểm tra ô slot có nên bỏ qua hay không (bộ lọc an toàn).
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
