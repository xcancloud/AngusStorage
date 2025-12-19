package cloud.xcan.angus.core.storage.interfaces.space.facade.dto.object;

import cloud.xcan.angus.api.enums.FileType;
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
public class SpaceObjectFindDto extends PageQuery implements Serializable {

  @Schema(description = "Directory or file id.")
  private Long id;

  @NotNull
  @Schema(description = "Space id.", requiredMode = RequiredMode.REQUIRED)
  private Long spaceId;

  @Schema(description = "Parent directory id, the first level directory is not required.")
  private Long parentDirectoryId;

  private FileType type;

  private String name;

}
