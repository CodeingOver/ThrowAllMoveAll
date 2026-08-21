package com.example.throwallmoveall.client;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * Quản lý việc đăng ký và lưu trữ các phím tắt (KeyBindings) cho mod.
 */
public class KeyBindings {
    public static final String KEY_CATEGORY = "category.throwallmoveall.title";

    public static KeyBinding throwAllKeyBinding;
    public static KeyBinding moveAllKeyBinding;

    public static void register() {
        // Hotkey vứt toàn bộ vật phẩm (ThrowAll) - Mặc định phím V
        throwAllKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.throwallmoveall.throw_all",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                KEY_CATEGORY
        ));

        // Hotkey di chuyển toàn bộ vật phẩm (MoveAll) - Mặc định phím X
        moveAllKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.throwallmoveall.move_all",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_X,
                KEY_CATEGORY
        ));
    }
}
