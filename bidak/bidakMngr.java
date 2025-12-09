package bidak;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class BidakMngr {

    private final List<Bidak> bidaks = new ArrayList<>();

    // BufferedImages untuk setiap type bidak
    BufferedImage whitePawn, whiteRook, whiteKnight, whiteBishop, whiteQueen, whiteKing;
    BufferedImage blackPawn, blackRook, blackKnight, blackBishop, blackQueen, blackKing;

    public BidakMngr() {
        loadImages();
        setupPieces();
    }

    private void loadImages() {
        whitePawn = imageLoader.load("img/bidak/W_pawn.png");
        whiteRook = imageLoader.load("img/bidak/W_rook.png");
        whiteKnight = imageLoader.load("img/bidak/W_knight.png");
        whiteBishop = imageLoader.load("img/bidak/W_bishop.png");
        whiteQueen = imageLoader.load("img/bidak/W_queen.png");
        whiteKing = imageLoader.load("img/bidak/W_king.png");

        blackPawn = imageLoader.load("img/bidak/B_pawn.png");
        blackRook = imageLoader.load("img/bidak/B_rook.png");
        blackKnight = imageLoader.load("img/bidak/B_knight.png");
        blackBishop = imageLoader.load("img/bidak/B_bishop.png");
        blackQueen = imageLoader.load("img/bidak/B_queen.png");
        blackKing = imageLoader.load("img/bidak/B_king.png");
    }

    private void setupPieces() {
        // Pawns
        for (int i = 0; i < 8; i++) {
            bidaks.add(new Pawn(whitePawn, i, 6, true));
            bidaks.add(new Pawn(blackPawn, i, 1, false));
        }
        // Rooks
        bidaks.add(new Rook(whiteRook, 0, 7, true));
        bidaks.add(new Rook(whiteRook, 7, 7, true));
        bidaks.add(new Rook(blackRook, 0, 0, false));
        bidaks.add(new Rook(blackRook, 7, 0, false));

        // Knights
        bidaks.add(new Knight(whiteKnight, 1, 7, true));
        bidaks.add(new Knight(whiteKnight, 6, 7, true));
        bidaks.add(new Knight(blackKnight, 1, 0, false));
        bidaks.add(new Knight(blackKnight, 6, 0, false));

        // Bishops
        bidaks.add(new Bishop(whiteBishop, 2, 7, true));
        bidaks.add(new Bishop(whiteBishop, 5, 7, true));
        bidaks.add(new Bishop(blackBishop, 2, 0, false));
        bidaks.add(new Bishop(blackBishop, 5, 0, false));

        // Queens
        bidaks.add(new Queen(whiteQueen, 3, 7, true));
        bidaks.add(new Queen(blackQueen, 3, 0, false));

        // Kings
        bidaks.add(new King(whiteKing, 4, 7, true));
        bidaks.add(new King(blackKing, 4, 0, false));
    }

    // ===================================
    // FUNGSI TAMBAHAN: DIPERLUKAN OLEH GameLogic.handlePromotion()
    // ===================================
    public void addBidak(Bidak b) {
        bidaks.add(b);
    }

    public void removeBidak(Bidak b) {
        bidaks.remove(b);
    }

    // ===================================
    // UTILITY
    // ===================================
    public Bidak getBidakAt(int col, int row) {
        for (Bidak b : bidaks) {
            if (!b.isCaptured() && b.getCol() == col && b.getRow() == row)
                return b;
        }
        return null;
    }

    public Bidak[] getAllBidaks() {
        return bidaks.toArray(new Bidak[0]);
    }

    public void cleanup() {
        bidaks.removeIf(Bidak::isCaptured);
    }

    // ===================================
    // PROMOTION LAMA (TIDAK DIGUNAKAN)
    // ===================================
    @Deprecated
    public void promotePawn(Pawn pawn) {
        // DISABLED: Logika promosi sekarang ada di GameLogic.handlePromotion()
        System.out.println("promotePawn() tidak digunakan lagi. Promosi ditangani oleh GameLogic.");
    }

    // ===================================
    // DRAWING
    // ===================================
    public BufferedImage getWhiteTurnIcon() {
        return whitePawn;
    }

    public BufferedImage getBlackTurnIcon() {
        return blackPawn;
    }

    public void draw(Graphics2D g2) {
        for (Bidak b : bidaks) {
            b.draw(g2);
        }
    }
}
