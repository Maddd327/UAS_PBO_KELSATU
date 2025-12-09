package main;

import bidak.*;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;

public class GameLogic {

    private final BidakMngr manager;
    private boolean whiteTurn = true;
    private final List<Bidak> graveyardWhite = new ArrayList<>();
    private final List<Bidak> graveyardBlack = new ArrayList<>();

    public GameLogic(BidakMngr manager) {
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

    private boolean isTurnValid(Bidak b) {
        return b != null && b.isWhite() == whiteTurn;
    }

    // ========================
    // MOVE CALCULATION
    // ========================
    public List<int[]> getPossibleMoves(Bidak b) {
        if (b == null)
            return new ArrayList<>();
        return b.getPossibleMoves(manager.getAllBidaks());
    }

    private boolean isMoveValid(Bidak b, int col, int row) {
        return getPossibleMoves(b).stream()
                .anyMatch(m -> m[0] == col && m[1] == row);
    }

    // ========================
    // MAIN MOVE LOGIC
    // ========================
    public boolean tryMove(Bidak b, int targetCol, int targetRow) {
        if (!isTurnValid(b) || !isMoveValid(b, targetCol, targetRow))
            return false;

        Bidak target = manager.getBidakAt(targetCol, targetRow);
        int oldCol = b.getCol();
        int oldRow = b.getRow();
        boolean targetWasCaptured = false;

        if (target != null) {
            targetWasCaptured = target.isCaptured();
            capture(target);
        }

        b.setPosition(targetCol, targetRow);

        // ========================
        // PAWN PROMOTION (CUSTOM: WAJIB ADA BIDAK BESAR DI KUBURAN)
        // ========================
        if (b instanceof Pawn) {
            int lastRow = b.isWhite() ? 0 : 7;
            if (b.getRow() == lastRow) {
                handlePromotion((Pawn) b);
            }
        }

        // cek apakah setelah langkah ini, raja sendiri masih dalam skak
        if (isInCheck(b.isWhite())) {
            // rollback karena raja masih check
            b.setPosition(oldCol, oldRow);
            if (target != null)
                target.setCaptured(targetWasCaptured);

            javax.swing.SwingUtilities.invokeLater(() -> {
                javax.swing.JOptionPane.showMessageDialog(null,
                        "Gerakan tidak sah! Raja Anda masih dalam skak.",
                        "Illegal Move",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
            });

            return false;
        }

        boolean moverIsWhite = b.isWhite(); // simpan sebelum endTurn()
        // move sah → lanjutkan turn
        endTurn();
        manager.cleanup();

        // cek check / checkmate lawan
        boolean opponentWhite = isWhiteTurn();
        if (isInCheck(opponentWhite)) {
            if (isCheckmate(opponentWhite)) {
                String winner = moverIsWhite ? "Putih" : "Hitam";
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
    // PROMOTION DENGAN ATURAN KUBURAN
    // ========================
    private void handlePromotion(Pawn pawn) {
        // pilih graveyard sesuai warna
        List<Bidak> graveyard = pawn.isWhite() ? graveyardWhite : graveyardBlack;

        boolean canQueen = false;
        boolean canRook = false;
        boolean canBishop = false;
        boolean canKnight = false;

        // cek jenis bidak besar apa saja yang ada di kuburan
        for (Bidak piece : graveyard) {
            if (piece instanceof Queen)  canQueen  = true;
            if (piece instanceof Rook)   canRook   = true;
            if (piece instanceof Bishop) canBishop = true;
            if (piece instanceof Knight) canKnight = true;
        }

        // kalau tidak ada satupun bidak besar yang mati -> promosi dilarang
        if (!canQueen && !canRook && !canBishop && !canKnight) {
            JOptionPane.showMessageDialog(
                null,
                "Tidak bisa promosi.\n" +
                "Tidak ada bidak Ratu / Benteng / Gajah / Kuda yang sudah dimakan.",
                "Promosi Ditolak",
                JOptionPane.WARNING_MESSAGE
            );
            return; // pawn tetap pawn di petak terakhir
        }

        // buat list pilihan sesuai bidak yang benar-benar ada di kuburan
        List<String> optionsList = new ArrayList<>();
        if (canQueen)  optionsList.add("Queen");
        if (canRook)   optionsList.add("Rook");
        if (canBishop) optionsList.add("Bishop");
        if (canKnight) optionsList.add("Knight");

        String[] options = optionsList.toArray(new String[0]);

        int choice = JOptionPane.showOptionDialog(
                null,
                "Pilih bidak promosi (hanya yang sudah ada di kuburan):",
                "Pawn Promotion",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice < 0) {
            // pemain close dialog / cancel -> batal promosi
            return;
        }

        String selected = options[choice];

        // ambil satu bidak yang sesuai dengan pilihan dari kuburan
        Bidak usedPiece = null;
        for (Bidak piece : graveyard) {
            if (selected.equals("Queen")  && piece instanceof Queen)  { usedPiece = piece; break; }
            if (selected.equals("Rook")   && piece instanceof Rook)   { usedPiece = piece; break; }
            if (selected.equals("Bishop") && piece instanceof Bishop) { usedPiece = piece; break; }
            if (selected.equals("Knight") && piece instanceof Knight) { usedPiece = piece; break; }
        }

        if (usedPiece == null) {
            // fallback (harusnya tidak terjadi)
            return;
        }

        // 1) keluarkan bidak besar dari kuburan → tidak ditampilkan lagi di sidebar
        graveyard.remove(usedPiece);

        // 2) hidupkan kembali bidak besar ini di posisi pawn
        usedPiece.setCaptured(false);
        usedPiece.setPosition(pawn.getCol(), pawn.getRow());

        // 3) pawn-nya sendiri masuk kuburan
        capture(pawn);  // akan menambah pawn ke graveyardWhite/Black

        // 4) masukkan bidak besar yang dihidupkan ke daftar bidak di papan
        manager.addBidak(usedPiece);
    }

    // ========================
    // CAPTURE
    // ========================
    private void capture(Bidak target) {
        target.capture();
        if (target.isWhite())
            graveyardWhite.add(target);
        else
            graveyardBlack.add(target);
    }

    // ========================
    // CHECK & CHECKMATE
    // ========================
    public boolean isInCheck(boolean whiteKing) {
        Bidak king = null;
        for (Bidak b : manager.getAllBidaks()) {
            if (!b.isCaptured() && b instanceof King && b.isWhite() == whiteKing) {
                king = b;
                break;
            }
        }
        if (king == null)
            return false;

        for (Bidak b : manager.getAllBidaks()) {
            if (!b.isCaptured() && b.isWhite() != whiteKing) {
                for (int[] m : b.getPossibleMoves(manager.getAllBidaks())) {
                    if (m[0] == king.getCol() && m[1] == king.getRow())
                        return true;
                }
            }
        }
        return false;
    }

    public boolean isCheckmate(boolean whiteKing) {
        if (!isInCheck(whiteKing))
            return false;

        for (Bidak b : manager.getAllBidaks()) {
            if (b.isCaptured() || b.isWhite() != whiteKing)
                continue;

            for (int[] m : b.getPossibleMoves(manager.getAllBidaks())) {
                int oldCol = b.getCol(), oldRow = b.getRow();
                Bidak target = manager.getBidakAt(m[0], m[1]);
                boolean targetWasCaptured = false;

                if (target != null) {
                    targetWasCaptured = target.isCaptured();
                    target.capture();
                }

                b.setPosition(m[0], m[1]);
                boolean stillInCheck = isInCheck(whiteKing);

                // rollback
                b.setPosition(oldCol, oldRow);
                if (target != null)
                    target.setCaptured(targetWasCaptured);

                if (!stillInCheck)
                    return false;
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
}
