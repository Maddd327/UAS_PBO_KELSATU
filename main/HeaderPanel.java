package main;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Header bagian atas:
 * - Background terang/netral
 * - Judul besar di tengah: "Catur UMG"
 * - Subjudul di bawah
 * - Badge v1 di pojok kanan atas
 */
public class HeaderPanel extends JPanel {

    private static final Color BG = new Color(0xF3F5F8);
    private static final Color TITLE = new Color(0x131722);
    private static final Color SUB = new Color(0x5C6676);
    private static final Color BADGE_BG = new Color(0xC9, 0xA2, 0x27);

    public HeaderPanel() {
        setOpaque(true);
        setBackground(BG);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(18, 18, 12, 18));

        // Center title
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Catur UMG");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(TITLE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 44));

        // UI ONLY CHANGE: hindari karakter non-ASCII
        JLabel sub = new JLabel("Chess - Modern UI - Local Play (Multiplayer Online: Coming Soon)");
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        sub.setForeground(SUB);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        center.add(title);
        center.add(Box.createVerticalStrut(6));
        center.add(sub);

        // Badge v1 top-right
        JLabel badge = new JLabel(" v1 ");
        badge.setOpaque(true);
        badge.setBackground(BADGE_BG);
        badge.setForeground(new Color(0x10, 0x12, 0x16));
        badge.setBorder(new EmptyBorder(6, 12, 6, 12));
        badge.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.add(badge);

        add(center, BorderLayout.CENTER);
        add(right, BorderLayout.EAST);
    }
}
