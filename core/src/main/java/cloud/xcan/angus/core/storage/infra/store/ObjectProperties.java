package cloud.xcan.angus.core.storage.infra.store;

import cloud.xcan.angus.api.enums.PlatformStoreType;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = ObjectProperties.PREFIX)
public class ObjectProperties {

  public static final String PREFIX = "xcan.storage";

  private PlatformStoreType storeType;

  private String proxyAddress;

  private String aesKey;

  private String localDir;

  /**
   * Access domain name
   */
  private String endpoint;

  /**
   * Represented as a disk path in {@link PlatformStoreType#LOCAL} mode, For example: /data
   */
  private String region;

  /**
   * Value is bizKey in {@link PlatformStoreType#LOCAL} mode.
   */
  private String accessKey;

  /**
   * Value is accessSecret in {@link PlatformStoreType#LOCAL} mode.
   */
  private String secretKey;

  private Map<String, Object> other = new HashMap<>();

  public boolean isS3Platform() {
    return Objects.nonNull(storeType) && storeType.equals(PlatformStoreType.AWS_S3);
  }

  public boolean isLocalPlatform() {
    return Objects.nonNull(storeType) && storeType.equals(PlatformStoreType.LOCAL);
  }
}
