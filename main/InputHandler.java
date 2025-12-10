package main;

import bidak.Bidak;
import bidak.BidakMngr;
import bidak.Pawn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class InputHandler extends MouseAdapter {

  private final BidakMngr manager;
  private final GameLogic logic;
  private final PanelGame panel;

  private Bidak selected;
  private List<int[]> possibleMoves;
  private boolean inputBlocked = false;

public void blockInput() {
    inputBlocked = true;
}

public void allowInput() {
    inputBlocked = false;
}


  public InputHandler(BidakMngr manager, GameLogic logic, PanelGame panel) {
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
        selected.setSelected(false);
        selected = null;
        possibleMoves = null;
      } else if (clicked != null && clicked.isWhite() == logic.isWhiteTurn()) {
        selected.setSelected(false);
        selected = clicked;
        selected.setSelected(true);
        possibleMoves = logic.getPossibleMoves(selected);
      } else {

        boolean moved = logic.tryMove(selected, col, row);

        // PROMOTION CHECK
        if (moved && selected instanceof Pawn) {
          Pawn pawn = (Pawn) selected;

          if (logic.isPromotionRank(pawn)) {

            java.util.List<bidak.Bidak> graveyard = pawn.isWhite()
                ? logic.getGraveyardWhite()
                : logic.getGraveyardBlack();

            if (logic.hasPromotionPiece(graveyard)) {
              // start overlay in PanelGame; PanelGame will call back to revive/promote
              panel.startPromotion(pawn, graveyard);
            }
          }
        }

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
