package cloud.xcan.angus.core.storage.domain.setting;

import cloud.xcan.angus.api.enums.PlatformStoreType;
import cloud.xcan.angus.spec.experimental.ValueObjectSupport;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SettingData extends ValueObjectSupport<SettingData> {

  private PlatformStoreType storeType;

  private String proxyAddress;

  private String aesKey;

  private String localDir;

  private String endpoint;

  private String region;

  private String accessKey;

  private String secretKey;

  @JsonIgnore
  @Transient
  private Boolean force;
  @JsonIgnore
  @Transient
  private transient String defaultProxyAddress;

  public SettingData() {
  }

  public SettingData(PlatformStoreType storeType, String proxyAddress, String aesKey,
      String localDir, String endpoint, String region, String accessKey, String secretKey) {
    this.storeType = storeType;
    this.proxyAddress = proxyAddress;
    this.aesKey = aesKey;
    this.localDir = localDir;
    this.endpoint = endpoint;
    this.region = region;
    this.accessKey = accessKey;
    this.secretKey = secretKey;
  }

  @Override
  public SettingData copy() {
    return new SettingData(storeType, proxyAddress, aesKey, localDir, endpoint, region,
        accessKey, secretKey);
  }
}
