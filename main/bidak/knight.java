package main.bidak;

import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;

public class knight extends bidak {

  public knight(BufferedImage img, int col, int row, boolean isWhite) {
    super(img, col, row, isWhite);
  }

  @Override
  public List<int[]> getPossibleMoves(bidak[] allBidaks) {
    List<int[]> moves = new ArrayList<>();
    bidak[][] board = new bidak[8][8];
    for (bidak b : allBidaks)
      if (b != null)
        board[b.col][b.row] = b;

    int[][] offsets = { { 1, 2 }, { 2, 1 }, { -1, 2 }, { -2, 1 }, { 1, -2 }, { 2, -1 }, { -1, -2 }, { -2, -1 } };
    for (int[] off : offsets) {
      int newCol = col + off[0];
      int newRow = row + off[1];
      if (newCol >= 0 && newCol < 8 && newRow >= 0 && newRow < 8) {
        if (board[newCol][newRow] == null || board[newCol][newRow].isWhite != this.isWhite) {
          moves.add(new int[] { newCol, newRow });
        }
      }
    }
    return moves;
  }
}
