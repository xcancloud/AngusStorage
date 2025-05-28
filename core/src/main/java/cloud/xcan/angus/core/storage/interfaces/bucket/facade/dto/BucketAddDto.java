package cloud.xcan.angus.core.storage.interfaces.bucket.facade.dto;


import static cloud.xcan.angus.api.commonlink.StorageConstant.MAX_BUCKET_NAME_LENGTH;
import static cloud.xcan.angus.api.commonlink.StorageConstant.MIN_BUCKET_NAME_LENGTH;

import cloud.xcan.angus.core.storage.infra.store.model.AccessControl;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
@Accessors(chain = true)
public class BucketAddDto implements Serializable {

  @NotBlank
  @Length(min = MIN_BUCKET_NAME_LENGTH, max = MAX_BUCKET_NAME_LENGTH)
  @Schema(description = "Bucket name.", example = "Bucket01", requiredMode = RequiredMode.REQUIRED,
      minLength = MIN_BUCKET_NAME_LENGTH, maxLength = MAX_BUCKET_NAME_LENGTH)
  private String name;

  @NotNull
  @Schema(description = "Bucket access control.", example = "Private", requiredMode = RequiredMode.REQUIRED)
  private AccessControl acl = AccessControl.Private;

}
