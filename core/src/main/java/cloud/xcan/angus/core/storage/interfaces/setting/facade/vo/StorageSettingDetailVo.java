package cloud.xcan.angus.core.storage.interfaces.setting.facade.vo;

import cloud.xcan.angus.api.enums.PlatformStoreType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class StorageSettingDetailVo implements Serializable {

  private PlatformStoreType storeType;

  private String proxyAddress;

  @Schema(hidden = true)
  private String aesKey;

  private String localDir;

  private String endpoint;

  private String region;

  private String accessKey;

  private String secretKey;

}
