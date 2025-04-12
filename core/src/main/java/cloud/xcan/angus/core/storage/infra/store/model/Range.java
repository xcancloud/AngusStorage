package cloud.xcan.angus.core.storage.infra.store.model;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class Range {

  long start;
  long end;
  long length;
  long total;

  /**
   * Construct a byte range.
   *
   * @param start Start of the byte range.
   * @param end   End of the byte range.
   * @param total Total length of the byte source.
   */
  public Range(long start, long end, long total) {
    this.start = start;
    this.end = end;
    this.length = end - start + 1;
    this.total = total;
  }

  public static long sublong(String value, int beginIndex, int endIndex) {
    String substring = value.substring(beginIndex, endIndex);
    return (substring.length() > 0) ? Long.parseLong(substring) : -1;
  }

  private static void copy(InputStream input, OutputStream output, long inputSize, long start,
      long length) throws IOException {
    byte[] buffer = new byte[2048];
    int read;

    if (inputSize == length) {
      // Write full range.
      while ((read = input.read(buffer)) > 0) {
        output.write(buffer, 0, read);
        output.flush();
      }
    } else {
      process(input, output, start, length, buffer);
    }
  }

  private static void process(InputStream input, OutputStream output, long start, long length,
      byte[] buffer) throws IOException {
    int read;
    long toRead = length;
    if (input.skip(start) > 0) {
      while ((read = input.read(buffer)) > 0) {
        if (toRead > read) {
          try {
            output.write(buffer, 0, read);
            output.flush();
          } catch (Exception e) {
            log.error(e.getMessage(), e);
          }
        } else {
          try {
            output.write(buffer, 0, (int) toRead);
            output.flush();
          } catch (Exception e) {
            log.error(e.getMessage(), e);
          }
          break;
        }
      }
    }
  }

}
