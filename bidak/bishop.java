package bidak;

import java.awt.image.BufferedImage;
import java.util.List;

public class Bishop extends Bidak {
  private static final int[][] DIRS = { { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } };

  public Bishop(BufferedImage img, int col, int row, boolean isWhite) {
    super(img, col, row, isWhite);
  }

  @Override
  public List<int[]> getPossibleMoves(Bidak[] allBidaks) {
    return slideMoves(DIRS, allBidaks);
  }

  @Override
  public String getName() {
    return isWhite ? "Bishop Putih" : "Bishop Hitam";
  }
}
