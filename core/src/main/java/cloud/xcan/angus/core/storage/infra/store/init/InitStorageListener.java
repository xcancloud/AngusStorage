package cloud.xcan.angus.core.storage.infra.store.init;

import cloud.xcan.angus.core.spring.SpringContextHolder;
import cloud.xcan.angus.core.storage.domain.bucket.Bucket;
import cloud.xcan.angus.core.storage.domain.bucket.BucketRepo;
import cloud.xcan.angus.core.storage.domain.setting.StorageSetting;
import cloud.xcan.angus.core.storage.domain.setting.StorageSettingKey;
import cloud.xcan.angus.core.storage.domain.setting.StorageSettingRepo;
import cloud.xcan.angus.core.storage.infra.store.ObjectProperties;
import cloud.xcan.angus.core.storage.infra.store.impl.ObjectClientFactory;
import cloud.xcan.angus.spec.utils.ObjectUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

@Slf4j
public class InitStorageListener implements ApplicationListener<ApplicationReadyEvent> {

  @Override
  public void onApplicationEvent(ApplicationReadyEvent contextStartedEvent) {
    try {
      initBucket();
      loadSettingInDb();
    } catch (Exception e) {
      log.error("System startup exception, system exit");
      SpringApplication.exit(contextStartedEvent.getApplicationContext(), () -> -1);
      System.exit(-1);
    }
  }

  private void initBucket() throws Exception {
    try {
      BucketRepo bucketRepo = SpringContextHolder.getBean(BucketRepo.class);
      List<Bucket> buckets = bucketRepo.findAll();
      if (ObjectUtils.isEmpty(buckets)) {
        throw new IllegalStateException("No bucket configuration found");
      }
      ObjectClientFactory.current().init(buckets);
      log.info("Initialization storage directory or bucket success");
    } catch (Exception e) {
      log.error("Initialization storage directory or bucket exception", e);
      throw e;
    }
  }

  private void loadSettingInDb() throws Exception {
    try {
      StorageSettingRepo storageSettingRepo = SpringContextHolder.getBean(StorageSettingRepo.class);
      StorageSetting settingDb = storageSettingRepo.findByPkey(StorageSettingKey.SETTING);
      if (ObjectUtils.isEmpty(settingDb) || ObjectUtils.isEmpty(settingDb.getPvalue())) {
        return;
      }
      ObjectProperties objectProperties = SpringContextHolder.getBean(ObjectProperties.class);
      ObjectMapper objectMapper = SpringContextHolder.getBean(ObjectMapper.class);
      BeanUtils.copyProperties(settingDb.toSetting(objectMapper), objectProperties);
      log.info("Load storage setting success, Use {} storage", objectProperties.getStoreType());
    } catch (Exception e) {
      log.error("Load storage setting exception", e);
      throw e;
    }
  }
}
