package main;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.JPanel;
import bidak.Bidak;
import bidak.BidakMngr;
import bidak.Bishop;
import bidak.Knight;
import bidak.Pawn;
import bidak.Queen;
import bidak.Rook;

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
    private final PromotionOverlay promotionOverlay;

    public PanelGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.black);

        input = new InputHandler(bidakMngr, logic, this);
        sidebar = new SidebarRenderer(logic, bidakMngr);

        promotionOverlay = new PromotionOverlay(bidakMngr);
        setLayout(null);
        promotionOverlay.setBounds(0, 0, WIDTH, HEIGHT);
        promotionOverlay.setVisible(false);

        addMouseListener(input);
        add(promotionOverlay);
    }

    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        final double drawInterval = 1_000_000_000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();

        while (gameThread != null) {
            final long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    public void startPromotion(final Pawn pawn, final java.util.List<Bidak> graveyard) {
        // filter hanya bidak promosi yang valid
        java.util.List<Bidak> validPromotions = new ArrayList<>();
        for (Bidak b : graveyard) {
            if (b instanceof Queen || b instanceof Rook || b instanceof Bishop || b instanceof Knight) {
                validPromotions.add(b);
            }
        }

        promotionOverlay.showPromotion(pawn, validPromotions, (selected) -> {
            logic.reviveFromGrave(selected);
            bidakMngr.promotePawnFromGrave(pawn, selected);
            repaint();
        });
    }

    private void update() {
        // tempat animasi / update logika jika perlu
    }

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2 = (Graphics2D) g;

        papan.draw(g2);
        input.drawMoveHints(g2);
        bidakMngr.draw(g2);
        sidebar.draw(g2);
        // overlay menggambar sendiri sebagai child component
    }
}
