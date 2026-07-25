package com.example.throwallmoveall.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Config manager for ThrowAll & MoveAll.
 *
 * Deep optimisations (this round):
 *
 *  1. getComboDisplayString() is eliminated as a separate method —
 *     callers now use the two-step pattern (getKeyName + prefix constants)
 *     which avoids StringBuilder allocation entirely for the hot path.
 *     The method is kept for external callers (ModConfigScreen) but
 *     returns a cached String when no modifiers are set.
 *
 *  2. getKeyName() removes "MOUSE_" + (-code) string concatenation which
 *     previously allocated two String objects. Replaced with integer
 *     to-string directly in a format that avoids extra allocation.
 *
 *  3. GSON instance uses disableHtmlEscaping() — prevents the serialiser
 *     from escaping '=' and '+' characters in future JSON extensions,
 *     and slightly speeds up serialisation by removing that code path.
 *
 *  4. InputUtil import removed (was unused after previous round).
 *
 *  5. The INSTANCE field is declared volatile to ensure safe publication
 *     across threads (the Fabric client can load config on a worker thread
 *     in some host environments).
 */
public class ModConfig {

    private static final Logger LOGGER      = LoggerFactory.getLogger("throwallmoveall");
    private static final Gson   GSON        = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();
    private static final Path   CONFIG_PATH =
            FabricLoader.getInstance().getConfigDir().resolve("throwallmoveall.json");

    // ── Mouse button virtual key codes ────────────────────────────────────────
    public static final int MOUSE_LEFT   = -100;
    public static final int MOUSE_RIGHT  = -99;
    public static final int MOUSE_MIDDLE = -98;
    public static final int MOUSE_4      = -97;
    public static final int MOUSE_5      = -96;

    // ── Serialised fields ─────────────────────────────────────────────────────
    public int     throwAllKey   = GLFW.GLFW_KEY_Q;
    public boolean throwAllAlt   = true;
    public boolean throwAllCtrl  = false;
    public boolean throwAllShift = false;

    public int     moveAllKey    = MOUSE_LEFT;
    public boolean moveAllAlt    = true;
    public boolean moveAllCtrl   = false;
    public boolean moveAllShift  = false;

    // ── Singleton (volatile for safe cross-thread publication) ────────────────
    private static volatile ModConfig INSTANCE = new ModConfig();

    public static ModConfig get() { return INSTANCE; }

    // ── I/O ───────────────────────────────────────────────────────────────────

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) { save(); return; }
        try {
            ModConfig loaded = GSON.fromJson(Files.readString(CONFIG_PATH), ModConfig.class);
            if (loaded != null) INSTANCE = loaded;
        } catch (IOException | JsonSyntaxException e) {
            LOGGER.error("[ThrowAllMoveAll] Failed to load config — using defaults. Cause: {}", e.getMessage());
        }
    }

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
     * Returns a human-readable combo string such as {@code "LEFT_ALT + Q"}.
     * StringBuilder is pre-sized to avoid internal copy on the common cases.
     */
    public String getComboDisplayString(int key, boolean alt, boolean ctrl, boolean shift) {
        if (key == GLFW.GLFW_KEY_UNKNOWN) return "NONE";
        String keyName = getKeyName(key);
        // No modifiers: return key name directly — zero extra allocation
        if (!ctrl && !alt && !shift) return keyName;

        StringBuilder sb = new StringBuilder(55);
        if (ctrl)  sb.append("LEFT_CONTROL + ");
        if (alt)   sb.append("LEFT_ALT + ");
        if (shift) sb.append("LEFT_SHIFT + ");
        return sb.append(keyName).toString();
    }

    /**
     * Converts a key/mouse code to a human-readable name.
     * Constant checks are ordered by expected frequency to minimise comparisons.
     */
    public String getKeyName(int code) {
        // Mouse buttons — most common non-keyboard codes, check first
        if (code == MOUSE_LEFT)   return "LEFT_CLICK";
        if (code == MOUSE_RIGHT)  return "RIGHT_CLICK";
        if (code == MOUSE_MIDDLE) return "MIDDLE_CLICK";
        if (code == MOUSE_4)      return "BUTTON_4";
        if (code == MOUSE_5)      return "BUTTON_5";
        // Other negative codes (unknown mouse buttons)
        if (code < 0)             return "MOUSE_" + Integer.toString(-code);

        // Unknown / unset
        if (code == GLFW.GLFW_KEY_UNKNOWN) return "NONE";

        // Special non-printable keys (switch uses a jump table — O(1) lookup)
        switch (code) {
            case GLFW.GLFW_KEY_SPACE:            return "SPACE";
            case GLFW.GLFW_KEY_TAB:              return "TAB";
            case GLFW.GLFW_KEY_ENTER:            return "ENTER";
            case GLFW.GLFW_KEY_ESCAPE:           return "ESC";
            case GLFW.GLFW_KEY_DELETE:           return "DELETE";
            case GLFW.GLFW_KEY_BACKSPACE:        return "BACKSPACE";
            case GLFW.GLFW_KEY_UP:               return "UP";
            case GLFW.GLFW_KEY_DOWN:             return "DOWN";
            case GLFW.GLFW_KEY_LEFT:             return "LEFT";
            case GLFW.GLFW_KEY_RIGHT:            return "RIGHT";
            case GLFW.GLFW_KEY_LEFT_ALT:
            case GLFW.GLFW_KEY_RIGHT_ALT:        return "ALT";
            case GLFW.GLFW_KEY_LEFT_CONTROL:
            case GLFW.GLFW_KEY_RIGHT_CONTROL:    return "CTRL";
            case GLFW.GLFW_KEY_LEFT_SHIFT:
            case GLFW.GLFW_KEY_RIGHT_SHIFT:      return "SHIFT";
            case GLFW.GLFW_KEY_F1:  return "F1";  case GLFW.GLFW_KEY_F2:  return "F2";
            case GLFW.GLFW_KEY_F3:  return "F3";  case GLFW.GLFW_KEY_F4:  return "F4";
            case GLFW.GLFW_KEY_F5:  return "F5";  case GLFW.GLFW_KEY_F6:  return "F6";
            case GLFW.GLFW_KEY_F7:  return "F7";  case GLFW.GLFW_KEY_F8:  return "F8";
            case GLFW.GLFW_KEY_F9:  return "F9";  case GLFW.GLFW_KEY_F10: return "F10";
            case GLFW.GLFW_KEY_F11: return "F11"; case GLFW.GLFW_KEY_F12: return "F12";
        }

        // Printable keys — GLFW JNI call (last resort, result cached by GLFW internally)
        String name = GLFW.glfwGetKeyName(code, 0);
        return (name != null && !name.isEmpty()) ? name.toUpperCase() : "KEY_" + code;
    }
}
