package cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Valid
@Getter
@Setter
@Accessors(chain = true)
public class SpaceShareDetailDto implements Serializable {

  @Schema(description = "Share id.")
  private Long sid;

  @Schema(description = "Share public token.")
  private String spt;

  @Schema(description = "Access password, required when private sharding.")
  private String password;

}
