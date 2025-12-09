package bidak;

import java.awt.image.BufferedImage;
import java.util.List;

public class Rook extends Bidak {
    private static final int[][] DIRS = {{1,0},{-1,0},{0,1},{0,-1}};

    public Rook(BufferedImage img, int col, int row, boolean isWhite) {
        super(img, col, row, isWhite);
    }

    @Override
    public List<int[]> getPossibleMoves(Bidak[] allBidaks) {
        return slideMoves(DIRS, allBidaks);
    }

    @Override
    public String getName() {
        return isWhite ? "Rook Putih" : "Rook Hitam";
    }
}
