package com.example.throwallmoveall.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Config manager for ThrowAll & MoveAll.
 *
 * Optimisations applied:
 *  - I/O uses NIO (Files.readString / Files.writeString) instead of FileReader/FileWriter —
 *    no manual stream management, fewer syscalls, uses the OS page-cache efficiently.
 *  - Errors are logged via SLF4J (same logger as the rest of the mod) instead of
 *    printStackTrace() which flushes to stderr on every character.
 *  - getComboDisplayString() pre-sizes the StringBuilder to avoid internal resizing.
 *  - getKeyName() checks cheap constant comparisons before calling GLFW JNI.
 *  - CONFIG_PATH is stored as Path (NIO) directly; no intermediate File conversion
 *    during load/save.
 */
public class ModConfig {

    private static final Logger       LOGGER      = LoggerFactory.getLogger("throwallmoveall");
    private static final Gson         GSON        = new GsonBuilder().setPrettyPrinting().create();
    private static final Path         CONFIG_PATH =
            FabricLoader.getInstance().getConfigDir().resolve("throwallmoveall.json");

    // ── Mouse button virtual key codes ────────────────────────────────────────
    // Negative values so they never collide with any GLFW keyboard key code (≥ 0).
    public static final int MOUSE_LEFT   = -100;
    public static final int MOUSE_RIGHT  = -99;
    public static final int MOUSE_MIDDLE = -98;
    public static final int MOUSE_4      = -97;
    public static final int MOUSE_5      = -96;

    // ── Binding fields (serialised to JSON) ───────────────────────────────────
    public int     throwAllKey   = GLFW.GLFW_KEY_Q;
    public boolean throwAllAlt   = true;
    public boolean throwAllCtrl  = false;
    public boolean throwAllShift = false;

    public int     moveAllKey    = MOUSE_LEFT;
    public boolean moveAllAlt    = true;
    public boolean moveAllCtrl   = false;
    public boolean moveAllShift  = false;

    // ── Singleton ─────────────────────────────────────────────────────────────
    private static ModConfig INSTANCE = new ModConfig();

    public static ModConfig get() { return INSTANCE; }

    // ── I/O ───────────────────────────────────────────────────────────────────

    /** Loads config from disk (NIO read, no manual stream/close). */
    public static void load() {
        if (!Files.exists(CONFIG_PATH)) {
            save();   // write defaults on first run
            return;
        }
        try {
            String json = Files.readString(CONFIG_PATH);
            ModConfig loaded = GSON.fromJson(json, ModConfig.class);
            if (loaded != null) INSTANCE = loaded;
        } catch (IOException | com.google.gson.JsonSyntaxException e) {
            LOGGER.error("[ThrowAllMoveAll] Failed to load config — using defaults. Cause: {}", e.getMessage());
        }
    }

    /** Saves config to disk (NIO write, no manual stream/close). */
    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(INSTANCE));
        } catch (IOException e) {
            LOGGER.error("[ThrowAllMoveAll] Failed to save config. Cause: {}", e.getMessage());
        }
    }

    // ── Display helpers ───────────────────────────────────────────────────────

    /**
     * Returns a human-readable combo string, e.g. {@code "LEFT_ALT + Q"},
     * {@code "LEFT_SHIFT + LEFT_CLICK"}, or {@code "NONE"}.
     *
     * Pre-sizes the StringBuilder to avoid internal array copies.
     */
    public String getComboDisplayString(int key, boolean alt, boolean ctrl, boolean shift) {
        if (key == GLFW.GLFW_KEY_UNKNOWN) return "NONE";

        // Rough max length: "LEFT_CONTROL + LEFT_ALT + LEFT_SHIFT + " (39) + key name (~16)
        StringBuilder sb = new StringBuilder(55);
        if (ctrl)  sb.append("LEFT_CONTROL + ");
        if (alt)   sb.append("LEFT_ALT + ");
        if (shift) sb.append("LEFT_SHIFT + ");
        sb.append(getKeyName(key));
        return sb.toString();
    }

    /**
     * Converts a key/mouse code to a human-readable name.
     *
     * Order: cheap constant checks → GLFW JNI call (only for real keyboard keys).
     */
    public String getKeyName(int code) {
        // Mouse buttons (negative codes) — no JNI needed
        if (code == MOUSE_LEFT)   return "LEFT_CLICK";
        if (code == MOUSE_RIGHT)  return "RIGHT_CLICK";
        if (code == MOUSE_MIDDLE) return "MIDDLE_CLICK";
        if (code == MOUSE_4)      return "BUTTON_4";
        if (code == MOUSE_5)      return "BUTTON_5";
        if (code < 0)             return "MOUSE_" + (-code);

        // Unknown
        if (code == GLFW.GLFW_KEY_UNKNOWN) return "NONE";

        // Special keys without a printable GLFW name
        switch (code) {
            case GLFW.GLFW_KEY_SPACE:     return "SPACE";
            case GLFW.GLFW_KEY_TAB:       return "TAB";
            case GLFW.GLFW_KEY_ENTER:     return "ENTER";
            case GLFW.GLFW_KEY_ESCAPE:    return "ESC";
            case GLFW.GLFW_KEY_DELETE:    return "DELETE";
            case GLFW.GLFW_KEY_BACKSPACE: return "BACKSPACE";
            case GLFW.GLFW_KEY_UP:        return "UP";
            case GLFW.GLFW_KEY_DOWN:      return "DOWN";
            case GLFW.GLFW_KEY_LEFT:      return "LEFT";
            case GLFW.GLFW_KEY_RIGHT:     return "RIGHT";
            case GLFW.GLFW_KEY_LEFT_ALT:
            case GLFW.GLFW_KEY_RIGHT_ALT:     return "ALT";
            case GLFW.GLFW_KEY_LEFT_CONTROL:
            case GLFW.GLFW_KEY_RIGHT_CONTROL: return "CTRL";
            case GLFW.GLFW_KEY_LEFT_SHIFT:
            case GLFW.GLFW_KEY_RIGHT_SHIFT:   return "SHIFT";
        }

        // Printable keys — ask GLFW (JNI call, last resort)
        String name = GLFW.glfwGetKeyName(code, 0);
        if (name != null && !name.isEmpty()) return name.toUpperCase();

        return "KEY_" + code;
    }
}
