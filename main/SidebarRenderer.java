package main;

import bidak.Bidak;
import bidak.BidakMngr;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class SidebarRenderer {

  private final GameLogic logic;
  private final BidakMngr manager;

  private final int startX = 820;
  private final int width = 280;
  private final int height = 800;

  private final Font titleFont = new Font("SansSerif", Font.BOLD, 20);
  private final Font itemFont = new Font("SansSerif", Font.PLAIN, 16);

  public SidebarRenderer(GameLogic logic, BidakMngr manager) {
    this.logic = logic;
    this.manager = manager;
  }

  public void draw(Graphics2D g) {

    // ===== background simpel =====
    g.setColor(new Color(73, 135, 121));
    g.fillRect(startX, 0, width, height);

    int y = 50;

    // =============================
    // TURN SECTION
    // =============================
    y = drawTitle(g, "TURN", y);

    BufferedImage turnIcon = logic.isWhiteTurn()
        ? manager.getWhiteTurnIcon()
        : manager.getBlackTurnIcon();

    drawCenteredImage(g, turnIcon, y, 60);
    y += 90;

    y = drawSeparator(g, y);

    // =============================
    // WHITE GRAVEYARD
    // =============================
    y = drawTitle(g, "WHITE GRAVEYARD", y);
    y = drawGraveyard(g, logic.getGraveyardWhite(), y);

    y = drawSeparator(g, y);

    // =============================
    // BLACK GRAVEYARD
    // =============================
    y = drawTitle(g, "BLACK GRAVEYARD", y);
    drawGraveyard(g, logic.getGraveyardBlack(), y);
  }

  // --------------------------------------------------------------

  private int drawTitle(Graphics2D g, String text, int y) {
    g.setFont(titleFont);
    g.setColor(Color.white);

    int tw = g.getFontMetrics().stringWidth(text);
    int cx = startX + (width / 2) - (tw / 2);

    g.drawString(text, cx, y);
    return y + 35;
  }

  private int drawSeparator(Graphics2D g, int y) {
    g.setColor(new Color(180, 180, 180));
    g.drawLine(startX + 20, y, startX + width - 20, y);
    return y + 25;
  }

  private int drawGraveyard(Graphics2D g, List<Bidak> list, int y) {
    g.setFont(itemFont);

    int iconSize = 40;

    for (Bidak b : list) {
      Image img = b.getImage();
      drawCenteredImage(g, img, y, iconSize);
      y += iconSize + 10;
    }

    return y;
  }

  private void drawCenteredImage(Graphics2D g, Image img, int y, int size) {
    int cx = startX + (width / 2) - (size / 2);
    g.drawImage(img, cx, y, size, size, null);
  }
}
