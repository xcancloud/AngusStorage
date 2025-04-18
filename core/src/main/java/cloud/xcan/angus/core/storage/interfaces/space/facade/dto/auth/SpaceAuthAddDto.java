package cloud.xcan.angus.core.storage.interfaces.space.facade.dto.auth;


import static cloud.xcan.angus.api.commonlink.StorageConstant.SPACE_PERMISSION_NUM;

import cloud.xcan.angus.api.enums.AuthObjectType;
import cloud.xcan.angus.core.storage.domain.space.auth.SpacePermission;
import cloud.xcan.angus.validator.CollectionValueNotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Valid
@Setter
@Getter
@Accessors(chain = true)
public class SpaceAuthAddDto implements Serializable {

  @NotNull
  @Schema(description = "Space authorization object type.", example = "USER", requiredMode = RequiredMode.REQUIRED)
  private AuthObjectType authObjectType;

  @NotNull
  @Schema(description = "Space authorization object id.", example = "1", requiredMode = RequiredMode.REQUIRED)
  private Long authObjectId;

  @Size(min = 1, max = SPACE_PERMISSION_NUM)
  @CollectionValueNotNull // Fix invalid enumeration value is null element
  @Schema(description = "Authorization permissions(Operation permission), default `view`.")
  private List<SpacePermission> permissions;

}




