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

import main.bidak.bidak;
import main.bidak.bidakMngr;
import main.bidak.pawn;

public class panelgame extends JPanel implements Runnable {
    public static final int WIDTH = 1200;
    public static final int HEIGHT = 800;
    final int FPS = 60;
    Thread gameThread;
    Papan papan = new Papan();
    bidak selectedBidak = null;
    List<int[]> moves = new ArrayList<>();
    // simpan bidak terpilih

    public panelgame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.black);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int col = e.getX() / Papan.KOTAK_SIZE;
                int row = e.getY() / Papan.KOTAK_SIZE;

                if (selectedBidak != null) {
                    // cek apakah klik di kotak tujuan
                    boolean validMove = false;
                    for (int[] move : moves) {
                        if (move[0] == col && move[1] == row) {
                            validMove = true;
                            break;
                        }
                    }

                    if (validMove) {
                        // pindahkan bidak
                        selectedBidak.col = col;
                        selectedBidak.row = row;
                        moves.clear();
                        selectedBidak.selected = false;
                        selectedBidak = null;
                        repaint();
                        return; // sudah pindah, keluar
                    }
                }

                // jika tidak memindahkan, pilih bidak baru
                selectedBidak = bidakMngr.selectBidakAt(col, row);
                if (selectedBidak != null && selectedBidak instanceof pawn) {
                    pawn p = (pawn) selectedBidak;
                    moves = p.getPossibleMoves(bidakMngr.getAllBidaks());
                } else {
                    moves.clear();
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

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // gambar papan
        papan.draw(g2);

        // highlight kotak gerakan (jika ada)
        if (moves != null) {
            g2.setColor(new Color(0, 255, 0, 100)); // hijau transparan
            for (int[] move : moves) {
                g2.fillRect(move[0] * Papan.KOTAK_SIZE, move[1] * Papan.KOTAK_SIZE,
                        Papan.KOTAK_SIZE, Papan.KOTAK_SIZE);
            }
        }

        // gambar semua bidak di atas highlight
        bidakMngr.draw(g2);
    }

}
