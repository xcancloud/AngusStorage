package cloud.xcan.angus.core.storage.interfaces.space.facade.vo.object;

import cloud.xcan.angus.remote.NameJoinField;
import cloud.xcan.angus.api.enums.FileType;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SpaceObjectVo implements Serializable {

  private Long id;

  private FileType type;

  private String name;

  private int level;

  private Long parentDirectoryId;

  @NameJoinField(id = "parentDirectoryId", repository = "spaceObjectRepo")
  private String parentDirectoryName;

  private Long createdBy;

  private LocalDateTime createdDate;

  private Long lastModifiedBy;

  private LocalDateTime lastModifiedDate;

  private SpaceObjectSummaryVo summary;

}
