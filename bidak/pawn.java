package bidak;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Pawn extends Bidak {
  private int lastDoubleStepTurn = -1; // untuk en passant

  public Pawn(BufferedImage img, int col, int row, boolean isWhite) {
    super(img, col, row, isWhite);
  }

  public void setLastDoubleStepTurn(int turn) {
    lastDoubleStepTurn = turn;
  }

  public int getLastDoubleStepTurn() {
    return lastDoubleStepTurn;
  }

  @Override
  public List<int[]> getPossibleMoves(Bidak[] all) {
    List<int[]> moves = new ArrayList<>();
    int dir = isWhite ? -1 : 1;
    int startRow = isWhite ? 6 : 1;

    // maju 1
    if (!isOccupied(col, row + dir, all))
      moves.add(new int[] { col, row + dir });
    // maju 2
    if (row == startRow && !isOccupied(col, row + dir, all) && !isOccupied(col, row + 2 * dir, all))
      moves.add(new int[] { col, row + 2 * dir });
    // serang diagonal
    if (isWithinBoard(col - 1, row + dir) && isEnemy(col - 1, row + dir, all))
      moves.add(new int[] { col - 1, row + dir });
    if (isWithinBoard(col + 1, row + dir) && isEnemy(col + 1, row + dir, all))
      moves.add(new int[] { col + 1, row + dir });

    // TODO: en passant (cek lastDoubleStepTurn)

    return moves;
  }

  @Override
  public String getName() {
    return isWhite ? "Pawn Putih" : "Pawn Hitam";
  }
}
