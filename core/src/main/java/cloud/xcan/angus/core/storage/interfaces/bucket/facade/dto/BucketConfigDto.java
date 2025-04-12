package cloud.xcan.angus.core.storage.interfaces.bucket.facade.dto;

import static cloud.xcan.angus.api.commonlink.FileProxyConstant.MAX_BUCKET_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BIZ_KEY_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_REMARK_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Valid
@Getter
@Setter
@Accessors(chain = true)
public class BucketConfigDto implements Serializable {

  @NotBlank
  @Length(max = MAX_BUCKET_NAME_LENGTH)
  @Schema(description = "Bucket name.", example = "Bucket01", requiredMode = RequiredMode.REQUIRED, maxLength = MAX_BUCKET_NAME_LENGTH)
  private String bucketName;

  @NotBlank
  @Length(max = MAX_BIZ_KEY_LENGTH)
  @Schema(description = "Business key.", example = "avatar", requiredMode = RequiredMode.REQUIRED, maxLength = MAX_BIZ_KEY_LENGTH)
  private String bizKey;

  @Schema(description = "Whether to enable file encryption, default `false`.")
  private Boolean encrypt = false;

  @Length(max = MAX_REMARK_LENGTH)
  @Schema(description = "Bucket remark.", maxLength = MAX_REMARK_LENGTH)
  private String remark;

  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "Application code to which the business belongs, default `ANGUSTESTER`.", maxLength = MAX_CODE_LENGTH)
  private String appCode;

  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "Application administrator code, default `ANGUSTESTER_ADMIN`.", maxLength = MAX_CODE_LENGTH)
  private String appAdminCode;

  @Min(0)
  @Schema(description = "Browser cache duration, in second.")
  private int cacheAge;

  @Schema(description = "Allow other tenants to create object flag.")
  private Boolean allowTenantCreated;

}
