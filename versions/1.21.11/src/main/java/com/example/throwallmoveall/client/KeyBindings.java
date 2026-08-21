package com.example.throwallmoveall.client;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final KeyBinding.Category KEY_CATEGORY = KeyBinding.Category.create(
            Identifier.of("throwallmoveall", "title")
    );

    public static KeyBinding throwAllKeyBinding;
    public static KeyBinding moveAllKeyBinding;

    public static void register() {
        throwAllKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.throwallmoveall.throw_all",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                KEY_CATEGORY
        ));

        moveAllKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.throwallmoveall.move_all",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_X,
                KEY_CATEGORY
        ));
    }
}
