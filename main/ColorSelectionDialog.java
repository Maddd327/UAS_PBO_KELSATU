package main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Dialog pemilihan warna dengan gaya "glass" (semi-transparan).
 * Mengembalikan:
 * - true  = pilih putih
 * - false = pilih hitam
 * - null  = batal / ditutup
 */
public class ColorSelectionDialog extends JDialog {

    private static final Color BG_DIM = new Color(0, 0, 0, 160);
    private static final Color GLASS = new Color(30, 35, 44, 200);
    private static final Color GLASS_2 = new Color(40, 46, 58, 200);
    private static final Color TEXT = new Color(0xEA, 0xEE, 0xF5);
    private static final Color MUTED = new Color(0xB8, 0xC0, 0xCC);
    private static final Color ACCENT = new Color(0xC9, 0xA2, 0x27);

    private Boolean isWhiteSelected = null;

    public ColorSelectionDialog(Frame parent) {
        super(parent, true);

        setUndecorated(true);
        setSize(520, 320);
        setLocationRelativeTo(parent);

        // Supaya transparansi window lebih mulus (kalau OS mendukung)
        try {
            setBackground(new Color(0, 0, 0, 0));
        } catch (Exception ignored) {}

        GlassRoot root = new GlassRoot();
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.setLayout(new BorderLayout(12, 12));
        setContentPane(root);

        // Header (title + close)
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Pilih Warna");
        title.setForeground(TEXT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JButton close = new JButton("✕");
        close.setFocusPainted(false);
        close.setBorderPainted(false);
        close.setContentAreaFilled(false);
        close.setForeground(MUTED);
        close.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        close.setFont(new Font("Segoe UI", Font.BOLD, 16));
        close.addActionListener(e -> {
            isWhiteSelected = null;
            dispose();
        });

        header.add(title, BorderLayout.WEST);
        header.add(close, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        // Body
        JPanel body = new JPanel(new GridLayout(1, 2, 14, 14));
        body.setOpaque(false);

        ChoiceCard white = new ChoiceCard("Main sebagai Putih", "♔", true);
        ChoiceCard black = new ChoiceCard("Main sebagai Hitam", "♚", false);

        white.addActionListener(() -> { isWhiteSelected = true; dispose(); });
        black.addActionListener(() -> { isWhiteSelected = false; dispose(); });

        body.add(white);
        body.add(black);

        root.add(body, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        JLabel hint = new JLabel("Tip: pilihan ini menentukan orientasi papan (kamu di bawah).");
        hint.setForeground(new Color(160, 170, 185));
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JButton cancel = new JButton("Cancel");
        styleSmallButton(cancel);
        cancel.addActionListener(e -> {
            isWhiteSelected = null;
            dispose();
        });

        footer.add(hint, BorderLayout.WEST);
        footer.add(cancel, BorderLayout.EAST);

        root.add(footer, BorderLayout.SOUTH);

        // ESC to close
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
        root.getActionMap().put("close", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                isWhiteSelected = null;
                dispose();
            }
        });

        // Drag window (biar terasa premium)
        DragMover.makeDraggable(this, root);
        DragMover.makeDraggable(this, header);
    }

    public Boolean isWhiteSelected() {
        return isWhiteSelected;
    }

    private static void styleSmallButton(JButton b) {
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setForeground(TEXT);
        b.setBackground(new Color(45, 52, 64));
        b.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /** Root panel: dim background + glass card rounded. */
    private static class GlassRoot extends JPanel {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // dim whole
            g2.setColor(BG_DIM);
            g2.fillRect(0, 0, getWidth(), getHeight());

            // glass card
            int pad = 6;
            int arc = 22;
            int w = getWidth() - pad * 2;
            int h = getHeight() - pad * 2;

            g2.setColor(new Color(0, 0, 0, 120));
            g2.fillRoundRect(pad + 5, pad + 6, w, h, arc, arc);

            GradientPaint gp = new GradientPaint(0, 0, GLASS, 0, getHeight(), GLASS_2);
            g2.setPaint(gp);
            g2.fillRoundRect(pad, pad, w, h, arc, arc);

            g2.setColor(new Color(255, 255, 255, 35));
            g2.drawRoundRect(pad, pad, w, h, arc, arc);

            g2.dispose();
            super.paintComponent(g);
        }

        @Override public boolean isOpaque() { return false; }
    }

    /** Kartu pilihan (hover + clickable). */
    private static class ChoiceCard extends JPanel {
        private boolean hover = false;

        private final String label;
        private final String icon;
        private final boolean white;
        private Runnable onClick;

        ChoiceCard(String label, String icon, boolean white) {
            this.label = label;
            this.icon = icon;
            this.white = white;

            setOpaque(false);
            setBorder(new EmptyBorder(18, 18, 18, 18));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
                @Override public void mouseClicked(MouseEvent e) { if (onClick != null) onClick.run(); }
            });
        }

        void addActionListener(Runnable r) { this.onClick = r; }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int arc = 18;
            int w = getWidth();
            int h = getHeight();

            // shadow
            g2.setColor(new Color(0, 0, 0, hover ? 110 : 80));
            g2.fillRoundRect(6, 8, w - 12, h - 12, arc, arc);

            // body
            Color body = hover ? new Color(55, 64, 80, 230) : new Color(45, 52, 64, 210);
            g2.setColor(body);
            g2.fillRoundRect(0, 0, w - 12, h - 12, arc, arc);

            // border
            g2.setColor(new Color(255, 255, 255, hover ? 55 : 35));
            g2.drawRoundRect(0, 0, w - 12, h - 12, arc, arc);

            // accent strip
            g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), hover ? 230 : 180));
            g2.fillRoundRect(0, 0, 6, h - 12, arc, arc);

            // icon (bidak)
            int iconSize = Math.min(92, (h - 12) / 2);
            g2.setFont(new Font("Segoe UI Symbol", Font.PLAIN, iconSize));
            g2.setColor(white ? new Color(255, 255, 255, 235) : new Color(0, 0, 0, 210));
            g2.drawString(icon, 22, 28 + iconSize);

            // text
            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g2.setColor(TEXT);
            g2.drawString(label, 22, h - 70);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.setColor(new Color(170, 180, 195));
            g2.drawString(white ? "Kamu bermain dengan bidak putih." : "Kamu bermain dengan bidak hitam.", 22, h - 48);

            // small hint
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 220));
            g2.drawString(hover ? "Klik untuk pilih" : " ", 22, h - 26);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Utility: bikin dialog bisa di-drag. */
    private static class DragMover extends MouseAdapter {
        private final Window w;
        private Point down;

        private DragMover(Window w) { this.w = w; }

        static void makeDraggable(Window w, Component c) {
            DragMover dm = new DragMover(w);
            c.addMouseListener(dm);
            c.addMouseMotionListener(dm);
        }

        @Override public void mousePressed(MouseEvent e) { down = e.getPoint(); }

        @Override public void mouseDragged(MouseEvent e) {
            Point p = e.getLocationOnScreen();
            w.setLocation(p.x - down.x, p.y - down.y);
        }
    }
}
