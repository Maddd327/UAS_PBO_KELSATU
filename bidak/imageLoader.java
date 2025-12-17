package bidak;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class imageLoader {

  public static BufferedImage load(String path) {
    try {
      // redirect path sesuai skin yang dipilih dari Main Menu
      String resolved = main.SkinManager.resolvePiecePath(path);
      return ImageIO.read(new File(resolved));
    } catch (Exception e) {
      System.out.println("Gagal load gambar: " + path);
      return null;
    }
  }
}
