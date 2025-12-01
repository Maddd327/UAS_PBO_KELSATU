package main.bidak;

import main.Papan;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;

public class bidak {
  public BufferedImage img;
  public int col, row; // posisi grid 0–7
  public boolean alive = true;
  public boolean selected = false;
  public boolean isWhite; // <-- tambahkan properti ini

  public bidak(BufferedImage img, int col, int row, boolean isWhite) {
    this.img = img;
    this.col = col;
    this.row = row;
    this.isWhite = isWhite;
  }

  public void draw(Graphics2D g2) {
    if (alive) {
      // gambar bidak
      g2.drawImage(
          img,
          col * Papan.KOTAK_SIZE,
          row * Papan.KOTAK_SIZE,
          Papan.KOTAK_SIZE,
          Papan.KOTAK_SIZE,
          null);

      // indikator jika terpilih
      if (selected) {
        g2.setColor(Color.YELLOW);
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(
            col * Papan.KOTAK_SIZE,
            row * Papan.KOTAK_SIZE,
            Papan.KOTAK_SIZE,
            Papan.KOTAK_SIZE);
      }
    }
  }

  public List<int[]> getPossibleMoves(bidak[] allBidaks) {
    return null; // di-override di subclass
  }
}
