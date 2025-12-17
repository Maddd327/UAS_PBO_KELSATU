package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

/**
 * GameUI: JFrame untuk layar permainan.
 * Berisi toolbar Undo/Redo + info turn, dan PanelGame sebagai canvas utama.
 */
public class GameUI extends JFrame {

    private final PanelGame panelGame;

    public GameUI(boolean playerIsWhite) {
        super("Catur UMG - Play");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            javax.swing.ImageIcon icon = new javax.swing.ImageIcon("asset/chess.png");
            setIconImage(icon.getImage());
        } catch (Exception ignored) {}

        this.panelGame = new PanelGame(playerIsWhite);

        setLayout(new BorderLayout());
        add(buildToolbar(), BorderLayout.NORTH);
        add(panelGame, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        panelGame.launchGame();
    }

    private JToolBar buildToolbar() {
        UIConfig.BoardTheme theme = UIConfig.getTheme();

        JToolBar bar = new JToolBar();
        bar.setFloatable(false);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(255, 255, 255, 30)));
        bar.setBackground(theme.sidebar);

        // Left group (Undo / Redo)
        JButton undo = makeToolButton("Undo", "Ctrl+Z");
        JButton redo = makeToolButton("Redo", "Ctrl+Y");

        undo.addActionListener(e -> panelGame.undoMoveUI());
        redo.addActionListener(e -> panelGame.redoMoveUI());

        bar.add(Box.createHorizontalStrut(10));
        bar.add(undo);
        bar.add(Box.createHorizontalStrut(8));
        bar.add(redo);

        bar.add(Box.createHorizontalGlue());

        // Right group (info singkat)
        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.X_AXIS));

        JLabel t = new JLabel("Turn: lihat indikator di sidebar");
        t.setForeground(new Color(240, 240, 245));
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        info.add(t);
        info.add(Box.createHorizontalStrut(12));

        bar.add(info);
        bar.add(Box.createHorizontalStrut(10));

        return bar;
    }

    private JButton makeToolButton(String text, String shortcut) {
        UIConfig.BoardTheme theme = UIConfig.getTheme();
        JButton b = new JButton("" + text + "  (" + shortcut + ")");
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        b.setMargin(new Insets(0, 0, 0, 0));
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(theme.accent);
        b.setForeground(new Color(0x10, 0x12, 0x16));
        b.setOpaque(true);
        b.setPreferredSize(new Dimension(160, 34));
        return b;
    }
}
