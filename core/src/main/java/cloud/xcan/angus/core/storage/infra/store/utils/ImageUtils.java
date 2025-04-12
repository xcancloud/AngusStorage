package cloud.xcan.angus.core.storage.infra.store.utils;

import cloud.xcan.angus.core.storage.infra.store.model.ProcessCommand;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.imageio.ImageIO;
import org.apache.commons.io.FilenameUtils;

public class ImageUtils {

  public static void zoomByCommand(String srcFile, String destFile, ProcessCommand command)
      throws IOException {
    if (command.isZoomScale()) {
      zoomByScale(srcFile, destFile, command.getScale());
    } else if (command.isZoomFixed()) {
      zoomBySize(srcFile, destFile, command.getWidth(), command.getHeight());
    }
  }

  /**
   * Scale the picture proportionally.
   *
   * @param scale scale proportion
   */
  public static void zoomByScale(String srcFile, String destFile, double scale)
      throws IOException {
    String ext = FilenameUtils.getExtension(srcFile);
    BufferedImage imgBuffer = ImageIO.read(new File(srcFile));
    int width = (int) (scale * imgBuffer.getWidth());
    int height = (int) (scale * imgBuffer.getHeight());
    Image img = imgBuffer.getScaledInstance(width, height, Image.SCALE_DEFAULT);
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics = image.createGraphics();
    graphics.drawImage(img, 0, 0, null);
    graphics.dispose();
    OutputStream out = new FileOutputStream(destFile);
    ImageIO.write(image, ext, out);
    out.close();
  }


  /**
   * Scale the picture according to the width and height
   *
   * @param width  width
   * @param height height
   */
  public static void zoomBySize(String srcFile, String destFile, int width, int height)
      throws IOException {
    String ext = FilenameUtils.getExtension(srcFile);
    BufferedImage imgBuffer = ImageIO.read(new File(srcFile));
    Image img = imgBuffer.getScaledInstance(width, height, Image.SCALE_DEFAULT);
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics = image.createGraphics();
    graphics.drawImage(img, 0, 0, null);
    graphics.dispose();
    OutputStream out = new FileOutputStream(destFile);
    ImageIO.write(image, ext, out);
    out.close();
  }
}
