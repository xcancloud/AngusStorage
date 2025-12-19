package cloud.xcan.angus.core.storage.interfaces.space.facade.vo;

import cloud.xcan.angus.api.enums.PlatformStoreType;
import cloud.xcan.angus.core.storage.interfaces.bucket.facade.vo.BucketBizConfigVo;
import cloud.xcan.angus.remote.NameJoinField;
import cloud.xcan.angus.spec.unit.DataSize;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SpaceDetailVo implements Serializable {

  private Long id;

  private Long projectId;

  private String name;

  private PlatformStoreType storeType;

  private String bizKey;

  private DataSize quotaSize;

  private String bucketName;

  private Boolean auth;

  private String remark;

  private Long createdBy;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String creator;

  private LocalDateTime createdDate;

  private Long modifiedBy;

  @NameJoinField(id = "modifiedBy", repository = "commonUserBaseRepo")
  private String modifier;

  private LocalDateTime modifiedDate;

  private SpaceSummaryVo summary;

  private BucketBizConfigVo config;

}
