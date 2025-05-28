package cloud.xcan.angus.core.storage.interfaces.setting.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH_X2;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_FILE_PATH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_HOST_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_PARAM_VALUE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_URL_LENGTH_X2;

import cloud.xcan.angus.api.enums.PlatformStoreType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Accessors(chain = true)
public class StorageSettingReplaceDto implements Serializable {

  @Schema(description = "Whether to force cover, required when the platform storage type is forced to be modified.")
  private Boolean force;

  @NotNull
  @Schema(description = "Platform store type.", requiredMode = RequiredMode.REQUIRED)
  private PlatformStoreType storeType;

  @NotEmpty
  @Length(max = MAX_HOST_LENGTH)
  @Schema(description = "Storage proxy address, will be used by the download url.",
      requiredMode = RequiredMode.REQUIRED, maxLength = MAX_HOST_LENGTH)
  private String proxyAddress;

  @Length(max = MAX_PARAM_VALUE_LENGTH)
  @Schema(description = "AES encryption key, required when encrypting stored files.", hidden = true)
  private String aesKey;

  @Length(max = MAX_FILE_PATH)
  @Schema(description = "Local storage directory, required when storeType=LOCAL.", maxLength = MAX_FILE_PATH)
  private String localDir;

  @Length(max = MAX_URL_LENGTH_X2)
  @Schema(description = "S3 storage endpoint, required when storeType=AWS_S3.", maxLength = MAX_URL_LENGTH_X2)
  private String endpoint;

  @Length(max = MAX_CODE_LENGTH_X2)
  @Schema(description = "S3 storage region, optional when storeType=AWS_S3.", maxLength = MAX_CODE_LENGTH_X2)
  private String region;

  @Length(max = MAX_PARAM_VALUE_LENGTH)
  @Schema(description = "S3 storage accessKey, required when storeType=AWS_S3.", maxLength = MAX_PARAM_VALUE_LENGTH)
  private String accessKey;

  @Length(max = MAX_PARAM_VALUE_LENGTH)
  @Schema(description = "S3 storage secretKey, required when storeType=AWS_S3.", maxLength = MAX_PARAM_VALUE_LENGTH)
  private String secretKey;

}
