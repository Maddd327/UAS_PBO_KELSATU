package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import bidak.bidak;
import bidak.bidakMngr;
import java.awt.Font;

public class panelgame extends JPanel implements Runnable {
    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;
    public static final int SIDEBAR_WIDTH = 300;
    final int FPS = 60;
    Thread gameThread;

    Papan papan = new Papan();
    bidak selectedBidak = null;
    List<int[]> moves = null;

    bidakMngr bidakMngr = new bidakMngr();
    gameLogic logic = new gameLogic(bidakMngr);

    public panelgame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.black);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Klik di papan saja, bukan sidebar
                if (e.getX() >= Papan.MAX_COL * Papan.KOTAK_SIZE)
                    return;

                int col = e.getX() / Papan.KOTAK_SIZE;
                int row = e.getY() / Papan.KOTAK_SIZE;

                bidak clicked = bidakMngr.getBidakAt(col, row);

                // Pilih bidak sesuai giliran
                if (clicked != null && clicked.isWhite == logic.isWhiteTurn()) {
                    selectedBidak = clicked;
                    moves = logic.getPossibleMoves(clicked);
                }
                // Klik kotak tujuan
                else if (selectedBidak != null && moves != null) {
                    boolean moved = logic.tryMove(selectedBidak, col, row);
                    if (moved) {
                        selectedBidak = null;
                        moves.clear();
                        moves = null;
                    }
                }

                repaint();
            }
        });
    }

    public void launchgame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawinterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawinterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    private void update() {
        // Animasi / logic tambahan bisa ditempatkan di sini
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Gambar papan
        papan.draw(g2);

        // Highlight moves
        if (moves != null) {
            g2.setColor(new Color(0, 255, 0, 100));
            for (int[] move : moves) {
                g2.fillRect(move[0] * Papan.KOTAK_SIZE, move[1] * Papan.KOTAK_SIZE,
                        Papan.KOTAK_SIZE, Papan.KOTAK_SIZE);
            }
        }

        // Gambar bidak
        bidakMngr.draw(g2);

        // Sidebar background
        int sidebarX = Papan.MAX_COL * Papan.KOTAK_SIZE;
        g2.setColor(new Color(78, 53, 36));
        g2.fillRect(sidebarX, 0, SIDEBAR_WIDTH, HEIGHT);

        int yOffset = 20;

        // Turn info
        g2.setColor(Color.WHITE);
        g2.drawString("Turn:", sidebarX + 20, yOffset);

        BufferedImage turnPawn = null;
        if (logic.isWhiteTurn()) {
            turnPawn = bidakMngr.getWhitePawnIcon(); // method baru di bidakMngr yang mengembalikan icon pawn putih
        } else {
            turnPawn = bidakMngr.getBlackPawnIcon(); // method baru di bidakMngr yang mengembalikan icon pawn hitam
        }

        if (turnPawn != null) {
            g2.drawImage(turnPawn, sidebarX + 80, yOffset - 16, Papan.HALF_KOTAK_SIZE,
                    Papan.HALF_KOTAK_SIZE, null);
        }

        yOffset += 50;

        // Graveyard White
        g2.drawString("White Graveyard:", sidebarX + 20, yOffset);
        yOffset += 10;
        int gx = sidebarX + 20;
        int gy = yOffset;
        int count = 0;
        for (bidak b : logic.getGraveyardWhite()) {
            g2.drawImage(b.img, gx, gy, Papan.HALF_KOTAK_SIZE, Papan.HALF_KOTAK_SIZE, null);
            gx += Papan.HALF_KOTAK_SIZE + 5;
            count++;
            if (count % 3 == 0) { // grid 3 kolom
                gx = sidebarX + 20;
                gy += Papan.HALF_KOTAK_SIZE + 5;
            }
        }

        yOffset = gy + Papan.HALF_KOTAK_SIZE + 20;

        // Graveyard Black
        g2.drawString("Black Graveyard:", sidebarX + 20, yOffset);
        yOffset += 10;
        gx = sidebarX + 20;
        gy = yOffset;
        count = 0;
        for (bidak b : logic.getGraveyardBlack()) {
            g2.drawImage(b.img, gx, gy, Papan.HALF_KOTAK_SIZE, Papan.HALF_KOTAK_SIZE, null);
            gx += Papan.HALF_KOTAK_SIZE + 5;
            count++;
            if (count % 3 == 0) {
                gx = sidebarX + 20;
                gy += Papan.HALF_KOTAK_SIZE + 5;
            }
        }
    }

}
