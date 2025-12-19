package cloud.xcan.angus.core.storage.interfaces.file.facade.vo;

import cloud.xcan.angus.api.enums.FileType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class FileVo implements Serializable {

  private Long id;

  private FileType type;

  private String name;

  @Schema(description = "File name when saving, unique, actual filename after storage.")
  private String uniqueName;

  private Long size;

  private String contentType;

  private String uri;

  private Long parentDirectoryId;

  private String path;

  protected LocalDateTime modifiedDate;

  private List<FileVo> children;

}
