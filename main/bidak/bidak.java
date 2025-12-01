package main.bidak;

import main.Papan;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class bidak {
  public BufferedImage img;
  public int col, row; // posisi grid 0–7
  public boolean alive = true;

  public bidak(BufferedImage img, int col, int row) {
    this.img = img;
    this.col = col;
    this.row = row;
  }

  public void draw(Graphics2D g2) {
    if (alive) {
      g2.drawImage(
          img,
          col * Papan.KOTAK_SIZE,
          row * Papan.KOTAK_SIZE,
          Papan.KOTAK_SIZE,
          Papan.KOTAK_SIZE,
          null);
    }
  }
}
