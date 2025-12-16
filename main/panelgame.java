package main;

import bidak.Bidak;
import bidak.BidakMngr;
import bidak.Bishop;
import bidak.Knight;
import bidak.Pawn;
import bidak.Queen;
import bidak.Rook;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 * Panel utama game.
 * - Mendukung pilihan warna (white/black) dengan membalik orientasi papan.
 * - Undo/Redo: Ctrl+Z / Ctrl+Y
 */
public class PanelGame extends JPanel implements Runnable {

    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;

    private final int FPS = 60;
    private Thread gameThread;

    private final Papan papan = new Papan();
    private final BidakMngr bidakMngr = new BidakMngr();
    private final GameLogic logic = new GameLogic(bidakMngr);

    private InputHandler input;
    private SidebarRenderer sidebar;
    private PromotionOverlay promotionOverlay;

    // Jika false, papan dirender terbalik (hitam di bawah).
    private boolean playerIsWhite = true;

    public PanelGame() {
        this(true);
    }

    public PanelGame(boolean isWhite) {
        this.playerIsWhite = isWhite;

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.black);
        setFocusable(true);

        // Komponen pendukung
        sidebar = new SidebarRenderer(logic, bidakMngr);
        promotionOverlay = new PromotionOverlay(bidakMngr);

        // Input
        input = new InputHandler(bidakMngr, logic, this);
        addMouseListener(input);

        setupUndoRedoKeybinds();
    }

    private void setupUndoRedoKeybinds() {
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke("control Z"), "undoMove");
        im.put(KeyStroke.getKeyStroke("control Y"), "redoMove");

        am.put("undoMove", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (logic.undo()) {
                    repaint();
                }
            }
        });

        am.put("redoMove", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (logic.redo()) {
                    repaint();
                }
            }
        });
    }

    public boolean isPlayerWhite() {
        return playerIsWhite;
    }

    /**
     * Konversi koordinat layar (pixel) -> koordinat papan (col,row).
     * Kalau pemain memilih hitam, input juga dibalik agar klik tetap akurat.
     *
     * @return int[]{col,row} atau null jika di luar papan.
     */
    public int[] screenToBoard(final int x, final int y) {
        final int boardW = Papan.MAX_COL * Papan.KOTAK_SIZE;
        final int boardH = Papan.MAX_ROW * Papan.KOTAK_SIZE;

        if (x < 0 || y < 0 || x >= boardW || y >= boardH) {
            return null;
        }

        int col = x / Papan.KOTAK_SIZE;
        int row = y / Papan.KOTAK_SIZE;

        if (!playerIsWhite) {
            col = 7 - col;
            row = 7 - row;
        }

        return new int[] { col, row };
    }

    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();
        requestFocusInWindow();
    }

    @Override
    public void run() {
        final double drawInterval = 1_000_000_000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();

        while (gameThread != null) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    // =============================
    //      UPDATE GAME
    // =============================
    private void update() {
        // Jika overlay promosi tampil, blok input sampai pemain memilih bidak promosi
        if (promotionOverlay != null && promotionOverlay.isVisible()) {
            input.blockInput();
            return;
        }
        input.allowInput();

        // Catatan:
        // Jangan blok input berdasar giliran di sini.
        // Kalau pemain memilih "hitam" dan belum ada AI, game akan terasa "tidak jalan".
        // Dengan begini, pilihan warna hanya mengubah orientasi papan + input mapping.
    }

    // =============================
    //   PROMOSI BIDAK
    // =============================
    public void startPromotion(final Pawn pawn, final List<Bidak> graveyard) {
        List<Bidak> validPromotions = new ArrayList<>();
        for (Bidak b : graveyard) {
            if (b instanceof Queen || b instanceof Rook || b instanceof Bishop || b instanceof Knight) {
                validPromotions.add(b);
            }
        }

        promotionOverlay.showPromotion(pawn, validPromotions, (selected) -> {
            logic.reviveFromGrave(selected);
            bidakMngr.promotePawnFromGrave(pawn, selected);
            repaint();
        });
    }

    // =============================
    //   RENDER
    // =============================
    @Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;

    // Papan (kotak) sesuai orientasi pemain
    papan.draw(g2, playerIsWhite);

    // Hint langkah (ditampilkan sesuai orientasi)
    input.drawMoveHints(g2);

    // Bidak digambar tanpa rotate supaya tidak terbalik
    drawPieces(g2);

    // Sidebar (kanan)
    sidebar.draw(g2);

    // Overlay promosi (jika ada)
    if (promotionOverlay != null && promotionOverlay.isVisible()) {
        // overlay adalah komponen Swing; cukup repaint agar animasi/alpha jalan
        promotionOverlay.repaint();
    }
}

private void drawPieces(Graphics2D g2) {
    final int size = Papan.KOTAK_SIZE;

    for (Bidak b : bidakMngr.getAllBidaks()) {
        int col = b.getCol();
        int row = b.getRow();

        int drawCol = playerIsWhite ? col : (Papan.MAX_COL - 1 - col);
        int drawRow = playerIsWhite ? row : (Papan.MAX_ROW - 1 - row);

        int x = drawCol * size;
        int y = drawRow * size;

        g2.drawImage(b.getImage(), x, y, size, size, null);

        // highlight sederhana untuk bidak yang sedang dipilih
        if (input != null && b == input.getSelectedBidak()) {
            g2.setColor(new java.awt.Color(255, 255, 0, 180));
            g2.drawRect(x + 2, y + 2, size - 4, size - 4);
        }
    }
}

/**
 * Konversi koordinat papan (col,row) -> koordinat tampilan (col,row) sesuai orientasi.
 */
public int[] boardToScreen(int col, int row) {
    if (col < 0 || col >= Papan.MAX_COL || row < 0 || row >= Papan.MAX_ROW) return null;
    int sc = playerIsWhite ? col : (Papan.MAX_COL - 1 - col);
    int sr = playerIsWhite ? row : (Papan.MAX_ROW - 1 - row);
    return new int[]{sc, sr};
}

}
