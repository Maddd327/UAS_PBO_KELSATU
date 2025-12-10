package bidak;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;

import java.awt.*;
import javax.swing.Timer;

import main.Papan;

public class King extends Bidak {
  public boolean shakeRedVisible = false;
  public int shakeOffset = 0;

  private static final int[][] MOVES = {
      { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }, { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 }
  };

  public King(BufferedImage img, int col, int row, boolean isWhite) {
    super(img, col, row, isWhite);
  }

  public void shakeRed() {
    shakeRedVisible = true;
    shakeOffset = 0;

    final int totalDuration = 1000;
    final int interval = 100;
    final int frames = totalDuration / interval;
    final int shakeAmount = 5;

    new javax.swing.Timer(interval, new AbstractAction() {
      int frame = 0;
      boolean forward = true;

      @Override
      public void actionPerformed(java.awt.event.ActionEvent e) {
        if (frame >= frames) {
          shakeRedVisible = false;
          shakeOffset = 0;
          ((Timer) e.getSource()).stop();
          return;
        }

        shakeOffset = (forward ? shakeAmount : -shakeAmount);
        forward = !forward;
        shakeRedVisible = !shakeRedVisible;
        frame++;
      }
    }).start();
  }

  @Override
  public List<int[]> getPossibleMoves(Bidak[] all) {
    List<int[]> moves = new ArrayList<>();
    for (int[] m : MOVES)
      addStepMove(col + m[0], row + m[1], all, moves);

    // castling
    // TODO: cek hasMoved rook & king, jalur kosong, raja tidak in check
    return moves;
  }

  @Override
  public String getName() {
    return isWhite ? "King Putih" : "King Hitam";
  }

  @Override
  public void draw(Graphics2D g2) {
    // draw biasa
    super.draw(g2);

    int drawX = col * Papan.KOTAK_SIZE + shakeOffset;
    if (shakeRedVisible) {
      g2.setColor(new Color(255, 0, 0, 120));
      g2.fillRect(drawX, row * Papan.KOTAK_SIZE, Papan.KOTAK_SIZE, Papan.KOTAK_SIZE);
    }
    g2.drawImage(img, drawX, row * Papan.KOTAK_SIZE, Papan.KOTAK_SIZE, Papan.KOTAK_SIZE, null);

  }
}
