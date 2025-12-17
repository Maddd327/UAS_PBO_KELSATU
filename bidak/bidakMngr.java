package bidak;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class BidakMngr {

  private final List<Bidak> bidaks = new ArrayList<>();

  // BufferedImages untuk setiap type bidak
  BufferedImage whitePawn, whiteRook, whiteKnight, whiteBishop, whiteQueen, whiteKing;
  BufferedImage blackPawn, blackRook, blackKnight, blackBishop, blackQueen, blackKing;

  public BidakMngr() {
    loadImages();
    setupPieces();
  }

  private void loadImages() {
    // Sesuaikan imageLoader sesuai implementasimu
    whitePawn = imageLoader.load("asset/img/W_pawn.png");
    whiteRook = imageLoader.load("asset/img/W_rook.png");
    whiteKnight = imageLoader.load("asset/img/W_knight.png");
    whiteBishop = imageLoader.load("asset/img/W_bishop.png");
    whiteQueen = imageLoader.load("asset/img/W_queen.png");
    whiteKing = imageLoader.load("asset/img/W_king.png");

    blackPawn = imageLoader.load("asset/img/B_pawn.png");
    blackRook = imageLoader.load("asset/img/B_rook.png");
    blackKnight = imageLoader.load("asset/img/B_knight.png");
    blackBishop = imageLoader.load("asset/img/B_bishop.png");
    blackQueen = imageLoader.load("asset/img/B_queen.png");
    blackKing = imageLoader.load("asset/img/B_king.png");
  }

  private void setupPieces() {
    // Pawns
    for (int i = 0; i < 8; i++) {
      bidaks.add(new Pawn(whitePawn, i, 6, true));
      bidaks.add(new Pawn(blackPawn, i, 1, false));
    }
    // Rooks
    bidaks.add(new Rook(whiteRook, 0, 7, true));
    bidaks.add(new Rook(whiteRook, 7, 7, true));
    bidaks.add(new Rook(blackRook, 0, 0, false));
    bidaks.add(new Rook(blackRook, 7, 0, false));
    // Knights
    bidaks.add(new Knight(whiteKnight, 1, 7, true));
    bidaks.add(new Knight(whiteKnight, 6, 7, true));
    bidaks.add(new Knight(blackKnight, 1, 0, false));
    bidaks.add(new Knight(blackKnight, 6, 0, false));
    // Bishops
    bidaks.add(new Bishop(whiteBishop, 2, 7, true));
    bidaks.add(new Bishop(whiteBishop, 5, 7, true));
    bidaks.add(new Bishop(blackBishop, 2, 0, false));
    bidaks.add(new Bishop(blackBishop, 5, 0, false));
    // Queens
    bidaks.add(new Queen(whiteQueen, 3, 7, true));
    bidaks.add(new Queen(blackQueen, 3, 0, false));
    // Kings
    bidaks.add(new King(whiteKing, 4, 7, true));
    bidaks.add(new King(blackKing, 4, 0, false));
  }

  public Bidak getBidakAt(int col, int row) {
    for (Bidak b : bidaks) {
      if (!b.isCaptured() && b.getCol() == col && b.getRow() == row)
        return b;
    }
    return null;
  }

  public Bidak[] getAllBidaks() {
    return bidaks.toArray(new Bidak[0]);
  }

  public void cleanup() {
    // UI ONLY CHANGE: Jangan hapus bidak yang captured dari list.
    // Alasannya: fitur Undo/Redo butuh referensi bidak yang pernah dimakan untuk bisa di-revive.
    // Rendering & interaksi tetap aman karena getBidakAt() sudah memfilter !isCaptured.
  }

  // --------------------------
  // NEW: return the image icon for any Bidak (BufferedImage)
  // --------------------------
  public BufferedImage getIconForPiece(Bidak b) {
    if (b == null)
      return null;
    boolean white = b.isWhite();

    if (white) {
      if (b instanceof Pawn)
        return whitePawn;
      if (b instanceof Rook)
        return whiteRook;
      if (b instanceof Knight)
        return whiteKnight;
      if (b instanceof Bishop)
        return whiteBishop;
      if (b instanceof Queen)
        return whiteQueen;
      if (b instanceof King)
        return whiteKing;
    } else {
      if (b instanceof Pawn)
        return blackPawn;
      if (b instanceof Rook)
        return blackRook;
      if (b instanceof Knight)
        return blackKnight;
      if (b instanceof Bishop)
        return blackBishop;
      if (b instanceof Queen)
        return blackQueen;
      if (b instanceof King)
        return blackKing;
    }
    return null;
  }

  // --------------------------
  // NEW: promote pawn using a revived piece from graveyard
  // --------------------------
  public void promotePawnFromGrave(Pawn pawn, Bidak revivedPiece) {
    if (pawn == null || revivedPiece == null)
      return;

    // Ensure pawn removed and revived put on board at pawn position
    // Remove pawn
    bidaks.remove(pawn);

    // revive piece
    revivedPiece.setCaptured(false);
    revivedPiece.setPosition(pawn.getCol(), pawn.getRow());

    // add revived to board pieces (avoid duplicates)
    if (!bidaks.contains(revivedPiece)) {
      bidaks.add(revivedPiece);
    }
  }

  public BufferedImage getWhiteTurnIcon() {
    return whitePawn; // pakai ikon bidak pawn putih
  }

  public BufferedImage getBlackTurnIcon() {
    return blackPawn; // ikon pawn hitam
  }

  public void draw(Graphics2D g2) {
    for (Bidak b : bidaks) {
      b.draw(g2);
    }
  }
}
