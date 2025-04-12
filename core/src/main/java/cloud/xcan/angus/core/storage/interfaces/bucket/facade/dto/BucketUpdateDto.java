package cloud.xcan.angus.core.storage.interfaces.bucket.facade.dto;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Valid
@Getter
@Setter
@Accessors(chain = true)
public class BucketUpdateDto implements Serializable {

  @NotNull
  @Schema(description = "Bucket id.",requiredMode = RequiredMode.REQUIRED)
  private Long id;

  @Schema(description = "Bucket access control.")
  private CannedAccessControlList acl;

}
