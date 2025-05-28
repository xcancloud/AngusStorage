package cloud.xcan.angus.core.storage.interfaces.space.facade.dto.auth;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;

import cloud.xcan.angus.api.enums.AuthObjectType;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Valid
@Setter
@Getter
public class SpaceAuthFindDto extends PageQuery {

  //@NotNull -> Transferring values in filters
  @Schema(description = "Space id.", requiredMode = RequiredMode.REQUIRED)
  private Long spaceId;

  @NotNull
  @Schema(description = "Space authorization object type.", requiredMode = RequiredMode.REQUIRED)
  private AuthObjectType authObjectType;

  @Schema(description = "Space authorization object id.")
  private Long authObjectId;

  @DateTimeFormat(pattern = DATE_FMT)
  @Schema(description = "Space created date.")
  private LocalDateTime createdDate;

}
