package cloud.xcan.angus.core.storage.interfaces.space.facade.dto.object;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH_X2;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.io.Serializable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Valid
@Getter
@Setter
@Accessors(chain = true)
public class SpaceDirectoryAddDto implements Serializable {

  @NotNull
  @Schema(description = "Space id.", requiredMode = RequiredMode.REQUIRED)
  private Long spaceId;

  @Schema(description = "Parent directory id, the first level directory is not required.")
  private Long parentDirectoryId;

  @NotBlank
  @Length(max = MAX_NAME_LENGTH_X2)
  @Schema(description = "Directory name.", requiredMode = RequiredMode.REQUIRED, maxLength = MAX_NAME_LENGTH_X2)
  private String name;

}
