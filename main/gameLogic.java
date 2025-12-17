package main;

import bidak.Bidak;
import bidak.BidakMngr;
import bidak.King;
import bidak.Pawn;
import bidak.Rook;
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

        // status hasMoved sebelum move (penting untuk undo/redo castling)
        final boolean pieceMovedBefore;
        final boolean capturedWasCapturedBefore;

        // data rook jika move adalah castling (rokade)
        final Bidak rook;          // null jika bukan castling
        final int rookFromCol, rookFromRow;
        final int rookToCol, rookToRow;
        final boolean rookMovedBefore;

        Move(Bidak piece, int fromCol, int fromRow, int toCol, int toRow,
             Bidak captured,
             boolean pieceMovedBefore,
             boolean capturedWasCapturedBefore,
             Bidak rook, int rookFromCol, int rookFromRow, int rookToCol, int rookToRow,
             boolean rookMovedBefore) {
            this.piece = piece;
            this.fromCol = fromCol;
            this.fromRow = fromRow;
            this.toCol = toCol;
            this.toRow = toRow;
            this.captured = captured;

            this.pieceMovedBefore = pieceMovedBefore;
            this.capturedWasCapturedBefore = capturedWasCapturedBefore;

            this.rook = rook;
            this.rookFromCol = rookFromCol;
            this.rookFromRow = rookFromRow;
            this.rookToCol = rookToCol;
            this.rookToRow = rookToRow;
            this.rookMovedBefore = rookMovedBefore;
        }

        boolean isCastling() {
            return rook != null;
        }
    }

    private final java.util.ArrayDeque<Move> undoStack = new java.util.ArrayDeque<>();
    private final java.util.ArrayDeque<Move> redoStack = new java.util.ArrayDeque<>();

    public boolean undo() {
        if (undoStack.isEmpty()) {
            return false;
        }

        final Move m = undoStack.pop();

        // restore posisi piece tanpa mengubah hasMoved, lalu kembalikan hasMoved ke nilai sebelum move
        m.piece.setPositionNoMoveFlag(m.fromCol, m.fromRow);
        m.piece.setMoved(m.pieceMovedBefore);

        // jika castling: restore rook juga
        if (m.isCastling()) {
            m.rook.setPositionNoMoveFlag(m.rookFromCol, m.rookFromRow);
            m.rook.setMoved(m.rookMovedBefore);
        }

        // hidupkan kembali bidak yang tertangkap (jika ada)
        if (m.captured != null) {
            m.captured.setCaptured(m.capturedWasCapturedBefore);
            // hapus dari graveyard jika sempat dimasukkan
            if (!m.capturedWasCapturedBefore) { // berarti tadi benar-benar ditangkap saat move
                if (m.captured.isWhite()) {
                    graveyardWhite.remove(m.captured);
                } else {
                    graveyardBlack.remove(m.captured);
                }
            }
        }

        // balik giliran
        whiteTurn = !whiteTurn;

        // setelah undo, move ini bisa di-redo
        redoStack.push(m);

        // rapikan state
        manager.cleanup();
        return true;
    }

    public boolean redo() {
        if (redoStack.isEmpty()) {
            return false;
        }

        final Move m = redoStack.pop();

        // apply lagi capture (kalau ada)
        if (m.captured != null && !m.capturedWasCapturedBefore) {
            capture(m.captured);
        }

        // gerakkan piece lagi
        m.piece.setPositionNoMoveFlag(m.toCol, m.toRow);
        m.piece.setMoved(true);

        // jika castling: gerakkan rook lagi
        if (m.isCastling()) {
            m.rook.setPositionNoMoveFlag(m.rookToCol, m.rookToRow);
            m.rook.setMoved(true);
        }

        // balik giliran lagi (karena redo = apply move)
        whiteTurn = !whiteTurn;

        undoStack.push(m);
        manager.cleanup();
        return true;
    }

    private void pushHistory(final Bidak piece, final int fromCol, final int fromRow,
                             final int toCol, final int toRow,
                             final Bidak captured,
                             final boolean pieceMovedBefore,
                             final boolean capturedWasCapturedBefore,
                             final Bidak rook, final int rookFromCol, final int rookFromRow,
                             final int rookToCol, final int rookToRow,
                             final boolean rookMovedBefore) {
        undoStack.push(new Move(piece, fromCol, fromRow, toCol, toRow,
                captured,
                pieceMovedBefore,
                capturedWasCapturedBefore,
                rook, rookFromCol, rookFromRow, rookToCol, rookToRow,
                rookMovedBefore));
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
        final boolean pieceMovedBefore = piece.hasMoved();

        final Bidak target = manager.getBidakAt(targetCol, targetRow);
        final boolean targetWasCaptured = target != null && target.isCaptured();

        // data rook untuk simulasi castling (jika perlu)
        Bidak rook = null;
        int rookOldCol = -1, rookOldRow = -1, rookNewCol = -1, rookNewRow = -1;
        boolean rookMovedBefore = false;

        final boolean isCastlingAttempt = (piece instanceof King) && (Math.abs(targetCol - oldCol) == 2) && (targetRow == oldRow);

        // simulasi
        if (target != null) {
            target.setCaptured(true);
        }

        piece.setPositionNoMoveFlag(targetCol, targetRow);

        if (isCastlingAttempt) {
            final boolean kingSide = targetCol > oldCol;
            final int rookFromCol = kingSide ? 7 : 0;
            final int rookToCol = kingSide ? (targetCol - 1) : (targetCol + 1);
            rook = manager.getBidakAt(rookFromCol, oldRow);
            if (rook != null) {
                rookOldCol = rook.getCol();
                rookOldRow = rook.getRow();
                rookMovedBefore = rook.hasMoved();
                rookNewCol = rookToCol;
                rookNewRow = oldRow;
                rook.setPositionNoMoveFlag(rookNewCol, rookNewRow);
            }
        }

        final boolean stillInCheck = isInCheck(piece.isWhite());

        // rollback (kembalikan posisi + flag hasMoved)
        piece.setPositionNoMoveFlag(oldCol, oldRow);
        piece.setMoved(pieceMovedBefore);

        if (rook != null) {
            rook.setPositionNoMoveFlag(rookOldCol, rookOldRow);
            rook.setMoved(rookMovedBefore);
        }

        if (target != null) {
            target.setCaptured(targetWasCaptured);
        }

        return stillInCheck;
    }

    /**
     * Semua langkah sah (sudah disaring agar tidak meninggalkan raja sendiri dalam kondisi skak).
     * Format: int[]{col,row}
     */
    

    // ========================
    // CASTLING (ROKADE) HELPERS
    // ========================

    /** Cek apakah kotak (col,row) diserang oleh side byWhite. */
    private boolean isSquareAttacked(final int col, final int row, final boolean byWhite) {
        for (final Bidak b : manager.getAllBidaks()) {
            if (b == null || b.isCaptured() || b.isWhite() != byWhite) continue;

            // Pawn: serangan diagonal tidak tergantung ada musuh atau tidak (penting untuk castling)
            if (b instanceof Pawn) {
                final int dir = b.isWhite() ? -1 : 1;
                final int r = b.getRow() + dir;
                if (r == row) {
                    if (b.getCol() - 1 == col) return true;
                    if (b.getCol() + 1 == col) return true;
                }
                continue;
            }

            // King: serangan 1 langkah di sekelilingnya (tanpa castling)
            if (b instanceof King) {
                final int dc = Math.abs(b.getCol() - col);
                final int dr = Math.abs(b.getRow() - row);
                if (dc <= 1 && dr <= 1 && (dc + dr) > 0) return true;
                continue;
            }

            for (final int[] m : b.getPossibleMoves(manager.getAllBidaks())) {
                if (m[0] == col && m[1] == row) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Cari rook pada kolom tertentu di row tertentu. */
    private Rook getRookAt(final int col, final int row, final boolean isWhite) {
        final Bidak b = manager.getBidakAt(col, row);
        if (b instanceof Rook && !b.isCaptured() && b.isWhite() == isWhite) {
            return (Rook) b;
        }
        return null;
    }

    /** Validasi castling untuk king yang sedang jalan. */
    private boolean canCastle(final King king, final boolean kingSide) {
        if (king == null || king.isCaptured()) return false;
        if (king.hasMoved()) return false;

        final int row = king.getRow();
        final boolean isWhite = king.isWhite();

        final int rookCol = kingSide ? 7 : 0;
        final Rook rook = getRookAt(rookCol, row, isWhite);
        if (rook == null || rook.hasMoved()) return false;

        // jalur harus kosong
        if (kingSide) {
            if (manager.getBidakAt(5, row) != null) return false;
            if (manager.getBidakAt(6, row) != null) return false;
        } else {
            if (manager.getBidakAt(1, row) != null) return false;
            if (manager.getBidakAt(2, row) != null) return false;
            if (manager.getBidakAt(3, row) != null) return false;
        }

        // raja tidak boleh sedang skak, melewati kotak yang diserang, atau mendarat di kotak yang diserang
        final boolean attackedByOpponent = !isWhite;
        if (isSquareAttacked(king.getCol(), row, attackedByOpponent)) return false;

        if (kingSide) {
            if (isSquareAttacked(5, row, attackedByOpponent)) return false;
            if (isSquareAttacked(6, row, attackedByOpponent)) return false;
        } else {
            if (isSquareAttacked(3, row, attackedByOpponent)) return false;
            if (isSquareAttacked(2, row, attackedByOpponent)) return false;
        }

        return true;
    }
public List<int[]> getLegalMoves(final Bidak piece) {
        final List<int[]> legal = new ArrayList<>();
        if (piece == null || piece.isCaptured()) return legal;

        for (final int[] m : piece.getPossibleMoves(manager.getAllBidaks())) {
            if (!wouldLeaveOwnKingInCheck(piece, m[0], m[1])) {
                legal.add(new int[]{m[0], m[1]});
            }
        }

        // Tambahan: castling (rokade) untuk King
        if (piece instanceof King) {
            final King k = (King) piece;
            final int r = k.getRow();

            // king-side: +2
            if (canCastle(k, true)) {
                final int tc = k.getCol() + 2;
                if (!wouldLeaveOwnKingInCheck(k, tc, r)) {
                    legal.add(new int[]{tc, r});
                }
            }

            // queen-side: -2
            if (canCastle(k, false)) {
                final int tc = k.getCol() - 2;
                if (!wouldLeaveOwnKingInCheck(k, tc, r)) {
                    legal.add(new int[]{tc, r});
                }
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

        final boolean pieceMovedBefore = b.hasMoved();
        final boolean targetWasCapturedBefore = target != null && target.isCaptured();

        // Deteksi castling (King pindah 2 kotak)
        Bidak rook = null;
        int rookFromCol = -1, rookFromRow = -1, rookToCol = -1, rookToRow = -1;
        boolean rookMovedBefore = false;

        final boolean isCastling = (b instanceof King)
                && (Math.abs(targetCol - oldCol) == 2)
                && (targetRow == oldRow);

        if (isCastling) {
            final boolean kingSide = targetCol > oldCol;
            rookFromCol = kingSide ? 7 : 0;
            rookFromRow = oldRow;
            rookToCol = kingSide ? (targetCol - 1) : (targetCol + 1);
            rookToRow = oldRow;

            rook = manager.getBidakAt(rookFromCol, rookFromRow);
            if (rook != null) {
                rookMovedBefore = rook.hasMoved();
            }
        }

        // tangkap (jika ada)
        if (target != null) {
            capture(target);
        }

        // jalankan move piece
        b.setPosition(targetCol, targetRow);

        // jika castling: rook ikut pindah
        if (isCastling && rook != null) {
            rook.setPosition(rookToCol, rookToRow);
        }

        // simpan history untuk Undo/Redo (termasuk castling)
        pushHistory(
                b, oldCol, oldRow, targetCol, targetRow, target,
                pieceMovedBefore,
                targetWasCapturedBefore,
                (isCastling ? rook : null),
                rookFromCol, rookFromRow, rookToCol, rookToRow,
                rookMovedBefore
        );

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
                            "Checkmate! " + winner + " menang!",
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