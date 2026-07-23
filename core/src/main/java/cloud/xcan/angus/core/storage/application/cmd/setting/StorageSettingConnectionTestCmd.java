package cloud.xcan.angus.core.storage.application.cmd.setting;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertForbidden;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isOpClient;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isSysAdmin;
import static cloud.xcan.angus.remote.message.http.Forbidden.M.NO_SYS_ADMIN_PERMISSION;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import cloud.xcan.angus.api.enums.PlatformStoreType;
import cloud.xcan.angus.core.spring.boot.ApplicationInfo;
import cloud.xcan.angus.core.storage.domain.setting.SettingData;
import cloud.xcan.angus.core.storage.domain.setting.StorageSetting;
import cloud.xcan.angus.core.storage.domain.setting.StorageSettingKey;
import cloud.xcan.angus.core.storage.domain.setting.StorageSettingRepo;
import cloud.xcan.angus.core.storage.infra.store.ObjectProperties;
import cloud.xcan.angus.core.storage.infra.store.impl.S3ObjectClient;
import cloud.xcan.angus.core.storage.interfaces.setting.facade.vo.StorageSettingTestVo;
import cloud.xcan.angus.core.utils.SpringAppDirUtils;
import cloud.xcan.angus.remote.message.ProtocolException;
import com.amazonaws.services.s3.AmazonS3;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 探测 LOCAL / S3 配置有效性：不写库、不污染全局 {@link ObjectProperties} / 单例 S3 客户端。
 */
@Slf4j
@Component
public class StorageSettingConnectionTestCmd {

  private static final String MASK = "******";

  @Resource
  private ApplicationInfo applicationInfo;

  @Resource
  private StorageSettingRepo storageSettingRepo;

  @Resource
  private ObjectProperties objectProperties;

  @Resource
  private SpringAppDirUtils appDirUtils;

  public StorageSettingTestVo test(SettingData incoming) {
    checkPermission();
    PlatformStoreType storeType = incoming.getStoreType();
    if (storeType == null) {
      throw ProtocolException.of("storeType is required");
    }
    SettingData merged = mergeSecrets(incoming);
    try {
      if (PlatformStoreType.LOCAL.equals(storeType)) {
        testLocal(merged.getLocalDir());
      } else if (PlatformStoreType.AWS_S3.equals(storeType)) {
        testS3(merged);
      } else {
        throw ProtocolException.of("Unsupported store type: " + storeType);
      }
      return new StorageSettingTestVo()
          .setSuccess(true)
          .setStoreType(storeType.getValue())
          .setMessage("OK");
    } catch (ProtocolException e) {
      throw e;
    } catch (Exception e) {
      log.warn("Storage connection test failed: {}", e.getMessage());
      throw ProtocolException.of(e.getMessage());
    }
  }

  private void checkPermission() {
    if (applicationInfo.isCloudServiceEdition()) {
      assertForbidden(isOpClient() && isSysAdmin(), NO_SYS_ADMIN_PERMISSION);
    } else {
      assertForbidden(isSysAdmin(), NO_SYS_ADMIN_PERMISSION);
    }
  }

  private SettingData mergeSecrets(SettingData incoming) {
    SettingData saved = loadSavedOrRuntime();
    if (isMasked(incoming.getSecretKey()) && saved != null && isNotBlank(saved.getSecretKey())) {
      incoming.setSecretKey(saved.getSecretKey());
    }
    if (isMasked(incoming.getAccessKey()) && saved != null && isNotBlank(saved.getAccessKey())) {
      incoming.setAccessKey(saved.getAccessKey());
    }
    return incoming;
  }

  private SettingData loadSavedOrRuntime() {
    try {
      StorageSetting setting = storageSettingRepo.findByPkey(StorageSettingKey.SETTING);
      if (Objects.nonNull(setting) && Objects.nonNull(setting.getPvalue())) {
        return setting.toSetting();
      }
    } catch (Exception e) {
      log.debug("Load saved storage setting for secret merge skipped: {}", e.getMessage());
    }
    return new SettingData()
        .setAccessKey(objectProperties.getAccessKey())
        .setSecretKey(objectProperties.getSecretKey());
  }

  private void testLocal(String localDir) throws IOException {
    String storagePath = isNotBlank(localDir)
        ? localDir.trim()
        : appDirUtils.getBizDataDir("storage_files");
    while (storagePath.endsWith("/") || storagePath.endsWith("\\")) {
      storagePath = storagePath.substring(0, storagePath.length() - 1);
    }
    Path root = Path.of(storagePath);
    Files.createDirectories(root);
    if (!Files.isWritable(root)) {
      throw ProtocolException.of("Local storage path is not writable: " + root.toAbsolutePath());
    }
    Path probe = Files.createTempFile(root, ".storage-probe-", ".tmp");
    Files.deleteIfExists(probe);
  }

  private void testS3(SettingData setting) {
    ObjectProperties props = new ObjectProperties();
    props.setEndpoint(setting.getEndpoint());
    props.setRegion(setting.getRegion());
    props.setAccessKey(setting.getAccessKey());
    props.setSecretKey(setting.getSecretKey());
    AmazonS3 client = S3ObjectClient.createTemporaryClient(props);
    try {
      client.listBuckets();
    } finally {
      try {
        client.shutdown();
      } catch (Exception ignore) {
        // best effort
      }
    }
  }

  private static boolean isMasked(String secret) {
    return isEmpty(secret) || MASK.equals(secret);
  }
}
