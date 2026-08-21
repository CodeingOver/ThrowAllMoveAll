package com.example.throwallmoveall.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final KeyMapping.Category KEY_CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath("throwallmoveall", "title")
    );

    public static KeyMapping throwAllKeyBinding;
    public static KeyMapping moveAllKeyBinding;

    public static void register() {
        throwAllKeyBinding = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.throwallmoveall.throw_all",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                KEY_CATEGORY
        ));

        moveAllKeyBinding = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.throwallmoveall.move_all",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_X,
                KEY_CATEGORY
        ));
    }
}
