package main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class MainMenu extends JFrame {
    private JButton startGameButton;
    private JButton multiplayerButton;
    private JButton exitButton;
    private float titleAlpha = 0f; // For animation
    
    public MainMenu() {
        setTitle("CaturUmgPBO Menu");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Background panel
        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Gradient background
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(30, 30, 60),
                        0, getHeight(), new Color(10, 10, 20)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bgPanel.setLayout(new BorderLayout());
        bgPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title Panel
        JPanel titlePanel = new JPanel() {
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, titleAlpha));
                ImageIcon icon = new ImageIcon("asset/chess.png");
                setIconImage(icon.getImage());
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 32));
                g2d.setColor(Color.WHITE);
                String title = "CaturUmgPBO";
                int textWidth = g2d.getFontMetrics().stringWidth(title);
                g2d.drawString(title, (getWidth() - textWidth) / 2, 50);
            }
        };
        titlePanel.setOpaque(false);
        titlePanel.setPreferredSize(new Dimension(600, 120));
        bgPanel.add(titlePanel, BorderLayout.NORTH);

        animateTitleFade(titlePanel);

        // Center menu panel (semi transparent)
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);

        startGameButton = createMenuButton("Mulai Game", "▶");
        multiplayerButton = createMenuButton("Multiplayer", "♟");
        exitButton = createMenuButton("Keluar", "✖");

        startGameButton.addActionListener(e -> openColorSelectionDialog());
        multiplayerButton.addActionListener(e -> showMultiplayerPlaceholder());
        exitButton.addActionListener(e -> System.exit(0));

        addMenuItem(menuPanel, startGameButton);
        addMenuItem(menuPanel, multiplayerButton);
        addMenuItem(menuPanel, exitButton);

        bgPanel.add(menuPanel, BorderLayout.CENTER);
        add(bgPanel);

        setVisible(true);
        
    }

    // Fancy button generator
    private JButton createMenuButton(String text, String iconSymbol) {
        JButton btn = new JButton(iconSymbol + "   " + text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(60, 60, 90));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Rounded corners
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(btn.getBackground());
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 20, 20);

                super.paint(g, c);
            }
        });

        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(80, 80, 140));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(60, 60, 90));
            }
        });

        return btn;
    }

    // Add spacing between menu items
    private void addMenuItem(JPanel panel, JButton btn) {
        panel.add(btn);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
    }

    // Title fade animation
    private void animateTitleFade(JPanel titlePanel) {
        Timer timer = new Timer(30, e -> {
            titleAlpha += 0.02f;
            if (titleAlpha >= 1f) {
                titleAlpha = 1f;
                ((Timer) e.getSource()).stop();
            }
            titlePanel.repaint();
        });
        timer.start();
    }

    private void openColorSelectionDialog() {
        ColorSelectionDialog dialog = new ColorSelectionDialog(this);
        dialog.setVisible(true);
        Boolean isWhite = dialog.isWhiteSelected();

        if (isWhite != null) {
            dispose();
            startGame(isWhite);
        }
    }

    private void showMultiplayerPlaceholder() {
        JOptionPane.showMessageDialog(this,
            "Multiplayer mode coming soon!",
            "Placeholder",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void startGame(boolean isWhite) {
    PanelGame panelGame = new PanelGame(isWhite);  // langsung gunakan warna pemain!
    
    JFrame frame = new JFrame("Chess Game");
    frame.setContentPane(panelGame);
    frame.pack();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);

    panelGame.launchGame();   // pastikan game loop berjalan
}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainMenu::new);
    }
}
