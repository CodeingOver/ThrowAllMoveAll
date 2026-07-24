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
 * Quản lý cấu hình tệp JSON độc lập (.minecraft/config/throwallmoveall.json).
 * Hỗ trợ các phím chính và phím bổ trợ Modifier (Alt, Ctrl, Shift).
 */
public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("throwallmoveall.json");

    // Phím tắt cho ThrowAll (Mặc định: Phím V, không bật modifier)
    public int throwAllKey = GLFW.GLFW_KEY_V;
    public boolean throwAllAlt = false;
    public boolean throwAllCtrl = false;
    public boolean throwAllShift = false;

    // Phím tắt cho MoveAll (Mặc định: Phím X, không bật modifier)
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

    public String getKeyName(int keyCode) {
        if (keyCode == GLFW.GLFW_KEY_UNKNOWN) return "NONE";
        String name = GLFW.glfwGetKeyName(keyCode, 0);
        if (name != null && !name.isEmpty()) {
            return name.toUpperCase();
        }
        // Trường hợp các phím đặc biệt
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
