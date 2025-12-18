package main;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * UIConfig menyimpan pilihan UI (skin bidak & tema papan) yang dipilih user di Main Menu.
 *
 * // UI ONLY CHANGE
 */
public final class UIConfig {

    private UIConfig() {}

    // =====================
    //   Piece Skin (Path)
    // =====================
    private static String selectedSkinKey = "Classic";

    // Base dir untuk setiap skin (isi file PNG/JPG mengikuti penamaan existing: W_pawn.png, B_king.png, dst)
    private static final Map<String, String> SKIN_DIRS = new LinkedHashMap<>();
    static {
        // Default (classic) tetap kompatibel dengan path lama asset/img/...
        SKIN_DIRS.put("Classic", "asset/skins/classic");
        SKIN_DIRS.put("Modern", "asset/skins/modern");
        SKIN_DIRS.put("Flat", "asset/skins/flat");
    }

    // =====================
    //    Board Theme
    // =====================
    private static String selectedThemeKey = "Classic Wood";

    public static final class BoardTheme {
        public final Color light;
        public final Color dark;
        public final Color outline;
        public final Color sidebar;
        public final Color accent;

        public BoardTheme(Color light, Color dark, Color outline, Color sidebar, Color accent) {
            this.light = light;
            this.dark = dark;
            this.outline = outline;
            this.sidebar = sidebar;
            this.accent = accent;
        }
    }

    private static final Map<String, BoardTheme> THEMES = new LinkedHashMap<>();
    static {
        THEMES.put("Classic Wood", new BoardTheme(
                new Color(210, 165, 125),
                new Color(175, 115, 70),
                new Color(80, 50, 30),
                new Color(0x18, 0x1B, 0x21),
                new Color(0xC9, 0xA2, 0x27)
        ));
        THEMES.put("Dark", new BoardTheme(
                new Color(0x2B, 0x2F, 0x36),
                new Color(0x18, 0x1B, 0x21),
                new Color(255, 255, 255, 50),
                new Color(0x18, 0x1B, 0x21),
                new Color(0xC9, 0xA2, 0x27)
        ));
        THEMES.put("Marble", new BoardTheme(
                new Color(0xE7, 0xE7, 0xEA),
                new Color(0xA5, 0xA8, 0xAF),
                new Color(0x4A, 0x4F, 0x59),
                new Color(0x18, 0x1B, 0x21),
                new Color(0x2F, 0x6F, 0xB3)
        ));
        THEMES.put("Modern Flat", new BoardTheme(
                new Color(0xD9, 0xDE, 0xE5),
                new Color(0x55, 0x61, 0x6F),
                new Color(0x23, 0x28, 0x31),
                new Color(0x18, 0x1B, 0x21),
                new Color(0xC9, 0xA2, 0x27)
        ));
    }

    // =====================
    //       Public API
    // =====================
    public static Map<String, String> getSkinDirs() {
        return SKIN_DIRS;
    }

    public static void setSelectedSkinKey(String key) {
        if (key != null && SKIN_DIRS.containsKey(key)) selectedSkinKey = key;
    }

    public static String getSelectedSkinKey() {
        return selectedSkinKey;
    }

    public static String getSelectedSkinDir() {
        return SKIN_DIRS.getOrDefault(selectedSkinKey, "asset/skins/classic");
    }

    public static Map<String, BoardTheme> getThemes() {
        return THEMES;
    }

    public static void setSelectedThemeKey(String key) {
        if (key != null && THEMES.containsKey(key)) selectedThemeKey = key;
    }

    public static String getSelectedThemeKey() {
        return selectedThemeKey;
    }

    public static BoardTheme getTheme() {
        return THEMES.getOrDefault(selectedThemeKey, THEMES.get("Classic Wood"));
    }
}
