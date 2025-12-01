package main;

import bidak.bidak;
import bidak.bidakMngr;
import java.util.ArrayList;
import java.util.List;

public class gameLogic {
  private final bidakMngr manager;
  private boolean whiteTurn = true;

  // Graveyard untuk bidak yang sudah dimakan
  private final List<bidak> graveyardWhite = new ArrayList<>();
  private final List<bidak> graveyardBlack = new ArrayList<>();

  public gameLogic(bidakMngr manager) {
    this.manager = manager;
  }

  /** Cek giliran saat ini */
  public boolean isWhiteTurn() {
    return whiteTurn;
  }

  /** Ambil semua kemungkinan gerakan untuk bidak tertentu */
  public List<int[]> getPossibleMoves(bidak b) {
    if (b == null)
      return new ArrayList<>();
    return b.getPossibleMoves(manager.getAllBidaks());
  }

  /**
   * Coba pindahkan bidak ke kolom dan baris target
   * Kembalikan true jika berhasil, false jika invalid
   */
  public boolean tryMove(bidak b, int targetCol, int targetRow) {
    if (b == null || b.isWhite != whiteTurn)
      return false;

    bidak target = manager.getBidakAt(targetCol, targetRow);

    // cek apakah target valid
    boolean valid = getPossibleMoves(b).stream()
        .anyMatch(m -> m[0] == targetCol && m[1] == targetRow);

    if (!valid)
      return false;

    // tangani bidak yang dimakan
    if (target != null) {
      target.alive = false;
      if (target.isWhite)
        graveyardWhite.add(target);
      else
        graveyardBlack.add(target);
    }

    // pindahkan bidak
    b.col = targetCol;
    b.row = targetRow;

    // ganti giliran
    whiteTurn = !whiteTurn;
    return true;
  }

  /** Ambil daftar bidak putih yang sudah dimakan */
  public List<bidak> getGraveyardWhite() {
    return graveyardWhite;
  }

  /** Ambil daftar bidak hitam yang sudah dimakan */
  public List<bidak> getGraveyardBlack() {
    return graveyardBlack;
  }
}
