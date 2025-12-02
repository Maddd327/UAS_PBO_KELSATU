package bidak;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class bidakMngr {

  // list dinamis (GANTI array 32)
  private final List<bidak> listBidak = new ArrayList<>();

  BufferedImage whitePawn, whiteRook, whiteKnight, whiteBishop, whiteQueen, whiteKing;
  BufferedImage blackPawn, blackRook, blackKnight, blackBishop, blackQueen, blackKing;

  public bidakMngr() {
    loadImages();
    setPieces();
  }

  public BufferedImage getWhitePawnIcon() {
    return whitePawn;
  }

  public BufferedImage getBlackPawnIcon() {
    return blackPawn;
  }

  // ===========================================================
  // LOAD ASSET
  // ===========================================================
  private void loadImages() {
    whitePawn = imageLoader.load("img/bidak/W_pawn.png");
    whiteRook = imageLoader.load("img/bidak/W_rook.png");
    whiteKnight = imageLoader.load("img/bidak/W_knight.png");
    whiteBishop = imageLoader.load("img/bidak/W_bishop.png");
    whiteQueen = imageLoader.load("img/bidak/W_queen.png");
    whiteKing = imageLoader.load("img/bidak/W_king.png");

    blackPawn = imageLoader.load("img/bidak/B_pawn.png");
    blackRook = imageLoader.load("img/bidak/B_rook.png");
    blackKnight = imageLoader.load("img/bidak/B_knight.png");
    blackBishop = imageLoader.load("img/bidak/B_bishop.png");
    blackQueen = imageLoader.load("img/bidak/B_queen.png");
    blackKing = imageLoader.load("img/bidak/B_king.png");
  }

  // ===========================================================
  // BUAT BIDAK
  // ===========================================================
  private void setPieces() {

    // === PAWN ===
    for (int i = 0; i < 8; i++)
      listBidak.add(new pawn(whitePawn, i, 6, true));

    for (int i = 0; i < 8; i++)
      listBidak.add(new pawn(blackPawn, i, 1, false));

    // === ROOK ===
    listBidak.add(new rook(whiteRook, 0, 7, true));
    listBidak.add(new rook(whiteRook, 7, 7, true));

    listBidak.add(new rook(blackRook, 0, 0, false));
    listBidak.add(new rook(blackRook, 7, 0, false));

    // === KNIGHT ===
    listBidak.add(new knight(whiteKnight, 1, 7, true));
    listBidak.add(new knight(whiteKnight, 6, 7, true));

    listBidak.add(new knight(blackKnight, 1, 0, false));
    listBidak.add(new knight(blackKnight, 6, 0, false));

    // === BISHOP ===
    listBidak.add(new bishop(whiteBishop, 2, 7, true));
    listBidak.add(new bishop(whiteBishop, 5, 7, true));

    listBidak.add(new bishop(blackBishop, 2, 0, false));
    listBidak.add(new bishop(blackBishop, 5, 0, false));

    // === QUEEN ===
    listBidak.add(new queen(whiteQueen, 3, 7, true));
    listBidak.add(new queen(blackQueen, 3, 0, false));

    // === KING ===
    listBidak.add(new king(whiteKing, 4, 7, true));
    listBidak.add(new king(blackKing, 4, 0, false));
  }

  // ===========================================================
  // SELEKSI & AMBIL BIDAK
  // ===========================================================
  public bidak selectBidakAt(int col, int row) {
    bidak clicked = null;

    for (bidak b : listBidak) {
      if (b.alive && !b.captured) {
        b.selected = false;
        if (b.col == col && b.row == row) {
          b.selected = true;
          clicked = b;
        }
      }
    }

    return clicked;
  }

  public bidak getBidakAt(int col, int row) {
    for (bidak b : listBidak) {
      if (b.alive && !b.captured && b.col == col && b.row == row)
        return b;
    }
    return null;
  }

  public bidak[] getAllBidaks() {
    return listBidak.toArray(new bidak[0]);
  }

  // ===========================================================
  // HAPUS BIDAK YANG SUDAH DI-MAKAN
  // ===========================================================
  public void cleanup() {
    listBidak.removeIf(b -> b.captured);
  }

  // ===========================================================
  // RENDER
  // ===========================================================
  public void draw(Graphics2D g2) {
    for (bidak b : listBidak) {
      if (b.alive && !b.captured)
        b.draw(g2);
    }
  }
}
