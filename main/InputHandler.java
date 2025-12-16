package main;

import bidak.Bidak;
import bidak.BidakMngr;
import bidak.Pawn;
import java.awt.Color;
import java.awt.Graphics2D;
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

  public Bidak getSelectedBidak() {
    return selected;
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
    if (inputBlocked) return;

    int[] br = panel.screenToBoard(e.getX(), e.getY());
    if (br == null) return;

    int col = br[0];
    int row = br[1];

    // klik bidak
    Bidak clicked = manager.getBidakAt(col, row);

    // Jika sedang skak, beri feedback animasi pada raja
    if (logic.isCurrentSideInCheck()) {
      bidak.King k = null;
      for (bidak.Bidak bb : manager.getAllBidaks()) {
        if (!bb.isCaptured() && bb instanceof bidak.King && bb.isWhite() == logic.isWhiteTurn()) {
          k = (bidak.King) bb;
          break;
        }
      }
      if (k != null) {
        // panggil sekali tiap interaksi agar terlihat berkedip/goyang
        k.shakeRed();
      }
    }

    if (selected == null) {
      // belum ada bidak terpilih
      if (clicked != null && clicked.isWhite() == logic.isWhiteTurn()) {
        // saat skak: hanya bidak yang punya langkah legal yang boleh dipilih
        List<int[]> legal = logic.getLegalMoves(clicked);
        if (logic.isCurrentSideInCheck() && (legal == null || legal.isEmpty())) {
          // tidak bisa menyelamatkan raja
          selected = null;
          possibleMoves = null;
        } else {
          selected = clicked;
          selected.setSelected(true);
          possibleMoves = legal;
        }
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
        possibleMoves = logic.getLegalMoves(selected);
      } else {

        boolean moved = logic.tryMove(selected, col, row);
        if (!moved) {
          // tetap tampilkan langkah legal (terutama saat skak)
          possibleMoves = logic.getLegalMoves(selected);
          panel.repaint();
          return;
        }

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
      int[] scr = panel.boardToScreen(m[0], m[1]);
      if (scr == null) continue;
      g.fillOval(
          scr[0] * Papan.KOTAK_SIZE + Papan.KOTAK_SIZE / 3,
          scr[1] * Papan.KOTAK_SIZE + Papan.KOTAK_SIZE / 3,
          Papan.KOTAK_SIZE / 3,
          Papan.KOTAK_SIZE / 3);
    }
  }
}
