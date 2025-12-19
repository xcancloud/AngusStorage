package cloud.xcan.angus.core.storage.interfaces.space.facade.vo.share;

import cloud.xcan.angus.api.enums.FileType;
import cloud.xcan.angus.api.enums.PlatformStoreType;
import cloud.xcan.angus.api.storage.file.vo.FileUploadVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.object.SpaceObjectSummaryVo;
import cloud.xcan.angus.remote.NameJoinField;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SpaceShareObjectDetailVo implements Serializable {

  private Long id;

  private FileType type;

  private PlatformStoreType storeType;

  private String name;

  private int level;

  private Long spaceId;

  @NameJoinField(id = "spaceId", repository = "spaceRepo")
  private String spaceName;

  private Long parentDirectoryId;

  @NameJoinField(id = "parentDirectoryId", repository = "spaceObjectRepo")
  private String parentDirectoryName;

  private Long createdBy;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String creator;

  private LocalDateTime createdDate;

  private FileUploadVo file;

  private SpaceObjectSummaryVo summary;

}
