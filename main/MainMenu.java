package main;

import javax.swing.SwingUtilities;

/**
 * Backward compatible entry: dulu bernama MainMenu.
 * Sekarang UI utama ada di MainMenuUI.
 *
 * // UI ONLY CHANGE
 */
public class MainMenu {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainMenuUI::new);
    }
}
