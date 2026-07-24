package com.example.throwallmoveall.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;

/**
 * Modern Item-Scroller style Config Manager.
 * Supports full key combinations (e.g. LEFT_ALT + Q, LEFT_SHIFT + LEFT_CLICK, BUTTON_3).
 */
public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("throwallmoveall.json");

    // Special Mouse Button Keycodes (Negative to avoid GLFW key collision)
    public static final int MOUSE_LEFT = -100;
    public static final int MOUSE_RIGHT = -99;
    public static final int MOUSE_MIDDLE = -98;
    public static final int MOUSE_4 = -97;
    public static final int MOUSE_5 = -96;

    // ThrowAll Shortcut Configuration (Default: V)
    public int throwAllKey = GLFW.GLFW_KEY_V;
    public boolean throwAllAlt = false;
    public boolean throwAllCtrl = false;
    public boolean throwAllShift = false;

    // MoveAll Shortcut Configuration (Default: X)
    public int moveAllKey = GLFW.GLFW_KEY_X;
    public boolean moveAllAlt = false;
    public boolean moveAllCtrl = false;
    public boolean moveAllShift = false;

    private static ModConfig INSTANCE = new ModConfig();

    public static ModConfig get() {
        return INSTANCE;
    }

    public static void load() {
        File configFile = CONFIG_PATH.toFile();
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                ModConfig loaded = GSON.fromJson(reader, ModConfig.class);
                if (loaded != null) {
                    INSTANCE = loaded;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            save();
        }
    }

    public static void save() {
        try {
            File configFile = CONFIG_PATH.toFile();
            configFile.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(configFile)) {
                GSON.toJson(INSTANCE, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns full formatted combo name like "LEFT_ALT + Q", "LEFT_SHIFT + LEFT_CLICK", "NONE".
     */
    public String getComboDisplayString(int key, boolean alt, boolean ctrl, boolean shift) {
        if (key == GLFW.GLFW_KEY_UNKNOWN) return "NONE";

        StringBuilder sb = new StringBuilder();
        if (ctrl) sb.append("LEFT_CONTROL + ");
        if (alt) sb.append("LEFT_ALT + ");
        if (shift) sb.append("LEFT_SHIFT + ");

        sb.append(getKeyName(key));
        return sb.toString();
    }

    /**
     * Converts key or mouse code to human-readable string.
     */
    public String getKeyName(int keyCode) {
        if (keyCode == GLFW.GLFW_KEY_UNKNOWN) return "NONE";
        if (keyCode == MOUSE_LEFT) return "LEFT_CLICK";
        if (keyCode == MOUSE_RIGHT) return "RIGHT_CLICK";
        if (keyCode == MOUSE_MIDDLE) return "MIDDLE_CLICK";
        if (keyCode == MOUSE_4) return "BUTTON_4";
        if (keyCode == MOUSE_5) return "BUTTON_5";

        if (keyCode < 0) return "MOUSE_" + Math.abs(keyCode);

        String name = GLFW.glfwGetKeyName(keyCode, 0);
        if (name != null && !name.isEmpty()) {
            return name.toUpperCase();
        }

        switch (keyCode) {
            case GLFW.GLFW_KEY_SPACE: return "SPACE";
            case GLFW.GLFW_KEY_TAB: return "TAB";
            case GLFW.GLFW_KEY_ENTER: return "ENTER";
            case GLFW.GLFW_KEY_ESCAPE: return "ESC";
            case GLFW.GLFW_KEY_DELETE: return "DELETE";
            case GLFW.GLFW_KEY_BACKSPACE: return "BACKSPACE";
            case GLFW.GLFW_KEY_UP: return "UP";
            case GLFW.GLFW_KEY_DOWN: return "DOWN";
            case GLFW.GLFW_KEY_LEFT: return "LEFT";
            case GLFW.GLFW_KEY_RIGHT: return "RIGHT";
            default: return "KEY_" + keyCode;
        }
    }
}
