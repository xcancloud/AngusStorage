package cloud.xcan.angus.core.storage.interfaces.space.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BIZ_KEY_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_REMARK_LENGTH;

import cloud.xcan.angus.spec.unit.DataSize;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Valid
@Getter
@Setter
@Accessors(chain = true)
public class SpaceAddDto implements Serializable {

  @NotNull
  @Schema(description = "Project id, it required when creating a space.", requiredMode = RequiredMode.REQUIRED)
  private Long projectId;

  @Length(max = MAX_BIZ_KEY_LENGTH)
  @Schema(description = "Business key, default value `angusTesterDataFiles`.", maxLength = MAX_BIZ_KEY_LENGTH)
  private String bizKey;

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Space name.", example = "undefined", requiredMode = RequiredMode.REQUIRED,
      maxLength = MAX_NAME_LENGTH)
  private String name;

  @Schema(description = "Whether to enable authorization control, default disabled.")
  private Boolean auth;

  @Schema(description = "Space space size (bytes). Unlimited space size when not set.")
  private DataSize quotaSize;

  @Length(max = MAX_REMARK_LENGTH)
  @Schema(description = "Space remark.", maxLength = MAX_REMARK_LENGTH)
  private String remark;

}
