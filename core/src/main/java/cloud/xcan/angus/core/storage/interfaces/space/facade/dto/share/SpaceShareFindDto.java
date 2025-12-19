package cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share;

import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SpaceShareFindDto extends PageQuery implements Serializable {

  @Schema(description = "It is required when application administrators query all sharing.")
  private Boolean admin;

  @NotNull
  @Schema(description = "Space id", requiredMode = RequiredMode.REQUIRED)
  private Long spaceId;

  @Schema(description = "Share id.")
  private Long id;

  @Schema(description = "Share remark.")
  private String remark;

}
