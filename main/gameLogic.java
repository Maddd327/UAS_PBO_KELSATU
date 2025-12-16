package main;

import bidak.Bidak;
import bidak.BidakMngr;
import bidak.King;
import bidak.Pawn;
import java.util.ArrayList;
import java.util.List;

public class GameLogic {

    private final BidakMngr manager;
    private boolean whiteTurn = true; // berubah setiap turn, jadi tidak final
    private final List<Bidak> graveyardWhite = new ArrayList<>();
    private final List<Bidak> graveyardBlack = new ArrayList<>();
    // ========================
    // UNDO / REDO
    // ========================
    private static class Move {
        final Bidak piece;
        final int fromCol, fromRow;
        final int toCol, toRow;
        final Bidak captured;

        Move(Bidak piece, int fromCol, int fromRow, int toCol, int toRow, Bidak captured) {
            this.piece = piece;
            this.fromCol = fromCol;
            this.fromRow = fromRow;
            this.toCol = toCol;
            this.toRow = toRow;
            this.captured = captured;
        }
    }

    private final java.util.ArrayDeque<Move> undoStack = new java.util.ArrayDeque<>();
    private final java.util.ArrayDeque<Move> redoStack = new java.util.ArrayDeque<>();

    public boolean undo() {
        if (undoStack.isEmpty()) {
            return false;
        }

        final Move m = undoStack.pop();

        // balikkan posisi bidak yang bergerak
        m.piece.setPosition(m.fromCol, m.fromRow);

        // hidupkan kembali bidak yang tertangkap (jika ada)
        if (m.captured != null) {
            m.captured.setCaptured(false);
            // hapus dari graveyard jika sempat dimasukkan
            if (m.captured.isWhite()) {
                graveyardWhite.remove(m.captured);
            } else {
                graveyardBlack.remove(m.captured);
            }
        }

        // balik giliran
        whiteTurn = !whiteTurn;

        // setelah undo, move ini bisa di-redo
        redoStack.push(m);

        // rapikan state jika ada logic cleanup
        manager.cleanup();
        return true;
    }

    public boolean redo() {
        if (redoStack.isEmpty()) {
            return false;
        }

        final Move m = redoStack.pop();

        // jalankan lagi gerakan
        if (m.captured != null) {
            capture(m.captured);
        }
        m.piece.setPosition(m.toCol, m.toRow);

        // balik giliran lagi (karena redo = apply move)
        whiteTurn = !whiteTurn;

        undoStack.push(m);
        manager.cleanup();
        return true;
    }

    private void pushHistory(final Bidak piece, final int fromCol, final int fromRow,
                             final int toCol, final int toRow, final Bidak captured) {
        undoStack.push(new Move(piece, fromCol, fromRow, toCol, toRow, captured));
        redoStack.clear();
    }


    public GameLogic(final BidakMngr manager) {
        this.manager = manager;
    }

    // ========================
    // TURN MANAGEMENT
    // ========================
    public boolean isWhiteTurn() {
        return whiteTurn;
    }

    private void endTurn() {
        whiteTurn = !whiteTurn;
    }

    private boolean isTurnValid(final Bidak b) {
        return b != null && b.isWhite() == whiteTurn;
    }

    // ========================
    // MOVE CALCULATION
    // ========================
    public List<int[]> getPossibleMoves(final Bidak b) {
        if (b == null) {
            return new ArrayList<>();
        }
        return b.getPossibleMoves(manager.getAllBidaks());
    }

    private boolean isMoveValid(final Bidak b, final int col, final int row) {
        return getLegalMoves(b).stream()
                .anyMatch(m -> m[0] == col && m[1] == row);
    }

    // ========================
    // MAIN MOVE LOGIC
    // ========================
    public King findKing(boolean isWhite) {
        for (Bidak b : manager.getAllBidaks()) {
            if (!b.isCaptured() && b instanceof King && b.isWhite() == isWhite) {
                return (King) b;
            }
        }
        return null;
    }
    
    // ========================
    // LEGAL MOVE FILTERING (CHECK SAFETY)
    // ========================
    /**
     * @return true kalau sisi yang sedang jalan (whiteTurn) sedang skak.
     */
    public boolean isCurrentSideInCheck() {
        return isInCheck(whiteTurn);
    }

    /**
     * Simulasi langkah: kalau bidak dipindah ke (targetCol,targetRow),
     * apakah raja sendiri jadi/masih skak?
     */
    public boolean wouldLeaveOwnKingInCheck(final Bidak piece, final int targetCol, final int targetRow) {
        if (piece == null) return true;

        final int oldCol = piece.getCol();
        final int oldRow = piece.getRow();

        final Bidak target = manager.getBidakAt(targetCol, targetRow);
        final boolean targetWasCaptured = target != null && target.isCaptured();

        // simulasi
        if (target != null) {
            target.setCaptured(true);
        }
        piece.setPosition(targetCol, targetRow);

        final boolean stillInCheck = isInCheck(piece.isWhite());

        // rollback
        piece.setPosition(oldCol, oldRow);
        if (target != null) {
            target.setCaptured(targetWasCaptured);
        }

        return stillInCheck;
    }

    /**
     * Semua langkah sah (sudah disaring agar tidak meninggalkan raja sendiri dalam kondisi skak).
     * Format: int[]{col,row}
     */
    public List<int[]> getLegalMoves(final Bidak piece) {
        final List<int[]> legal = new ArrayList<>();
        if (piece == null || piece.isCaptured()) return legal;

        for (final int[] m : piece.getPossibleMoves(manager.getAllBidaks())) {
            if (!wouldLeaveOwnKingInCheck(piece, m[0], m[1])) {
                legal.add(new int[]{m[0], m[1]});
            }
        }
        return legal;
    }

public boolean tryMove(final Bidak b, final int targetCol, final int targetRow) {
        if (!isTurnValid(b) || !isMoveValid(b, targetCol, targetRow)) {
            return false;
        }

        // Jangan izinkan langkah yang membuat raja sendiri tetap/menjadi skak
        if (wouldLeaveOwnKingInCheck(b, targetCol, targetRow)) {
            final King k = findKing(b.isWhite());
            if (k != null) {
                k.shakeRed();
            }
            return false;
        }

        final Bidak target = manager.getBidakAt(targetCol, targetRow);
        final int oldCol = b.getCol();
        final int oldRow = b.getRow();
        boolean targetWasCaptured = false;

        if (target != null) {
            targetWasCaptured = target.isCaptured();
            target.setCaptured(true); // sementara saja
        }
        b.setPosition(targetCol, targetRow);

        boolean stillInCheck = isInCheck(b.isWhite());

        // rollback setelah simulasi
        b.setPosition(oldCol, oldRow);
        if (target != null) {
            target.setCaptured(targetWasCaptured);
        }

        // jika masih skak, shake raja
        if (stillInCheck) {
            King king = findKing(b.isWhite());
            if (king != null) {
                king.shakeRed();
            }
            return false;
        }

        // ======================
        // JALANKAN GERAKAN SEBENARNYA
        // ======================
        if (target != null) {
            capture(target); // baru benar-benar ditangkap

                }b.setPosition(targetCol, targetRow);

        // simpan history untuk Undo/Redo
        pushHistory(b, oldCol, oldRow, targetCol, targetRow, target);


        // pawn promotion (legacy fallback)
        if (b instanceof Pawn) {
            final int lastRow = b.isWhite() ? 0 : 7;
            if (b.getRow() == lastRow) {
                // overlay handled elsewhere
            }
        }

        endTurn();
        manager.cleanup();
        final boolean moverIsWhite = !whiteTurn;

        final boolean opponentWhite = isWhiteTurn();
        if (isInCheck(opponentWhite)) {
            final King checkedKing = findKing(opponentWhite);
            if (checkedKing != null) {
                checkedKing.shakeRed();
            }
            if (isCheckmate(opponentWhite)) {
                final String winner = moverIsWhite ? "Putih" : "Hitam";
                javax.swing.SwingUtilities.invokeLater(() -> {
                    javax.swing.JOptionPane.showMessageDialog(null,
                            "Checkmate! Pemenang: " + winner,
                            "Game Over",
                            javax.swing.JOptionPane.INFORMATION_MESSAGE);
                });
            } else {
                javax.swing.SwingUtilities.invokeLater(() -> {
                    javax.swing.JOptionPane.showMessageDialog(null,
                            "Skak pada " + (opponentWhite ? "Putih" : "Hitam") + "!",
                            "Check",
                            javax.swing.JOptionPane.INFORMATION_MESSAGE);
                });
            }
        }

        return true;
    }

    // ========================
    // CAPTURE
    // ========================
    private void capture(final Bidak target) {
        target.setCaptured(true);
        if (target.isWhite()) {
            graveyardWhite.add(target); 
        }else {
            graveyardBlack.add(target);
        }
    }

    // ========================
    // CHECK & CHECKMATE
    // ========================
    public boolean isInCheck(final boolean whiteKing) {
        Bidak king = null;
        for (final Bidak b : manager.getAllBidaks()) {
            if (!b.isCaptured() && b instanceof King && b.isWhite() == whiteKing) {
                king = b;
                break;
            }
        }
        if (king == null) {
            return false;
        }

        for (final Bidak b : manager.getAllBidaks()) {
            if (!b.isCaptured() && b.isWhite() != whiteKing) {
                for (final int[] m : b.getPossibleMoves(manager.getAllBidaks())) {
                    if (m[0] == king.getCol() && m[1] == king.getRow()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void setInitialTurn(boolean whiteStarts) {
        this.whiteTurn = whiteStarts;
    }

    public boolean isCheckmate(final boolean whiteKing) {
        if (!isInCheck(whiteKing)) {
            return false;
        }

        for (final Bidak b : manager.getAllBidaks()) {
            if (b.isCaptured() || b.isWhite() != whiteKing) {
                continue;
            }

            for (final int[] m : b.getPossibleMoves(manager.getAllBidaks())) {
                final int oldCol = b.getCol();
                final int oldRow = b.getRow();
                final Bidak target = manager.getBidakAt(m[0], m[1]);
                boolean targetWasCaptured = false;

                if (target != null) {
                    targetWasCaptured = target.isCaptured();
                    target.setCaptured(true);
                }

                b.setPosition(m[0], m[1]);
                final boolean stillInCheck = isInCheck(whiteKing);

                // rollback
                b.setPosition(oldCol, oldRow);
                if (target != null) {
                    target.setCaptured(targetWasCaptured);
                }

                if (!stillInCheck) {
                    return false;
                }
            }
        }

        return true;
    }

    // ========================
    // GRAVEYARD
    // ========================
    public List<Bidak> getGraveyardWhite() {
        return graveyardWhite;
    }

    public List<Bidak> getGraveyardBlack() {
        return graveyardBlack;
    }

    public List<Bidak> getWhiteGraveyard() {
        return getGraveyardWhite();
    }

    public List<Bidak> getBlackGraveyard() {
        return getGraveyardBlack();
    }

    public boolean hasPromotionPiece(final List<Bidak> graveyard) {
        if (graveyard == null) {
            return false;
        }
        for (final Bidak b : graveyard) {
            if (b instanceof bidak.Queen || b instanceof bidak.Rook || b instanceof bidak.Bishop
                    || b instanceof bidak.Knight) {
                return true;
            }
        }
        return false;
    }

    public boolean isPromotionRank(final Pawn pawn) {
        if (pawn == null) {
            return false;
        }
        return pawn.isWhite() ? pawn.getRow() == 0 : pawn.getRow() == 7;
    }

    public void reviveFromGrave(final Bidak piece) {
        if (piece == null) {
            return;
        }
        if (piece.isWhite()) {
            graveyardWhite.remove(piece);
        } else {
            graveyardBlack.remove(piece);
        }
        piece.setCaptured(false);
    }
}