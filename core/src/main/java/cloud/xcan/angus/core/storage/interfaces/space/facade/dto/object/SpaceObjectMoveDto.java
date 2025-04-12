package cloud.xcan.angus.core.storage.interfaces.space.facade.dto.object;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Valid
@Getter
@Setter
@Accessors(chain = true)
public class SpaceObjectMoveDto implements Serializable {

  @NotEmpty
  @Size(min = 1, max = MAX_BATCH_SIZE)
  @Schema(description = "Source directory or files ids, max `200`.", requiredMode = RequiredMode.REQUIRED)
  private Set<Long> objectIds;

  @NotNull
  @Schema(description = "Target space id.", requiredMode = RequiredMode.REQUIRED)
  private Long targetSpaceId;

  @Schema(description = "Target directory id.")
  private Long targetDirectoryId;

}
