package bidak;

import java.awt.image.BufferedImage;
import java.util.List;

public class Queen extends Bidak {
    private static final int[][] DIRS = {
        {1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}
    };

    public Queen(BufferedImage img, int col, int row, boolean isWhite) {
        super(img, col, row, isWhite);
    }

    @Override
    public List<int[]> getPossibleMoves(Bidak[] allBidaks) {
        return slideMoves(DIRS, allBidaks);
    }

    @Override
    public String getName() {
        return isWhite ? "Queen Putih" : "Queen Hitam";
    }
}
