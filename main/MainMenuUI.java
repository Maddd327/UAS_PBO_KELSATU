package main;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * MainMenuUI: Main menu modern dengan opsi memilih skin bidak & tema papan
 */
public class MainMenuUI extends JFrame {

    private static final Color APP_BG = new Color(0xF3F5F8);

    public MainMenuUI() {
        super("Catur UMG");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 640));
        setLocationRelativeTo(null);

        try {
            ImageIcon icon = new ImageIcon("asset/chess.png");
            setIconImage(icon.getImage());
        } catch (Exception ignored) {}

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(APP_BG);
        root.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Header
        root.add(new HeaderPanel(), BorderLayout.NORTH);

        // Center 2 columns
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(16, 0, 10, 0));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridy = 0;
        gc.fill = GridBagConstraints.BOTH;
        gc.weighty = 1.0;

        // Left Menu
        gc.gridx = 0;
        gc.weightx = 0.44;
        gc.insets = new Insets(0, 0, 0, 14);

        MenuPanel menu = new MenuPanel(new MenuPanel.MenuActions() {
            @Override public void onPlay() { openColorSelectionAndStart(); }
            @Override public void onMultiplayerComingSoon() { showComingSoon(); }
            @Override public void onSettings() { openSettingsDialog(); }
            @Override public void onHelp() { openHelpDialog(); }
            @Override public void onExit() { confirmExit(); }
        });

        center.add(menu, gc);

        // Right Customization / Preview
        gc.gridx = 1;
        gc.weightx = 0.56;
        gc.insets = new Insets(0, 0, 0, 0);
        center.add(new ShowcasePanel(), gc);

        root.add(center, BorderLayout.CENTER);

        JLabel tip = new JLabel("Tip: saat bermain gunakan Ctrl+Z (Undo) dan Ctrl+Y (Redo)");
        tip.setBorder(new EmptyBorder(6, 6, 0, 6));
        tip.setForeground(new Color(0x6C, 0x75, 0x85));
        tip.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        root.add(tip, BorderLayout.SOUTH);

        setContentPane(root);
        setVisible(true);
    }

    // ===== Actions (tetap pakai game logic lama) =====

    private void openColorSelectionAndStart() {
        ColorSelectionDialog dialog = new ColorSelectionDialog(this);
        dialog.setVisible(true);
        Boolean isWhite = dialog.isWhiteSelected();

        if (isWhite != null) {
            dispose();
            startGame(isWhite);
        }
    }

    private void startGame(boolean playerIsWhite) {
        // UI ONLY CHANGE: gunakan GameUI agar ada tombol Undo/Redo
        new GameUI(playerIsWhite);
    }

    private void showComingSoon() {
        JOptionPane.showMessageDialog(
                this,
                // UI ONLY CHANGE: ASCII safe
                "Multiplayer online masih dalam pengembangan.\n\nStatus: COMING SOON (OK)",
                "Coming Soon",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void openSettingsDialog() {
        JOptionPane.showMessageDialog(
                this,
                "Settings masih placeholder.\n(Nanti: timer, audio, hotkeys)",
                "Settings",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void openHelpDialog() {
        JOptionPane.showMessageDialog(
                this,
                "How To Play:\n" +
                        "- Klik bidak untuk memilih\n" +
                        "- Klik petak tujuan untuk bergerak\n" +
                        "- Ctrl+Z: Undo, Ctrl+Y: Redo\n\n" +
                        "Mode Play: 2 pemain lokal (bergantian di 1 perangkat).",
                "Help / How To Play",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void confirmExit() {
        int res = JOptionPane.showConfirmDialog(
                this,
                "Keluar dari game?",
                "Exit",
                JOptionPane.YES_NO_OPTION
        );
        if (res == JOptionPane.YES_OPTION) System.exit(0);
    }
}
