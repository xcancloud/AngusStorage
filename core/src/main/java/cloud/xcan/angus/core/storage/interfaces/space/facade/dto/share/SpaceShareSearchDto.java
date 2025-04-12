package cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_REMARK_LENGTH;

import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.io.Serializable;
import java.time.LocalDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

@Valid
@Getter
@Setter
@Accessors(chain = true)
public class SpaceShareSearchDto extends PageQuery implements Serializable {

  @Schema(description = "It is required when application administrators query all sharing.")
  private Boolean admin;

  @NotNull
  @Schema(description = "Space id", requiredMode = RequiredMode.REQUIRED)
  private Long spaceId;

  @Schema(description = "Share id.")
  private Long id;

  @Schema(description = "Share remark.")
  private String remark;

  @Schema(description = "Share user id.")
  private Long createdBy;

  @DateTimeFormat(pattern = DATE_FMT)
  @Schema(description = "Share date.")
  private LocalDateTime createdDate;

}
