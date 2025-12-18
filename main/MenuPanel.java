
package main;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Panel kiri "MAIN MENU":
 * - Semua tombol ukuran sama
 * - Spacing konsisten
 * - Rounded modern card style
 * - Hover effect halus
 * - Active state (highlight) saat dipilih
 *
 * Callback actions disediakan lewat interface MenuActions.
 */
public class MenuPanel extends JPanel {

    public interface MenuActions {
        void onPlay();
        void onMultiplayerComingSoon();
        void onSettings();
        void onHelp();
        void onExit();
    }

    private static final Color PANEL_BG = new Color(0x1B, 0x20, 0x28);
    private static final Color PANEL_BORDER = new Color(255, 255, 255, 26);

    private static final Color TEXT = new Color(0xEA, 0xEE, 0xF5);
    private static final Color MUTED = new Color(0x95, 0xA0, 0xB2);
    private static final Color ACCENT = new Color(0xC9, 0xA2, 0x27);

    private final List<MenuButton> buttons = new ArrayList<>();

    public MenuPanel(MenuActions actions) {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(0, 0, 0, 0));

        Card card = new Card();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(26, 18, 18, 18));


        // Header "MAIN MENU"
        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("MAIN MENU");
        title.setForeground(MUTED);
        title.setFont(new Font("Segoe UI", Font.BOLD, 12));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(PANEL_BORDER);
        sep.setOpaque(false);

        head.add(title);
        head.add(Box.createVerticalStrut(12));
        head.add(sep);
        head.add(Box.createVerticalStrut(16));

        // Button stack
        JPanel stack = new JPanel();
        stack.setOpaque(false);
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));

        // Semua tombol dibuat sama ukuran (fixed height) dan max width fill
        Dimension btnSize = new Dimension(0, 52);

        // UI ONLY CHANGE: pakai ikon ASCII agar aman di semua compiler/OS
        MenuButton play = new MenuButton("Play", ">");
        MenuButton multi = new MenuButton("Multiplayer", ">");
        MenuButton settings = new MenuButton("Settings", ">");
        MenuButton help = new MenuButton("Help / How To Play", "?");
        MenuButton exit = new MenuButton("Exit", "X");

        play.setPreferredHeight(btnSize.height);
        multi.setPreferredHeight(btnSize.height);
        settings.setPreferredHeight(btnSize.height);
        help.setPreferredHeight(btnSize.height);
        exit.setPreferredHeight(btnSize.height);

        // Badge "COMING SOON" untuk multiplayer
       

        // Actions
        play.setOnClick(() -> { setActive(play); actions.onPlay(); });
        multi.setOnClick(() -> { setActive(multi); actions.onMultiplayerComingSoon(); });
        settings.setOnClick(() -> { setActive(settings); actions.onSettings(); });
        help.setOnClick(() -> { setActive(help); actions.onHelp(); });
        exit.setOnClick(() -> { setActive(exit); actions.onExit(); });

        addBtn(stack, play);
        addBtn(stack, multi);
        addBtn(stack, settings);
        addBtn(stack, help);
        stack.add(Box.createVerticalGlue());
        addBtn(stack, exit);

        // default active
        setActive(play);

        card.add(head, BorderLayout.NORTH);
        card.add(stack, BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);
    }

    private void addBtn(JPanel stack, MenuButton btn) {
        buttons.add(btn);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        stack.add(btn);
        stack.add(Box.createVerticalStrut(12)); // spacing konsisten
    }


    private void setActive(MenuButton active) {
        for (MenuButton b : buttons) b.setActive(b == active);
        repaint();
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

    /** Tombol menu: rounded, hover halus, active state, badge opsional */
    private static class MenuButton extends JComponent {
        private final String text;
        private final String icon;

        private boolean hover = false;
        private boolean active = false;

        private String badge = null;
        private Runnable onClick;

        private int prefHeight = 52;

        MenuButton(String text, String icon) {
            this.text = text;
            this.icon = icon;

            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setFocusable(true);

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
                @Override public void mousePressed(MouseEvent e) {
                    requestFocusInWindow();
                    if (onClick != null) onClick.run();
                }
            });
        }

        void setOnClick(Runnable r) { this.onClick = r; }
        void setActive(boolean v) { this.active = v; repaint(); }
        void setBadge(String t) { this.badge = t; repaint(); }
        void setPreferredHeight(int h) { this.prefHeight = h; revalidate(); }

        @Override public Dimension getPreferredSize() {
            return new Dimension(320, prefHeight);
        }

        @Override public Dimension getMaximumSize() {
            return new Dimension(Integer.MAX_VALUE, prefHeight);
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int arc = 18;

            Color base = new Color(0x23, 0x28, 0x31);
            Color hoverBg = new Color(0x2B, 0x31, 0x3C);
            Color activeBg = new Color(0x2A, 0x2F, 0x3A);

            Color bg = active ? activeBg : (hover ? hoverBg : base);

            // shadow
            g2.setColor(new Color(0, 0, 0, hover ? 95 : 70));
            g2.fillRoundRect(3, 5, w - 6, h - 6, arc, arc);

            // body
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, w - 6, h - 6, arc, arc);

            // accent strip (lebih tegas saat active)
            int aAlpha = active ? 240 : (hover ? 200 : 160);
            g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), aAlpha));
            g2.fillRoundRect(0, 0, 6, h - 6, arc, arc);

            // border
            g2.setColor(new Color(255, 255, 255, hover ? 42 : 26));
            g2.drawRoundRect(0, 0, w - 6, h - 6, arc, arc);

            // icon
            g2.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 16));
            g2.setColor(TEXT);
            int leftPad = 16;
            int iconX = leftPad;
            int iconY = (h - 6) / 2 + 6;
            g2.drawString(icon, iconX, iconY);

            // text
            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g2.setColor(TEXT);
            int textX = leftPad + 28;
            int textY = (h - 6) / 2 + 6;
            g2.drawString(text, textX, textY);

            // badge (coming soon)
            if (badge != null) {
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                FontMetrics fm = g2.getFontMetrics();
                int padX = 10, padY = 5;
                int bw = fm.stringWidth(badge) + padX * 2;
                int bh = fm.getHeight() + padY;

                int bx = w - 6 - bw - 14;
                int by = (h - 6 - bh) / 2;

                g2.setColor(new Color(0, 0, 0, 70));
                g2.fillRoundRect(bx + 2, by + 2, bw, bh, 12, 12);

                g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 210));
                g2.fillRoundRect(bx, by, bw, bh, 12, 12);

                g2.setColor(new Color(0x10, 0x12, 0x16));
                g2.drawString(badge, bx + padX, by + padY + fm.getAscent());
            }

            g2.dispose();
        }
    }
}
