package com.example.throwallmoveall.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Client-side inventory action executor for ThrowAll & MoveAll (Minecraft 26.x).
 */
public class InventoryHelper {

    // ── MethodHandle cache for AbstractContainerScreen.hoveredSlot ───────────
    private static final MethodHandle HOVERED_SLOT_HANDLE = resolveHoveredSlotHandle();

    private static MethodHandle resolveHoveredSlotHandle() {
        try {
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(
                    AbstractContainerScreen.class, MethodHandles.lookup());
            for (Field f : AbstractContainerScreen.class.getDeclaredFields()) {
                if (f.getType() == Slot.class) {
                    return lookup.unreflectGetter(f);
                }
            }
        } catch (Throwable t) {
        }
        return null;
    }

    // ── MethodHandle cache for CreativeModeInventoryScreen.SlotWrapper.target ─
    private static final MethodHandle CREATIVE_SLOT_HANDLE = resolveCreativeSlotHandle();

    private static MethodHandle resolveCreativeSlotHandle() {
        try {
            for (Class<?> inner : CreativeModeInventoryScreen.class.getDeclaredClasses()) {
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
    public static void executeMoveAll(Minecraft client) {
        LocalPlayer player = client.player;
        MultiPlayerGameMode im = client.gameMode;
        if (player == null || im == null || player.isSpectator()) return;
        if (!(client.gui.screen() instanceof AbstractContainerScreen<?> screen)) return;

        Slot focused = getFocusedSlot(screen);
        if (focused == null || !focused.hasItem() || !focused.mayPickup(player)) return;

        boolean isCreative = screen instanceof CreativeModeInventoryScreen;
        Slot realFocused = isCreative ? getRealSlot(focused) : focused;
        boolean srcInPlayer = realFocused.container instanceof Inventory;

        if (isCreative && !srcInPlayer) return;

        executeOnMatchingSlots(
                screen, player, im,
                focused.getItem().getItem(),
                /* filterSameSide */ true, srcInPlayer,
                ContainerInput.QUICK_MOVE, 0);
    }

    /** Throw all items of the same type as the hovered slot onto the ground. */
    public static void executeThrowAll(Minecraft client) {
        LocalPlayer player = client.player;
        MultiPlayerGameMode im = client.gameMode;
        if (player == null || im == null || player.isSpectator()) return;
        if (!(client.gui.screen() instanceof AbstractContainerScreen<?> screen)) return;

        Slot focused = getFocusedSlot(screen);
        if (focused == null || !focused.hasItem() || !focused.mayPickup(player)) return;

        executeOnMatchingSlots(
                screen, player, im,
                focused.getItem().getItem(),
                /* filterSameSide */ false, false,
                ContainerInput.THROW, 1);
    }

    // ── Core slot loop ───────────────────────────────────────────────────────

    private static void executeOnMatchingSlots(
            AbstractContainerScreen<?> screen,
            LocalPlayer player,
            MultiPlayerGameMode im,
            Item targetItem,
            boolean filterSameSide,
            boolean srcInPlayer,
            ContainerInput action,
            int btn) {

        AbstractContainerMenu handler = screen.getMenu();
        boolean isCreative = screen instanceof CreativeModeInventoryScreen;
        int syncId = isCreative ? player.inventoryMenu.containerId : handler.containerId;
        List<Slot> slots = handler.slots;
        int size = slots.size();

        for (int i = 0; i < size; i++) {
            Slot slot = slots.get(i);
            if (slot.container instanceof ResultContainer) continue;
            if (!slot.mayPickup(player)) continue;

            Slot realSlot;
            if (isCreative) {
                realSlot = getRealSlot(slot);
                if (realSlot == null) continue;
                if (!(realSlot.container instanceof Inventory)) continue;
            } else {
                realSlot = slot;
            }

            if (filterSameSide && ((realSlot.container instanceof Inventory) != srcInPlayer)) {
                continue;
            }

            ItemStack stack = slot.getItem();
            if (!stack.isEmpty() && stack.is(targetItem)) {
                if (isCreative) {
                    if (action == ContainerInput.THROW) {
                        im.handleCreativeModeItemDrop(stack.copy());
                        slot.set(ItemStack.EMPTY);
                        im.handleCreativeModeItemAdd(ItemStack.EMPTY, realSlot.index);
                    } else {
                        im.handleContainerInput(syncId, realSlot.index, btn, action, player);
                        slot.set(ItemStack.EMPTY);
                        im.handleCreativeModeItemAdd(ItemStack.EMPTY, realSlot.index);
                    }
                } else {
                    im.handleContainerInput(syncId, realSlot.index, btn, action, player);
                }
            }
        }
    }

    // ── Focused-slot accessor ────────────────────────────────────────────────

    private static Slot getFocusedSlot(AbstractContainerScreen<?> screen) {
        if (HOVERED_SLOT_HANDLE == null) return null;
        try {
            return (Slot) HOVERED_SLOT_HANDLE.invoke(screen);
        } catch (Throwable t) {
            return null;
        }
    }
}
