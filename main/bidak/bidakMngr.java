package main.bidak;

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
    for (int i = 0; i < 8; i++) {
      bidaks[i] = new bidak(whitePawn, i, 6);
    }

    // pawn hitam row 1
    for (int i = 0; i < 8; i++) {
      bidaks[i + 8] = new bidak(blackPawn, i, 1);
    }

    // ===== ROOK =====
    bidaks[index++] = new bidak(whiteRook, 0, 7);
    bidaks[index++] = new bidak(whiteRook, 7, 7);

    bidaks[index++] = new bidak(blackRook, 0, 0);
    bidaks[index++] = new bidak(blackRook, 7, 0);
    // ===== KNIGHT =====
    bidaks[index++] = new bidak(whiteKnight, 1, 7);
    bidaks[index++] = new bidak(whiteKnight, 6, 7);

    bidaks[index++] = new bidak(blackKnight, 1, 0);
    bidaks[index++] = new bidak(blackKnight, 6, 0);

    // ===== BISHOP =====
    bidaks[index++] = new bidak(whiteBishop, 2, 7);
    bidaks[index++] = new bidak(whiteBishop, 5, 7);
    bidaks[index++] = new bidak(blackBishop, 2, 0);
    bidaks[index++] = new bidak(blackBishop, 5, 0);

    // ===== QUEEN (di kolom 3) =====
    bidaks[index++] = new bidak(whiteQueen, 3, 7);
    bidaks[index++] = new bidak(blackQueen, 3, 0);
    // ===== KING (di kolom 4) =====
    bidaks[index++] = new bidak(whiteKing, 4, 7);
    bidaks[index++] = new bidak(blackKing, 4, 0);
  }

  int index = 16;

  public void draw(Graphics2D g2) {
    for (bidak p : bidaks) {
      if (p != null) {
        p.draw(g2);
      }
    }
  }
}
