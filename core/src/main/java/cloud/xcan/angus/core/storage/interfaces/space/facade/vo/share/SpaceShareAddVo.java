package cloud.xcan.angus.core.storage.interfaces.space.facade.vo.share;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SpaceShareAddVo implements Serializable {

  @Schema(description = "Share id.")
  private Long id;

  @Schema(description = "Share files access url.")
  private String url;

  @Schema(description = "Access password")
  private String password;

}
