package main;

import java.awt.Color;
import java.awt.Graphics2D;

public class Papan {
    public static final int MAX_COL = 8;
    public static final int MAX_ROW = 8;
    public static final int KOTAK_SIZE = 100;
    public static final int HALF_KOTAK_SIZE = KOTAK_SIZE / 2;

    public void draw(Graphics2D g2) {
        draw(g2, true);
    }

    public void draw(Graphics2D g2, boolean playerIsWhite) {

        int width = MAX_COL * KOTAK_SIZE;
        int height = MAX_ROW * KOTAK_SIZE;

        // UI ONLY CHANGE: warna papan mengikuti tema yang dipilih user dari Main Menu
        UIConfig.BoardTheme theme = UIConfig.getTheme();

        // --- Gambar papan ---
        int c = 0;
        for (int displayRow = 0; displayRow < MAX_ROW; displayRow++) {
    for (int displayCol = 0; displayCol < MAX_COL; displayCol++) {

        // Mapping koordinat papan -> tampilan (flip jika pemain memilih hitam)
        int boardCol = playerIsWhite ? displayCol : (MAX_COL - 1 - displayCol);
        int boardRow = playerIsWhite ? displayRow : (MAX_ROW - 1 - displayRow);

        // Warna papan harus konsisten berdasarkan koordinat papan (bukan tampilan)
        boolean light = ((boardCol + boardRow) % 2 == 0);
        g2.setColor(light ? theme.light : theme.dark);
        g2.fillRect(displayCol * KOTAK_SIZE, displayRow * KOTAK_SIZE, KOTAK_SIZE, KOTAK_SIZE);
    }
}

        // --- Outline tipis ---
        g2.setColor(theme.outline);
        g2.drawRect(0, 0, width - 1, height - 1); // outline 1px
    }
}
