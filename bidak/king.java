package bidak;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class King extends Bidak {
  private static final int[][] MOVES = {
      { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }, { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 }
  };

  public King(BufferedImage img, int col, int row, boolean isWhite) {
    super(img, col, row, isWhite);
  }

  @Override
  public List<int[]> getPossibleMoves(Bidak[] all) {
    List<int[]> moves = new ArrayList<>();
    for (int[] m : MOVES)
      addStepMove(col + m[0], row + m[1], all, moves);

    // castling
    // TODO: cek hasMoved rook & king, jalur kosong, raja tidak in check
    return moves;
  }

  @Override
  public String getName() {
    return isWhite ? "King Putih" : "King Hitam";
  }
}
