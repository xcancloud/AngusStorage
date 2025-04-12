package cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.io.Serializable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Valid
@Getter
@Setter
@Accessors(chain = true)
public class SpaceShareQuickAddDto implements Serializable {

  @NotNull
  @Schema(description = "Space share object id.", requiredMode = RequiredMode.REQUIRED)
  private Long objectId;

}
