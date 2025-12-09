package main;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JPanel;
import bidak.Bidak;
import bidak.BidakMngr;

public class InputHandler extends MouseAdapter {

  private final BidakMngr manager;
  private final GameLogic logic;
  private final JPanel panel;

  private Bidak selected;
  private List<int[]> possibleMoves;

  public InputHandler(BidakMngr manager, GameLogic logic, JPanel panel) {
    this.manager = manager;
    this.logic = logic;
    this.panel = panel;
  }

  @Override
  public void mousePressed(MouseEvent e) {
    int col = e.getX() / Papan.KOTAK_SIZE;
    int row = e.getY() / Papan.KOTAK_SIZE;

    // klik bidak
    Bidak clicked = manager.getBidakAt(col, row);

    if (selected == null) {
      // belum ada bidak terpilih
      if (clicked != null && clicked.isWhite() == logic.isWhiteTurn()) {
        selected = clicked;
        selected.setSelected(true);
        possibleMoves = logic.getPossibleMoves(selected);
      }
    } else {
      // sudah ada bidak terpilih
      if (clicked == selected) {
        // klik bidak yang sama → batal pilih
        selected.setSelected(false);
        selected = null;
        possibleMoves = null;
      } else if (clicked != null && clicked.isWhite() == logic.isWhiteTurn()) {
        // klik bidak lain yang sama warna → ganti selection
        selected.setSelected(false);
        selected = clicked;
        selected.setSelected(true);
        possibleMoves = logic.getPossibleMoves(selected);
      } else {
        // klik papan kosong atau bidak lawan → coba gerak
        boolean moved = logic.tryMove(selected, col, row);
        selected.setSelected(false);
        selected = null;
        possibleMoves = null;
      }
    }

    panel.repaint();

  }

  public void drawMoveHints(Graphics2D g) {
    if (possibleMoves == null)
      return;

    g.setColor(new Color(0, 255, 0, 120));
    for (int[] m : possibleMoves) {
      g.fillOval(
          m[0] * Papan.KOTAK_SIZE + Papan.KOTAK_SIZE / 3,
          m[1] * Papan.KOTAK_SIZE + Papan.KOTAK_SIZE / 3,
          Papan.KOTAK_SIZE / 3,
          Papan.KOTAK_SIZE / 3);
    }
  }
}
