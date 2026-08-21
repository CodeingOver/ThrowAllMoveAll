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
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Client-side inventory action executor for ThrowAll & MoveAll (Minecraft 1.20.4).
 * Uses native PlayerScreenHandler packet dispatching to bypass Creative drop rate limits.
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

        boolean isCreative = screen instanceof CreativeInventoryScreen;

        // ── Special handling for Creative Mode ────────────────────────────────
        if (isCreative) {
            PlayerScreenHandler playerHandler = player.playerScreenHandler;
            List<Slot> pSlots = playerHandler.slots;
            int pSize = pSlots.size();

            for (int i = 0; i < pSize; i++) {
                Slot slot = pSlots.get(i);
                if (slot.inventory instanceof CraftingResultInventory) continue;
                if (!(slot.inventory instanceof PlayerInventory)) continue;

                ItemStack stack = slot.getStack();
                if (!stack.isEmpty() && stack.isOf(targetItem)) {
                    im.clickSlot(playerHandler.syncId, slot.id, btn, action, player);
                }
            }

            // Sync visual slots on current screen
            for (Slot s : screen.getScreenHandler().slots) {
                if (s.hasStack() && s.getStack().isOf(targetItem)) {
                    Slot real = getRealSlot(s);
                    if (real != null && real.inventory instanceof PlayerInventory) {
                        s.setStack(ItemStack.EMPTY);
                    }
                }
            }
            player.playerScreenHandler.sendContentUpdates();
            return;
        }

        // ── Standard handling for Survival Mode & Containers ─────────────────
        ScreenHandler handler = screen.getScreenHandler();
        int syncId = handler.syncId;
        List<Slot> slots = handler.slots;
        int size = slots.size();

        for (int i = 0; i < size; i++) {
            Slot slot = slots.get(i);
            if (slot.inventory instanceof CraftingResultInventory) continue;
            if (!slot.canTakeItems(player)) continue;

            if (filterSameSide && ((slot.inventory instanceof PlayerInventory) != srcInPlayer)) {
                continue;
            }

            ItemStack stack = slot.getStack();
            if (!stack.isEmpty() && stack.isOf(targetItem)) {
                im.clickSlot(syncId, slot.id, btn, action, player);
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
