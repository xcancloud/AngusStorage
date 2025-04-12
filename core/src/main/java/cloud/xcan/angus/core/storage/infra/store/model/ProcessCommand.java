package cloud.xcan.angus.core.storage.infra.store.model;

import cloud.xcan.angus.core.biz.ProtocolAssert;
import cloud.xcan.angus.spec.utils.ObjectUtils;
import cloud.xcan.angus.spec.utils.StringUtils;
import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ProcessCommand implements Serializable {

  private static final int MAX_SCALE = 5 * 1024;
  private static final int MAX_WIDTH = 10 * 1024;
  private static final int MAX_HEIGHT = 10 * 1024;

  /**
   * The name of the image after zooming
   */
  private Command command;
  /**
   * Zoom mode when image
   */
  private ZoomMode zoomMode;
  /**
   * Zoom param when image
   */
  private double scale;
  private int width;
  private int height;

  /**
   * Return null in case of format error.
   */
  public static ProcessCommand parse(String fproc) {
    ProcessCommand command;
    try {
      command = parse0(fproc);
    } catch (Exception e) {
      // Format error
      return null;
    }
    if (Objects.isNull(command)) {
      return null;
    }
    if (Command.IMAGE_RESIZE.equals(command.getCommand())) {
      if (ZoomMode.SCALE.equals(command.getZoomMode())) {
        ProtocolAssert.assertTrue(command.getScale() > 0 && command.getScale() < MAX_SCALE,
            String.format("Valid scale value scope (0,%d]", MAX_SCALE));
      } else {
        ProtocolAssert.assertTrue(command.getWidth() > 0 && command.getWidth() < MAX_WIDTH,
            String.format("Valid width value scope (0,%d]", MAX_WIDTH));
        ProtocolAssert.assertTrue(command.getHeight() > 0 && command.getHeight() < MAX_HEIGHT,
            String.format("Valid height value scope (0,%d]", MAX_HEIGHT));
      }
    }
    return command;
  }

  /**
   * fproc parameter format: command + "," + param1 + "," + param2 + "," + ...
   *
   * <pre>
   * File processing extension parameters, image support two scaling modes:
   *   - Proportionally, such as: image/resize, m_scale, s_120, fixed width
   *   - Fixed width and height, such as: image/resize, m_fixed, w_100, h_100
   * </pre>
   */
  public static ProcessCommand parse0(String fproc) {
    if (ObjectUtils.isEmpty(fproc)) {
      return null;
    }
    String[] process = StringUtils.remove(fproc, " ").split(",");
    Command command = Command.of(process[0]);
    if (Objects.isNull(command)) {
      return null;
    }
    if (Command.IMAGE_RESIZE.equals(command)) {
      ZoomMode zoomMode = ZoomMode.of(process[1]);
      if (Objects.isNull(zoomMode)) {
        return null;
      }
      if (zoomMode.equals(ZoomMode.SCALE)) {
        if (process.length != 3) {
          return null;
        }
        double scale = Double.parseDouble(process[2].replaceFirst("s_", ""));
        return new ProcessCommand().setCommand(command)
            .setZoomMode(zoomMode).setScale(scale);
      } else {
        if (process.length != 4) {
          return null;
        }
        int width = Integer.parseInt(process[2].replaceFirst("w_", ""));
        int height = Integer.parseInt(process[3].replaceFirst("h_", ""));
        return new ProcessCommand().setCommand(command)
            .setZoomMode(zoomMode).setWidth(width).setHeight(height);
      }
    }
    return null;
  }

  public boolean isZoomScale() {
    return Objects.nonNull(zoomMode) && zoomMode.equals(ZoomMode.SCALE);
  }

  public boolean isZoomFixed() {
    return Objects.nonNull(zoomMode) && zoomMode.equals(ZoomMode.FIXED);
  }
}
