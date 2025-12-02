package main;

import bidak.bidak;
import bidak.bidakMngr;
import java.util.ArrayList;
import java.util.List;

public class gameLogic {

  private final bidakMngr manager;
  private boolean whiteTurn = true;

  private final List<bidak> graveyardWhite = new ArrayList<>();
  private final List<bidak> graveyardBlack = new ArrayList<>();

  public gameLogic(bidakMngr manager) {
    this.manager = manager;
  }

  public boolean isWhiteTurn() {
    return whiteTurn;
  }

  public List<int[]> getPossibleMoves(bidak b) {
    if (b == null)
      return new ArrayList<>();
    return b.getPossibleMoves(manager.getAllBidaks());
  }

  public boolean tryMove(bidak b, int targetCol, int targetRow) {
    if (b == null || b.isWhite != whiteTurn)
      return false;

    bidak target = manager.getBidakAt(targetCol, targetRow);

    boolean valid = getPossibleMoves(b).stream()
        .anyMatch(m -> m[0] == targetCol && m[1] == targetRow);

    if (!valid)
      return false;

    // --- FIX: bidak mati benar-benar hilang ---
    if (target != null) {
      target.captured = true;

      if (target.isWhite)
        graveyardWhite.add(target);
      else
        graveyardBlack.add(target);
    }

    b.col = targetCol;
    b.row = targetRow;

    // ganti giliran
    whiteTurn = !whiteTurn;

    // hapus bidak yang tadi ditandai captured
    manager.cleanup();

    return true;

  }

  public List<bidak> getGraveyardWhite() {
    return graveyardWhite;
  }

  public List<bidak> getGraveyardBlack() {
    return graveyardBlack;
  }
}
