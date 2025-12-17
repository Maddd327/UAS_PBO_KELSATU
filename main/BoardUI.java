package main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

 // khusus tampilan papan (highlight, hover, dsb).

public final class BoardUI {

    private BoardUI() {}

    public static void drawSelectedSquare(Graphics2D g2, int x, int y, int size) {
        UIConfig.BoardTheme theme = UIConfig.getTheme();
        Color a = theme.accent;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // shadow
        g2.setColor(new Color(0, 0, 0, 90));
        g2.fillRoundRect(x + 5, y + 6, size - 10, size - 10, 18, 18);

        // overlay
        g2.setColor(new Color(a.getRed(), a.getGreen(), a.getBlue(), 90));
        g2.fillRoundRect(x + 4, y + 4, size - 8, size - 8, 18, 18);

        // border
        g2.setColor(new Color(a.getRed(), a.getGreen(), a.getBlue(), 200));
        g2.drawRoundRect(x + 3, y + 3, size - 6, size - 6, 18, 18);
    }
}
