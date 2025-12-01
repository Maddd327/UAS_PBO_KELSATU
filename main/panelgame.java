package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import bidak.bidak;
import bidak.bidakMngr;

public class panelgame extends JPanel implements Runnable {
    public static final int WIDTH = 1200;
    public static final int HEIGHT = 800;
    final int FPS = 60;
    Thread gameThread;
    Papan papan = new Papan();
    bidak selectedBidak = null;
    List<int[]> moves = new ArrayList<>();
    boolean whiteTurn = true;
    // simpan bidak terpilih

    public panelgame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.black);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int col = e.getX() / Papan.KOTAK_SIZE;
                int row = e.getY() / Papan.KOTAK_SIZE;

                // klik bidak
                bidak clicked = bidakMngr.getBidakAt(col, row);

                // pilih bidak sesuai giliran
                if (clicked != null && clicked.isWhite == whiteTurn) {
                    selectedBidak = clicked;
                    moves = selectedBidak.getPossibleMoves(bidakMngr.getAllBidaks());
                }
                // klik kotak tujuan
                else if (selectedBidak != null) {
                    for (int[] move : moves) {
                        if (move[0] == col && move[1] == row) {
                            selectedBidak.col = col;
                            selectedBidak.row = row;

                            selectedBidak = null;
                            moves.clear();

                            // ganti giliran setelah bergerak
                            whiteTurn = !whiteTurn;
                            break;
                        }
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

    // akif
    bidakMngr bidakMngr = new bidakMngr();

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

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        papan.draw(g2);
        bidakMngr.draw(g2);

        // highlight
        g2.setColor(new Color(0, 255, 0, 100));
        for (int[] move : moves) {
            g2.fillRect(move[0] * Papan.KOTAK_SIZE, move[1] * Papan.KOTAK_SIZE,
                    Papan.KOTAK_SIZE, Papan.KOTAK_SIZE);
        }
    }

}
