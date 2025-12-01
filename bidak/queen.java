package bidak;

import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;

public class queen extends bidak {

  public queen(BufferedImage img, int col, int row, boolean isWhite) {
    super(img, col, row, isWhite);
  }

  @Override
  public List<int[]> getPossibleMoves(bidak[] allBidaks) {
    List<int[]> moves = new ArrayList<>();
    moves.addAll(new rook(this.img, this.col, this.row, this.isWhite).getPossibleMoves(allBidaks));
    moves.addAll(new bishop(this.img, this.col, this.row, this.isWhite).getPossibleMoves(allBidaks));
    return moves;
  }
}
