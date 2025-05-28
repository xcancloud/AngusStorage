package cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share;


import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_REMARK_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_SHARE_OBJECT_NUM;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_SHARE_PASSWORD_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MIN_SHARE_PASSWORD_LENGTH;

import cloud.xcan.angus.spec.unit.TimeValue;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Accessors(chain = true)
public class SpaceShareUpdateDto implements Serializable {

  @NotNull
  @Schema(description = "Share id.", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  @Size(max = MAX_SHARE_OBJECT_NUM)
  @Schema(description = "Space share object ids.")
  private Set<Long> objectIds;

  @Schema(description = "Expiration flag.")
  private Boolean expired;

  @Schema(description = "Expiration duration, required when expired = true.")
  private TimeValue expiredDuration;

  // Fix:: The modification will make the historical data unavailable
  //@Schema(description = "Whether to allow public access flag")
  //private Boolean public0;

  @Length(min = MIN_SHARE_PASSWORD_LENGTH, max = MAX_SHARE_PASSWORD_LENGTH)
  @Schema(description = "Access password, required when public = false.",
      minLength = MIN_SHARE_PASSWORD_LENGTH, maxLength = MAX_SHARE_PASSWORD_LENGTH)
  private String password;

  @Length(max = MAX_REMARK_LENGTH)
  @Schema(description = "Share remark.", maxLength = MAX_REMARK_LENGTH)
  private String remark;

}
