package bidak;

import main.Papan;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public abstract class Bidak {
    protected BufferedImage img;
    protected int col, row;
    protected boolean isWhite;
    protected boolean selected;
    protected boolean captured;
    protected boolean hasMoved;

    public Bidak(BufferedImage img, int col, int row, boolean isWhite) {
        this.img = img;
        this.col = col;
        this.row = row;
        this.isWhite = isWhite;
        this.selected = false;
        this.captured = false;
        this.hasMoved = false;
    }

    // Getter/Setter
    public BufferedImage getImage() {
        return img;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean s) {
        selected = s;
    }

    public boolean isCaptured() {
        return captured;
    }

    public void capture() {
        captured = true;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setMoved(boolean moved) {
        hasMoved = moved;
    }

    public void setPosition(int c, int r) {
        col = c;
        row = r;
        hasMoved = true;
    }

    public void draw(Graphics2D g2) {
        if (captured)
            return;
        g2.drawImage(img, col * Papan.KOTAK_SIZE, row * Papan.KOTAK_SIZE,
                Papan.KOTAK_SIZE, Papan.KOTAK_SIZE, null);
        if (selected) {
            g2.setColor(Color.YELLOW);
            g2.setStroke(new BasicStroke(3));
            g2.drawRect(col * Papan.KOTAK_SIZE, row * Papan.KOTAK_SIZE,
                    Papan.KOTAK_SIZE, Papan.KOTAK_SIZE);
        }
    }

    public abstract List<int[]> getPossibleMoves(Bidak[] allBidaks);

    public abstract String getName();

    public void setCaptured(boolean captured) {
        this.captured = captured;
    }

    // ================== HELPERS ==================
    protected boolean isWithinBoard(int c, int r) {
        return c >= 0 && c < 8 && r >= 0 && r < 8;
    }

    protected boolean isOccupied(int c, int r, Bidak[] all) {
        for (Bidak b : all)
            if (!b.isCaptured() && b.getCol() == c && b.getRow() == r)
                return true;
        return false;
    }

    protected boolean isEnemy(int c, int r, Bidak[] all) {
        for (Bidak b : all)
            if (!b.isCaptured() && b.getCol() == c && b.getRow() == r && b.isWhite() != this.isWhite)
                return true;
        return false;
    }

    protected void addStepMove(int c, int r, Bidak[] all, List<int[]> moves) {
        if (!isWithinBoard(c, r))
            return;
        if (!isOccupied(c, r, all) || isEnemy(c, r, all))
            moves.add(new int[] { c, r });
    }

    protected List<int[]> slideMoves(int[][] dirs, Bidak[] all) {
        List<int[]> moves = new ArrayList<>();
        for (int[] dir : dirs) {
            int c = col + dir[0], r = row + dir[1];
            while (isWithinBoard(c, r)) {
                if (isOccupied(c, r, all)) {
                    if (isEnemy(c, r, all))
                        moves.add(new int[] { c, r });
                    break;
                }
                moves.add(new int[] { c, r });
                c += dir[0];
                r += dir[1];
            }
        }
        return moves;
    }
}
