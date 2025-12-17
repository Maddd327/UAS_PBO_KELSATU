package main;


import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Panel kanan "SHOWCASE":
 * - Preview papan + preview mini bidak (mengikuti pilihan skin)
 * - Selector skin bidak & tema papan
 *
 * // UI ONLY CHANGE
 */
public class ShowcasePanel extends JPanel {

    private static final Color PANEL_BG = new Color(0x1B, 0x20, 0x28);
    private static final Color PANEL_BORDER = new Color(255, 255, 255, 26);

    private static final Color MUTED = new Color(0x95, 0xA0, 0xB2);
    private static final Color TEXT = new Color(0xEA, 0xEE, 0xF5);
    private static final Color SUB = new Color(0xB8, 0xC0, 0xCC);

    public ShowcasePanel() {
        setOpaque(false);
        setLayout(new BorderLayout());

        Card card = new Card();
        card.setLayout(new BorderLayout(14, 14));
        card.setBorder(new EmptyBorder(26, 18, 18, 18));


        // Header
        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("SHOWCASE");
        title.setForeground(MUTED);
        title.setFont(new Font("Segoe UI", Font.BOLD, 12));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(PANEL_BORDER);
        sep.setOpaque(false);

        head.add(title);
        head.add(Box.createVerticalStrut(12));
        head.add(sep);

        card.add(head, BorderLayout.NORTH);

        // Selector + Preview
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        CustomizationPane preview = new CustomizationPane();
        preview.setPreferredSize(new Dimension(420, 380));
        center.add(preview);

        card.add(center, BorderLayout.CENTER);

        // Info singkat
        card.add(buildInfo(), BorderLayout.SOUTH);

        add(card, BorderLayout.CENTER);
    }

    private JComponent buildInfo() {
        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBorder(new EmptyBorder(2, 2, 0, 2));

        JLabel label = new JLabel("Customization");
        label.setForeground(TEXT);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        info.add(label);
        info.add(Box.createVerticalStrut(6));
        info.add(bullet("Skin bidak & tema papan tersimpan dan diterapkan saat game dimulai"));
        info.add(Box.createVerticalStrut(6));
        info.add(bullet("Undo/Redo tersedia: Ctrl+Z / Ctrl+Y + tombol toolbar"));

        return info;
    }

    private JComponent bullet(String text) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JLabel dot = new JLabel("*"); // UI ONLY CHANGE: ASCII safe
        dot.setForeground(new Color(0xC9, 0xA2, 0x27));
        dot.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel t = new JLabel("<html><div style='width:340px;'>" + text + "</div></html>");
        t.setForeground(SUB);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        row.add(dot, BorderLayout.WEST);
        row.add(t, BorderLayout.CENTER);
        return row;
    }

    /** Card rounded + shadow */
    private static class Card extends JPanel {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int arc = 22;
            int w = getWidth();
            int h = getHeight();

            g2.setColor(new Color(0, 0, 0, 60));
            g2.fillRoundRect(6, 7, w - 10, h - 10, arc, arc);

            g2.setColor(PANEL_BG);
            g2.fillRoundRect(0, 0, w - 10, h - 10, arc, arc);

            g2.setColor(PANEL_BORDER);
            g2.drawRoundRect(0, 0, w - 10, h - 10, arc, arc);

            g2.dispose();
            super.paintComponent(g);
        }

        @Override public Insets getInsets() {
            return new Insets(0, 0, 10, 10);
        }

        @Override public boolean isOpaque() { return false; }
    }

    /** Pane selector + preview */
    private static class CustomizationPane extends JPanel {
        private final MiniBoardPreview boardPreview = new MiniBoardPreview();
        private final PiecePreview piecePreview = new PiecePreview();

        CustomizationPane() {
            setOpaque(false);
            setLayout(new BorderLayout(12, 12));

            JPanel controls = new JPanel(new GridBagLayout());
            controls.setOpaque(false);

            GridBagConstraints gc = new GridBagConstraints();
            gc.gridx = 0;
            gc.gridy = 0;
            gc.insets = new Insets(0, 0, 8, 10);
            gc.anchor = GridBagConstraints.WEST;

            JLabel skinLbl = new JLabel("Skin Bidak");
            skinLbl.setForeground(new Color(0xEA, 0xEE, 0xF5));
            skinLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            controls.add(skinLbl, gc);

            gc.gridx = 1;
            gc.fill = GridBagConstraints.HORIZONTAL;
            gc.weightx = 1;
            JComboBox<String> skinBox = new JComboBox<>(UIConfig.getSkinDirs().keySet().toArray(new String[0]));
            skinBox.setSelectedItem(UIConfig.getSelectedSkinKey());
            skinBox.addActionListener(e -> {
                UIConfig.setSelectedSkinKey((String) skinBox.getSelectedItem());
                // UI ONLY CHANGE
                piecePreview.refresh();
                repaint();
            });
            controls.add(skinBox, gc);

            gc.gridx = 0;
            gc.gridy = 1;
            gc.weightx = 0;
            gc.fill = GridBagConstraints.NONE;
            JLabel themeLbl = new JLabel("Tema Papan");
            themeLbl.setForeground(new Color(0xEA, 0xEE, 0xF5));
            themeLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            controls.add(themeLbl, gc);

            gc.gridx = 1;
            gc.fill = GridBagConstraints.HORIZONTAL;
            gc.weightx = 1;
            JComboBox<String> themeBox = new JComboBox<>(UIConfig.getThemes().keySet().toArray(new String[0]));
            themeBox.setSelectedItem(UIConfig.getSelectedThemeKey());
            themeBox.addActionListener(e -> {
                UIConfig.setSelectedThemeKey((String) themeBox.getSelectedItem());
                // UI ONLY CHANGE
                boardPreview.repaint();
                repaint();
            });
            controls.add(themeBox, gc);

            add(controls, BorderLayout.NORTH);

            JPanel previews = new JPanel(new GridBagLayout());
            previews.setOpaque(false);
            GridBagConstraints pc = new GridBagConstraints();
            pc.gridx = 0;
            pc.gridy = 0;
            pc.insets = new Insets(0, 0, 0, 12);
            previews.add(boardPreview, pc);

            pc.gridx = 1;
            pc.insets = new Insets(0, 0, 0, 0);
            previews.add(piecePreview, pc);

            add(previews, BorderLayout.CENTER);
        }
    }

    /** Preview papan mini mengikuti theme */
    private static class MiniBoardPreview extends JComponent {
        MiniBoardPreview() { setPreferredSize(new Dimension(260, 260)); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int pad = 8;
            int size = Math.min(getWidth(), getHeight()) - pad * 2;
            int x0 = (getWidth() - size) / 2;
            int y0 = (getHeight() - size) / 2;

            UIConfig.BoardTheme theme = UIConfig.getTheme();

            g2.setColor(new Color(0,0,0,70));
            g2.fillRoundRect(x0 + 6, y0 + 8, size, size, 18, 18);

            g2.setColor(new Color(255,255,255,26));
            g2.drawRoundRect(x0, y0, size, size, 18, 18);

            int cell = size / 8;
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    g2.setColor(((r + c) & 1) == 0 ? theme.light : theme.dark);
                    g2.fillRect(x0 + c * cell, y0 + r * cell, cell, cell);
                }
            }

            g2.setColor(theme.outline);
            g2.drawRect(x0, y0, cell * 8 - 1, cell * 8 - 1);

            g2.dispose();
        }
    }

    /** Preview mini bidak (putih & hitam) dari skin aktif */
    private static class PiecePreview extends JComponent {
        private javax.swing.ImageIcon wIcon;
        private javax.swing.ImageIcon bIcon;

        PiecePreview() {
            setPreferredSize(new Dimension(130, 260));
            refresh();
        }

        void refresh() {
            // UI ONLY CHANGE: preview mengambil file PNG dari skin aktif
            String dir = UIConfig.getSelectedSkinDir();
            wIcon = new javax.swing.ImageIcon(dir + "/W_queen.png");
            bIcon = new javax.swing.ImageIcon(dir + "/B_queen.png");
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            g2.setColor(new Color(255,255,255,18));
            g2.fillRoundRect(0, 0, w, h, 18, 18);
            g2.setColor(new Color(255,255,255,26));
            g2.drawRoundRect(0, 0, w-1, h-1, 18, 18);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2.setColor(new Color(0xEA, 0xEE, 0xF5));
            g2.drawString("Preview", 16, 22);

            int size = 86;
            int x = (w - size) / 2;

            if (wIcon != null && wIcon.getImage() != null) {
                g2.drawImage(wIcon.getImage(), x, 40, size, size, null);
            }
            if (bIcon != null && bIcon.getImage() != null) {
                g2.drawImage(bIcon.getImage(), x, 140, size, size, null);
            }

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.setColor(new Color(0xB8, 0xC0, 0xCC));
            g2.drawString("White", 18, 134);
            g2.drawString("Black", 18, 234);

            g2.dispose();
        }
    }
}

