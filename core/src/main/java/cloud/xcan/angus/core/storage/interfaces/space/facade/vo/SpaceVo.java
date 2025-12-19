package cloud.xcan.angus.core.storage.interfaces.space.facade.vo;

import cloud.xcan.angus.remote.NameJoinField;
import cloud.xcan.angus.spec.jackson.serializer.DataSizeFormat;
import cloud.xcan.angus.spec.unit.DataSize;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SpaceVo implements Serializable {

  private Long id;

  private Long projectId;

  private String name;

  private Boolean auth;

  private String remark;

  private String bizKey;

  //@DataSizeFormat
  private DataSize quotaSize;

  @DataSizeFormat
  private DataSize size;

  private long subDirectoryNum;

  private long subFileNum;

  private Long createdBy;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String creator;

  private LocalDateTime createdDate;

}
