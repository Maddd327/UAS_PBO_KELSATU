package main;

import java.awt.Color;
import java.awt.Graphics2D;

public class Papan {
    public static final int MAX_COL = 8;
    public static final int MAX_ROW = 8;
    public static final int KOTAK_SIZE = 100;
    public static final int HALF_KOTAK_SIZE = KOTAK_SIZE / 2;

    public void draw(Graphics2D g2) {

        int width = MAX_COL * KOTAK_SIZE;
        int height = MAX_ROW * KOTAK_SIZE;

        // --- Gambar papan ---
        int c = 0;
        for (int row = 0; row < MAX_ROW; row++) {
            for (int col = 0; col < MAX_COL; col++) {
                g2.setColor(c == 0 ? new Color(210, 165, 125) : new Color(175, 115, 70));
                g2.fillRect(col * KOTAK_SIZE, row * KOTAK_SIZE, KOTAK_SIZE, KOTAK_SIZE);
                c = 1 - c;
            }
            c = 1 - c;
        }

        // --- Outline tipis ---
        g2.setColor(new Color(80, 50, 30)); // coklat tua elegan
        g2.drawRect(0, 0, width - 1, height - 1); // outline 1px
    }
}
