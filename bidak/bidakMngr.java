package bidak;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class bidakMngr {

  bidak[] bidaks = new bidak[32];

  BufferedImage whitePawn, whiteRook, whiteKnight, whiteBishop, whiteQueen, whiteKing;
  BufferedImage blackPawn, blackRook, blackKnight, blackBishop, blackQueen, blackKing;

  public bidakMngr() {
    loadImages();
    setPieces();
  }

  public BufferedImage getWhitePawnIcon() {
    // asumsi sudah dimuat di constructor atau loadImages()
    return this.whitePawn;
  }

  public BufferedImage getBlackPawnIcon() {
    return this.blackPawn;
  }

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

  private void setPieces() {
    // pawn putih row 6
    // ===== PAWN =====
    for (int i = 0; i < 8; i++) {
      bidaks[i] = new pawn(whitePawn, i, 6, true); // pawn putih
    }

    for (int i = 0; i < 8; i++) {
      bidaks[i + 8] = new pawn(blackPawn, i, 1, false); // pawn hitam
    }

    // ===== ROOK =====
    bidaks[index++] = new rook(whiteRook, 0, 7, true);
    bidaks[index++] = new rook(whiteRook, 7, 7, true);

    bidaks[index++] = new rook(blackRook, 0, 0, false);
    bidaks[index++] = new rook(blackRook, 7, 0, false);
    // ===== KNIGHT =====
    bidaks[index++] = new knight(whiteKnight, 1, 7, true);
    bidaks[index++] = new knight(whiteKnight, 6, 7, true);

    bidaks[index++] = new knight(blackKnight, 1, 0, false);
    bidaks[index++] = new knight(blackKnight, 6, 0, false);

    // ===== BISHOP =====
    bidaks[index++] = new bishop(whiteBishop, 2, 7, true);
    bidaks[index++] = new bishop(whiteBishop, 5, 7, true);
    bidaks[index++] = new bishop(blackBishop, 2, 0, false);
    bidaks[index++] = new bishop(blackBishop, 5, 0, false);

    // ===== QUEEN (di kolom 3) =====
    bidaks[index++] = new queen(whiteQueen, 3, 7, true);
    bidaks[index++] = new queen(blackQueen, 3, 0, false);
    // ===== KING (di kolom 4) =====
    bidaks[index++] = new king(whiteKing, 4, 7, true);
    bidaks[index++] = new king(blackKing, 4, 0, false);
  }

  int index = 16;

  public bidak selectBidakAt(int col, int row) {
    bidak clicked = null;
    for (bidak b : bidaks) {
      if (b != null) {
        b.selected = false; // reset semua bidak
        if (b.col == col && b.row == row) {
          b.selected = true;
          clicked = b;
        }
      }
    }
    return clicked; // kembalikan bidak yang diklik, bisa null
  }

  public bidak getBidakAt(int col, int row) {
    for (bidak b : bidaks) {
      if (b != null && b.col == col && b.row == row)
        return b;
    }
    return null;
  }

  public bidak[] getAllBidaks() {
    return bidaks;
  }

  public void draw(Graphics2D g2) {
    for (bidak b : bidaks) {
      if (b != null) {
        b.draw(g2);
      }
    }
  }
}
