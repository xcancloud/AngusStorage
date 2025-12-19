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
public class SpaceShareDetailVo implements Serializable {

  private Long id;

  private Long spaceId;

  @NameJoinField(id = "spaceId", repository = "spaceRepo")
  private String spaceName;

  @Schema(description = "Whether to share the whole space flag")
  private Boolean all;

  private Set<Long> objectIds;

  private Boolean expired;

  private TimeValue expiredDuration;

  private LocalDateTime expiredDate;

  private Boolean public0;

  private Long createdBy;

  private String creator;

  private String avatar;

  private LocalDateTime createdDate;

}
