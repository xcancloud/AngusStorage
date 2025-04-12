package cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_DOMAIN_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_REMARK_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_SHARE_OBJECT_NUM;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_SHARE_PASSWORD_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MIN_SHARE_PASSWORD_LENGTH;

import cloud.xcan.angus.spec.unit.TimeValue;
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
import org.hibernate.validator.constraints.Length;

@Valid
@Getter
@Setter
@Accessors(chain = true)
public class SpaceShareAddDto implements Serializable {

  @NotNull
  @Schema(description = "Space id.", requiredMode = RequiredMode.REQUIRED)
  private Long spaceId;

  @Size(max = MAX_SHARE_OBJECT_NUM)
  @Schema(description = "Space share object ids. Share the whole space when it is empty.")
  private Set<Long> objectIds;

  @Length(max = MAX_DOMAIN_LENGTH)
  @NotEmpty
  @Schema(description = "Share files access url.", requiredMode = RequiredMode.REQUIRED)
  private String url;

  @NotNull
  @Schema(description = "Expiration flag.", requiredMode = RequiredMode.REQUIRED)
  private Boolean expired;

  @Schema(description = "Expiration duration, required when expired = true.")
  private TimeValue expiredDuration;

  @NotNull
  @Schema(description = "Whether to allow public access flag.", requiredMode = RequiredMode.REQUIRED)
  private Boolean public0;

  @Length(min = MIN_SHARE_PASSWORD_LENGTH, max = MAX_SHARE_PASSWORD_LENGTH)
  @Schema(description = "Access password, required when public = false.",
      minLength = MIN_SHARE_PASSWORD_LENGTH, maxLength = MAX_SHARE_PASSWORD_LENGTH)
  private String password;

  @Length(max = MAX_REMARK_LENGTH)
  @Schema(description = "Share remark.", maxLength = MAX_REMARK_LENGTH)
  private String remark;

}
