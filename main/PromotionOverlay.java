package main;

import bidak.Bidak;
import bidak.BidakMngr;
import bidak.Pawn;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.*;

public class PromotionOverlay extends JPanel {

  private final BidakMngr manager;
  private Pawn pawn;
  private List<Bidak> graveyard;
  private Consumer<Bidak> callback;

  // animation state
  private float globalAlpha = 0f;
  private double scale = 0.8;
  private Timer animTimer;

  // layout settings
  private final int cardW = 96;
  private final int cardH = 96;
  private final int gap = 24;
  private int startX, startY;

  public PromotionOverlay(BidakMngr manager) {
    this.manager = manager;
    setOpaque(false);
    setLayout(null);
    // ensure overlay covers whole PanelGame; PanelGame sets bounds too but safe to
    // set here
    setBounds(0, 0, PanelGame.WIDTH, PanelGame.HEIGHT);

    initMouse();
    initAnimTimer();
    setVisible(false);
  }

  private void initAnimTimer() {
    animTimer = new Timer(16, e -> {
      boolean changed = false;
      if (globalAlpha < 1f) {
        globalAlpha += 0.08f;
        if (globalAlpha > 1f)
          globalAlpha = 1f;
        changed = true;
      }
      if (scale < 1.0) {
        scale += 0.03;
        if (scale > 1.0)
          scale = 1.0;
        changed = true;
      }
      if (changed)
        repaint();
      if (globalAlpha >= 1f && scale >= 1.0) {
        ((Timer) e.getSource()).stop();
      }
    });
  }

  private void initMouse() {
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (graveyard == null || graveyard.isEmpty())
          return;

        int x = startX;
        for (Bidak b : graveyard) {
          Rectangle r = new Rectangle(x, startY, cardW, cardH);
          if (r.contains(e.getPoint())) {
            // chosen
            setVisible(false);
            if (callback != null)
              callback.accept(b);
            return;
          }
          x += cardW + gap;
        }
      }
    });
  }

  /**
   * Show the promotion overlay.
   * 
   * @param pawn      pawn yang dipromosikan
   * @param graveyard daftar bidak dalam kuburan (sesuai warna)
   * @param callback  Consumer yang menerima Bidak yang dipilih
   */
  public void showPromotion(Pawn pawn, List<Bidak> graveyard, Consumer<Bidak> callback) {
    this.pawn = pawn;
    this.graveyard = graveyard;
    this.callback = callback;

    // initial animation state
    globalAlpha = 0f;
    scale = 0.8;
    setVisible(true);

    // compute starting positions (centered)
    int totalW = graveyard.size() * cardW + Math.max(0, graveyard.size() - 1) * gap;

    // UI ONLY CHANGE: kalau overlay belum dapat ukuran layout (0x0), pakai ukuran PanelGame.
    int w = getWidth() > 0 ? getWidth() : PanelGame.WIDTH;
    int h = getHeight() > 0 ? getHeight() : PanelGame.HEIGHT;
    startX = (w - totalW) / 2;
    startY = (h - cardH) / 2 - 20;

    animTimer.restart();
    repaint();
  }

  @Override
  protected void paintComponent(Graphics g) {
    if (!isVisible())
      return;
    Graphics2D g2 = (Graphics2D) g.create();

    // dark translucent background
    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.min(0.85f, globalAlpha * 0.85f)));
    g2.setColor(Color.black);
    g2.fillRect(0, 0, getWidth(), getHeight());

    // title
    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, globalAlpha));
    g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
    String title = "Pilih bidak promosi dari kuburan";
    FontMetrics fm = g2.getFontMetrics();
    int tx = (getWidth() - fm.stringWidth(title)) / 2;
    g2.setColor(Color.white);
    g2.drawString(title, tx, startY - 30);

    // draw cards
    int x = startX;
    for (Bidak b : graveyard) {
      drawCard(g2, b, x, startY, cardW, cardH);
      x += cardW + gap;
    }

    g2.dispose();
  }

  private void drawCard(Graphics2D g2, Bidak b, int x, int y, int w, int h) {
    // shadow
    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, globalAlpha));
    g2.setColor(new Color(0, 0, 0, 120));
    g2.fillRoundRect(x + 4, y + 6, w, h, 12, 12);

    // card background with scale transform
    int cx = x + w / 2;
    int cy = y + h / 2;

    g2.translate(cx, cy);
    g2.scale(scale, scale);
    g2.translate(-cx, -cy);

    Color bgColor;
    Color fgColor;

    if (pawn.isWhite()) {
      bgColor = Color.BLACK; // UI ONLY CHANGE: untuk pawn putih -> background hitam
      fgColor = Color.WHITE; // tulisan & label putih
    } else {
      bgColor = Color.WHITE; // UI ONLY CHANGE: untuk pawn hitam -> background putih
      fgColor = Color.BLACK; // tulisan & label hitam
    }

    g2.setColor(bgColor);
    g2.fillRoundRect(x, y, w, h, 12, 12);

    // border (opsional tetap sama atau bisa disesuaikan)
    g2.setColor(fgColor);
    g2.drawRoundRect(x, y, w, h, 12, 12);

    // draw piece icon (centered)
    BufferedImage img = manager.getIconForPiece(b);
    if (img != null) {
      int imgW = Math.min(w - 20, img.getWidth());
      int imgH = Math.min(h - 34, img.getHeight());
      int ix = x + (w - imgW) / 2;
      int iy = y + 10;
      g2.drawImage(img, ix, iy, imgW, imgH, null);
    } else {
      // fallback: text
      g2.setColor(Color.DARK_GRAY);
      g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
      String name = b.getClass().getSimpleName();
      FontMetrics fm = g2.getFontMetrics();
      int tx = x + (w - fm.stringWidth(name)) / 2;
      int ty = y + h / 2;
      g2.drawString(name, tx, ty);
    }

    // name label
    g2.setColor(fgColor);
    g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    String label = b.getClass().getSimpleName();
    FontMetrics fm = g2.getFontMetrics();
    int lx = x + (w - fm.stringWidth(label)) / 2;
    int ly = y + h - 8;
    g2.drawString(label, lx, ly);

    // reset transforms
    g2.translate(cx, cy);
    g2.scale(1.0 / scale, 1.0 / scale);
    g2.translate(-cx, -cy);
  }
}
