package cloud.xcan.angus.core.storage.infra.store.init;

import static cloud.xcan.angus.core.utils.CoreUtils.copyPropertiesIgnoreNull;

import cloud.xcan.angus.api.enums.PlatformStoreType;
import cloud.xcan.angus.core.spring.SpringContextHolder;
import cloud.xcan.angus.core.storage.domain.bucket.Bucket;
import cloud.xcan.angus.core.storage.domain.bucket.BucketRepo;
import cloud.xcan.angus.core.storage.domain.setting.SettingData;
import cloud.xcan.angus.core.storage.domain.setting.StorageSetting;
import cloud.xcan.angus.core.storage.domain.setting.StorageSettingKey;
import cloud.xcan.angus.core.storage.domain.setting.StorageSettingRepo;
import cloud.xcan.angus.core.storage.infra.store.ObjectProperties;
import cloud.xcan.angus.core.storage.infra.store.impl.ObjectClientFactory;
import cloud.xcan.angus.spec.utils.ObjectUtils;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

@Slf4j
public class InitStorageListener implements ApplicationListener<ApplicationReadyEvent> {

  @Override
  public void onApplicationEvent(ApplicationReadyEvent contextStartedEvent) {
    try {
      ObjectProperties properties = loadSettingInDb();
      initBucket(properties);
    } catch (Exception e) {
      log.error("System startup exception, system exit", e);
      SpringApplication.exit(contextStartedEvent.getApplicationContext(), () -> -1);
      System.exit(-1);
    }
  }

  private void initBucket(ObjectProperties properties) throws Exception {
    try {
      PlatformStoreType storeType = properties.getStoreType();
      if (storeType == null) {
        throw new IllegalStateException(
            "Storage storeType is null: set env STORAGE_TYPE (LOCAL|AWS_S3) "
                + "or save a complete row in storage_setting (pkey=SETTING)");
      }
      BucketRepo bucketRepo = SpringContextHolder.getBean(BucketRepo.class);
      List<Bucket> buckets = bucketRepo.findAll();
      if (ObjectUtils.isEmpty(buckets)) {
        throw new IllegalStateException("No bucket configuration found");
      }
      ObjectClientFactory.of(storeType).init(buckets);
      log.info("Initialization storage directory or bucket success");
    } catch (Exception e) {
      log.error("Initialization storage directory or bucket exception", e);
      throw e;
    }
  }

  /**
   * 获取存储配置信息：数据库非空字段覆盖配置文件；勿用 BeanUtils 全量拷贝，避免 DB 缺字段把
   * {@code storeType} 等覆盖为 null。
   */
  private ObjectProperties loadSettingInDb() throws Exception {
    try {
      StorageSettingRepo storageSettingRepo = SpringContextHolder.getBean(StorageSettingRepo.class);
      StorageSetting settingDb = storageSettingRepo.findByPkey(StorageSettingKey.SETTING);
      ObjectProperties objectProperties = SpringContextHolder.getBean(ObjectProperties.class);
      if (ObjectUtils.isEmpty(settingDb) || ObjectUtils.isEmpty(settingDb.getPvalue())) {
        log.info("No DB storage setting, use config storeType={}", objectProperties.getStoreType());
        return objectProperties;
      }
      SettingData settingData = settingDb.toSetting();
      if (settingData != null) {
        copyPropertiesIgnoreNull(settingData, objectProperties);
      }
      log.info("Load storage setting success, Use {} storage", objectProperties.getStoreType());
      return objectProperties;
    } catch (Exception e) {
      log.error("Load storage setting exception", e);
      throw e;
    }
  }
}
