package main.bidak;

import main.Papan;
import java.util.ArrayList;
import java.util.List;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.image.BufferedImage;

public class pawn extends bidak {

  public pawn(BufferedImage img, int col, int row, boolean isWhite) {
    super(img, col, row, isWhite);
  }

  public List<int[]> getPossibleMoves(bidak[] allBidaks) {
    List<int[]> moves = new ArrayList<>();

    // buat papan 8x8 untuk cek posisi bidak
    bidak[][] board = new bidak[8][8];
    for (bidak b : allBidaks) {
      if (b != null)
        board[b.col][b.row] = b;
    }

    int dir = isWhite ? -1 : 1; // putih ke atas, hitam ke bawah
    int newRow = row + dir;

    // maju 1 langkah
    if (newRow >= 0 && newRow < 8 && board[col][newRow] == null) {
      moves.add(new int[] { col, newRow });

      // maju 2 langkah di posisi awal
      if ((isWhite && row == 6) || (!isWhite && row == 1)) {
        int twoRow = row + 2 * dir;
        if (board[col][twoRow] == null) {
          moves.add(new int[] { col, twoRow });
        }
      }
    }

    // serang diagonal
    for (int dcol = -1; dcol <= 1; dcol += 2) {
      int newCol = col + dcol;
      if (newCol >= 0 && newCol < 8 && newRow >= 0 && newRow < 8) {
        bidak target = board[newCol][newRow];
        if (target != null && target.isWhite != this.isWhite) {
          moves.add(new int[] { newCol, newRow });
        }
      }
    }

    return moves;
  }
}