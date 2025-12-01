package bidak;

import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;

public class bishop extends bidak {

  public bishop(BufferedImage img, int col, int row, boolean isWhite) {
    super(img, col, row, isWhite);
  }

  @Override
  public List<int[]> getPossibleMoves(bidak[] allBidaks) {
    List<int[]> moves = new ArrayList<>();
    bidak[][] board = new bidak[8][8];
    for (bidak b : allBidaks)
      if (b != null)
        board[b.col][b.row] = b;

    int[][] directions = { { -1, -1 }, { -1, 1 }, { 1, -1 }, { 1, 1 } };
    for (int[] d : directions) {
      int newCol = col;
      int newRow = row;
      while (true) {
        newCol += d[0];
        newRow += d[1];
        if (newCol < 0 || newCol >= 8 || newRow < 0 || newRow >= 8)
          break;
        if (board[newCol][newRow] == null) {
          moves.add(new int[] { newCol, newRow });
        } else {
          if (board[newCol][newRow].isWhite != this.isWhite)
            moves.add(new int[] { newCol, newRow });
          break;
        }
      }
    }
    return moves;
  }
}
