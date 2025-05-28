package cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_PUBLIC_TOKEN_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_SHARE_PASSWORD_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MIN_SHARE_PASSWORD_LENGTH;

import cloud.xcan.angus.api.enums.FileType;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@Accessors(chain = true)
public class SpaceShareObjectSearchDto extends PageQuery implements Serializable {

  @NotNull
  @Schema(description = "Share id.", requiredMode = RequiredMode.REQUIRED)
  private Long sid;

  @NotEmpty
  @Length(max = MAX_PUBLIC_TOKEN_LENGTH)
  @Schema(description = "Share public token.", requiredMode = RequiredMode.REQUIRED)
  private String spt;

  @Length(min = MIN_SHARE_PASSWORD_LENGTH, max = MAX_SHARE_PASSWORD_LENGTH)
  @Schema(description = "Access password, required when private sharding.",
      minLength = MIN_SHARE_PASSWORD_LENGTH, maxLength = MAX_SHARE_PASSWORD_LENGTH)
  private String password;

  //////////////////////////////////////////////////////////////

  private Long spaceId;

  private Long parentDirectoryId;

  private FileType type;

  private String name;

  private Long createdBy;

  @DateTimeFormat(pattern = DATE_FMT)
  private LocalDateTime createdDate;

  private Long lastModifiedBy;

  @DateTimeFormat(pattern = DATE_FMT)
  private LocalDateTime lastModifiedDate;

}
