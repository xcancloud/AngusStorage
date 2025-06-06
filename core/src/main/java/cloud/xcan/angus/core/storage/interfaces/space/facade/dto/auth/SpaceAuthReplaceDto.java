package cloud.xcan.angus.core.storage.interfaces.space.facade.dto.auth;


import static cloud.xcan.angus.api.commonlink.StorageConstant.SPACE_PERMISSION_NUM;

import cloud.xcan.angus.core.storage.domain.space.auth.SpacePermission;
import cloud.xcan.angus.validator.CollectionValueNotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SpaceAuthReplaceDto implements Serializable {

  @NotNull
  @CollectionValueNotNull // Fix:: invalid enumeration value is null element
  @Size(min = 1, max = SPACE_PERMISSION_NUM)
  @Schema(description = "Authorization permissions(Operation permission)", requiredMode = RequiredMode.REQUIRED)
  private Set<SpacePermission> permissions;

}




