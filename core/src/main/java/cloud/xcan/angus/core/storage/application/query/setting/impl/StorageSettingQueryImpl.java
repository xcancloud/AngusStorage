package cloud.xcan.angus.core.storage.application.query.setting.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.storage.application.query.setting.StorageSettingQuery;
import cloud.xcan.angus.core.storage.domain.setting.SettingData;
import cloud.xcan.angus.core.storage.domain.setting.StorageSetting;
import cloud.xcan.angus.core.storage.domain.setting.StorageSettingKey;
import cloud.xcan.angus.core.storage.domain.setting.StorageSettingRepo;
import cloud.xcan.angus.core.storage.infra.store.ObjectProperties;
import cloud.xcan.angus.remote.message.SysException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

@Biz
@Slf4j
public class StorageSettingQueryImpl implements StorageSettingQuery {

  @Resource
  private StorageSettingRepo storageSettingRepo;

  @Resource
  private ObjectProperties objectProperties;

  @Resource
  private ObjectMapper objectMapper;

  @Override
  public SettingData setting() {
    return new BizTemplate<SettingData>() {
      @Override
      protected void checkParams() {
        // NOOP
      }

      @Override
      protected SettingData process() {
        StorageSetting setting = storageSettingRepo.findByPkey(StorageSettingKey.SETTING);
        try {
          if (Objects.nonNull(setting) && Objects.nonNull(setting.getPvalue())) {
            return setting.toSetting(objectMapper);
          }
        } catch (Exception e) {
          log.error("Parse storage setting error", e);
          throw SysException.of("Parse storage setting error:" + e.getMessage());
        }
        SettingData settingData = new SettingData();
        BeanUtils.copyProperties(objectProperties, settingData);
        // Set default proxy address
        settingData.setDefaultProxyAddress(objectProperties.getProxyAddress());
        return settingData;
      }
    }.execute();
  }
}
