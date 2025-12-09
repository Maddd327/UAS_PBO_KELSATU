package main;

import java.awt.*;
import javax.swing.JPanel;
import bidak.BidakMngr;

public class PanelGame extends JPanel implements Runnable {

    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;
    private final int FPS = 60;
    private Thread gameThread;

    private final Papan papan = new Papan();
    private final BidakMngr bidakMngr = new BidakMngr();
    private final GameLogic logic = new GameLogic(bidakMngr);

    private final InputHandler input;
    private final SidebarRenderer sidebar;

    public PanelGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.black);

        input = new InputHandler(bidakMngr, logic, this);
        sidebar = new SidebarRenderer(logic, bidakMngr);

        addMouseListener(input);
    }

    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1_000_000_000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();

        while (gameThread != null) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    private void update() {
        // tempat animasi / update logika jika perlu
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        papan.draw(g2);
        input.drawMoveHints(g2);
        bidakMngr.draw(g2);
        sidebar.draw(g2);
    }
}
