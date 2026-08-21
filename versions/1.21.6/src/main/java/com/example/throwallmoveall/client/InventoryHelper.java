package com.example.throwallmoveall.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Client-side inventory action executor for ThrowAll & MoveAll.
 * Supports both Survival and Creative mode inventories.
 */
public class InventoryHelper {

    // ── MethodHandle cache for HandledScreen.focusedSlot ─────────────────────
    private static final MethodHandle FOCUSED_SLOT_HANDLE = resolveFocusedSlotHandle();

    private static MethodHandle resolveFocusedSlotHandle() {
        try {
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(
                    HandledScreen.class, MethodHandles.lookup());
            for (Field f : HandledScreen.class.getDeclaredFields()) {
                if (f.getType() == Slot.class) {
                    return lookup.unreflectGetter(f);
                }
            }
        } catch (Throwable t) {
        }
        return null;
    }

    // ── MethodHandle cache for CreativeInventoryScreen.CreativeSlot.slot ──────
    private static final MethodHandle CREATIVE_SLOT_HANDLE = resolveCreativeSlotHandle();

    private static MethodHandle resolveCreativeSlotHandle() {
        try {
            for (Class<?> inner : CreativeInventoryScreen.class.getDeclaredClasses()) {
                if (Slot.class.isAssignableFrom(inner)) {
                    MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(inner, MethodHandles.lookup());
                    for (Field f : inner.getDeclaredFields()) {
                        if (f.getType() == Slot.class) {
                            return lookup.unreflectGetter(f);
                        }
                    }
                }
            }
        } catch (Throwable t) {
        }
        return null;
    }

    private static Slot getRealSlot(Slot slot) {
        if (CREATIVE_SLOT_HANDLE != null && slot != null) {
            try {
                Object obj = CREATIVE_SLOT_HANDLE.invoke(slot);
                if (obj instanceof Slot s) return s;
            } catch (Throwable ignored) {
            }
        }
        return slot;
    }

    // ── Public entry points ──────────────────────────────────────────────────

    /** Move all items of the same type as the hovered slot to the other side. */
    public static void executeMoveAll(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        ClientPlayerInteractionManager im = client.interactionManager;
        if (player == null || im == null || player.isSpectator()) return;
        if (!(client.currentScreen instanceof HandledScreen<?> screen)) return;

        Slot focused = getFocusedSlot(screen);
        if (focused == null || !focused.hasStack() || !focused.canTakeItems(player)) return;

        boolean isCreative = screen instanceof CreativeInventoryScreen;
        Slot realFocused = isCreative ? getRealSlot(focused) : focused;
        boolean srcInPlayer = realFocused.inventory instanceof PlayerInventory;

        // In Creative Mode, MoveAll only makes sense for slots in the player's own inventory.
        // Hovering over a Creative Palette tab slot produces srcInPlayer=false and there is
        // no "other side" container to QUICK_MOVE into, so exit early.
        if (isCreative && !srcInPlayer) return;

        executeOnMatchingSlots(
                screen, player, im,
                focused.getStack().getItem(),
                /* filterSameSide */ true, srcInPlayer,
                SlotActionType.QUICK_MOVE, 0);
    }

    /** Throw all items of the same type as the hovered slot onto the ground. */
    public static void executeThrowAll(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        ClientPlayerInteractionManager im = client.interactionManager;
        if (player == null || im == null || player.isSpectator()) return;
        if (!(client.currentScreen instanceof HandledScreen<?> screen)) return;

        Slot focused = getFocusedSlot(screen);
        if (focused == null || !focused.hasStack() || !focused.canTakeItems(player)) return;

        executeOnMatchingSlots(
                screen, player, im,
                focused.getStack().getItem(),
                /* filterSameSide */ false, false,
                SlotActionType.THROW, 1);
    }

    // ── Core slot loop ───────────────────────────────────────────────────────

    private static void executeOnMatchingSlots(
            HandledScreen<?> screen,
            ClientPlayerEntity player,
            ClientPlayerInteractionManager im,
            Item targetItem,
            boolean filterSameSide,
            boolean srcInPlayer,
            SlotActionType action,
            int btn) {

        ScreenHandler handler = screen.getScreenHandler();
        boolean isCreative = screen instanceof CreativeInventoryScreen;
        int syncId = isCreative ? player.playerScreenHandler.syncId : handler.syncId;
        List<Slot> slots = handler.slots;
        int size = slots.size();

        for (int i = 0; i < size; i++) {
            Slot slot = slots.get(i);
            if (slot.inventory instanceof CraftingResultInventory) continue;
            if (!slot.canTakeItems(player)) continue;

            // Performance: avoid reflection for non-Creative branches.
            // In Creative Mode, unwrap CreativeSlot only for PlayerInventory slots.
            // Non-PlayerInventory Creative Palette slots are skipped before reflection is invoked.
            Slot realSlot;
            if (isCreative) {
                realSlot = getRealSlot(slot);
                if (realSlot == null) continue;
                // Skip template palette slots – these are infinite sources that must NOT be clicked.
                if (!(realSlot.inventory instanceof PlayerInventory)) continue;
            } else {
                realSlot = slot;
            }

            if (filterSameSide && ((realSlot.inventory instanceof PlayerInventory) != srcInPlayer)) {
                continue;
            }

            ItemStack stack = slot.getStack();
            if (!stack.isEmpty() && stack.isOf(targetItem)) {
                if (isCreative) {
                    if (action == SlotActionType.THROW) {
                        im.dropCreativeStack(stack.copy());
                        slot.setStack(ItemStack.EMPTY);
                        im.clickCreativeStack(ItemStack.EMPTY, realSlot.id);
                    } else {
                        im.clickSlot(syncId, realSlot.id, btn, action, player);
                        slot.setStack(ItemStack.EMPTY);
                        im.clickCreativeStack(ItemStack.EMPTY, realSlot.id);
                    }
                } else {
                    im.clickSlot(syncId, realSlot.id, btn, action, player);
                }
            }
        }
    }

    // ── Focused-slot accessor ────────────────────────────────────────────────

    private static Slot getFocusedSlot(HandledScreen<?> screen) {
        if (FOCUSED_SLOT_HANDLE == null) return null;
        try {
            return (Slot) FOCUSED_SLOT_HANDLE.invoke(screen);
        } catch (Throwable t) {
            return null;
        }
    }
}
