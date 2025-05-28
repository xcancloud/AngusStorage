package cloud.xcan.angus.core.storage.interfaces.space.facade.dto;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;

import cloud.xcan.angus.core.storage.domain.space.auth.SpacePermission;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
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
public class SpaceSearchDto extends PageQuery implements Serializable {

  private Long id;

  @NotNull
  @Schema(description = "Project id.", requiredMode = RequiredMode.REQUIRED)
  private Long projectId;

  @Schema(description = "Business key.")
  private String bizKey;

  @Schema(description = "Space name.")
  private String name;

  @NotBlank
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "Application code.", example = "AngusTester", requiredMode = RequiredMode.REQUIRED)
  private String appCode;

  @Schema(description = "It is required when application administrators query all spaces.")
  private Boolean admin;

  @Schema(description = "It is required when the user query has the one permission spaces.")
  private SpacePermission hasPermission;

  private Long createdBy;

  @DateTimeFormat(pattern = DATE_FMT)
  private LocalDateTime createdDate;

  @Override
  public String getDefaultOrderBy() {
    return "createdDate";
  }

}
