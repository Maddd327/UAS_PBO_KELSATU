package bidak;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class rook extends bidak {
  public rook(BufferedImage img, int col, int row, boolean isWhite) {
    super(img, col, row, isWhite);
  }

  public List<int[]> getPossibleMoves(bidak[] allBidaks) {
    List<int[]> moves = new ArrayList<>();
    bidak[][] board = new bidak[8][8];
    for (bidak b : allBidaks)
      if (b != null)
        board[b.col][b.row] = b;

    int[] dx = { 0, 1, 0, -1 }; // atas, kanan, bawah, kiri
    int[] dy = { -1, 0, 1, 0 };

    for (int d = 0; d < 4; d++) {
      int nx = col + dx[d];
      int ny = row + dy[d];
      while (nx >= 0 && nx < 8 && ny >= 0 && ny < 8) {
        if (board[nx][ny] == null) {
          moves.add(new int[] { nx, ny });
        } else {
          if (board[nx][ny].isWhite != this.isWhite)
            moves.add(new int[] { nx, ny });
          break;
        }
        nx += dx[d];
        ny += dy[d];
      }
    }
    return moves;
  }
}
