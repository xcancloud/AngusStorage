package cloud.xcan.angus.core.storage.interfaces.file.facade.vo;

import cloud.xcan.angus.api.enums.FileType;
import cloud.xcan.angus.remote.NameJoinField;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class FileShareVo implements Serializable {

  private Long id;

  private FileType type;

  private String name;

  private Long size;

  private Long parentId;

  private String likeId;

  private String path;

  private Long createdBy;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String createdByName;

  private LocalDateTime createdDate;

  private LocalDateTime lastModifiedDate;

  private List<FileShareVo> children = new ArrayList<>();

}
