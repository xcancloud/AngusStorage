package cloud.xcan.angus.core.storage.interfaces.space.facade.vo.share;

import cloud.xcan.angus.remote.NameJoinField;
import cloud.xcan.angus.spec.unit.TimeValue;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SpaceShareVo implements Serializable {

  private Long id;

  private Set<Long> objectIds;

  @Schema(description = "Whether to share the whole space flag.")
  private Boolean all;

  private String url;

  private Boolean expired;

  private TimeValue expiredDuration;

  private LocalDateTime expiredDate;

  private Boolean public0;

  private String password;

  private String remark;

  private Long createdBy;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String createdByName;

  private LocalDateTime createdDate;

}
