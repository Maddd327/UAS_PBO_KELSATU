package bidak;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Knight extends Bidak {
  private static final int[][] MOVES = {
      { 1, 2 }, { 2, 1 }, { -1, 2 }, { -2, 1 }, { 1, -2 }, { 2, -1 }, { -1, -2 }, { -2, -1 }
  };

  public Knight(BufferedImage img, int col, int row, boolean isWhite) {
    super(img, col, row, isWhite);
  }

  @Override
  public List<int[]> getPossibleMoves(Bidak[] allBidaks) {
    List<int[]> moves = new ArrayList<>();
    for (int[] m : MOVES)
      addStepMove(col + m[0], row + m[1], allBidaks, moves);
    return moves;
  }

  @Override
  public String getName() {
    return isWhite ? "Knight Putih" : "Knight Hitam";
  }

}
