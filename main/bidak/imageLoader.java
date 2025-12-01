package main.bidak;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class imageLoader {

  public static BufferedImage load(String path) {
    try {
      return ImageIO.read(new File(path));
    } catch (Exception e) {
      System.out.println("Gagal load gambar: " + path);
      return null;
    }
  }
}
