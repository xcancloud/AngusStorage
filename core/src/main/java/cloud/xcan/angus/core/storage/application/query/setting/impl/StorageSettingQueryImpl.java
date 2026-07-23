package cloud.xcan.angus.core.storage.application.query.setting.impl;


import static org.apache.commons.lang3.StringUtils.isBlank;

import cloud.xcan.angus.api.enums.PlatformStoreType;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.storage.application.query.setting.StorageSettingQuery;
import cloud.xcan.angus.core.storage.domain.setting.SettingData;
import cloud.xcan.angus.core.storage.domain.setting.StorageSetting;
import cloud.xcan.angus.core.storage.domain.setting.StorageSettingKey;
import cloud.xcan.angus.core.storage.domain.setting.StorageSettingRepo;
import cloud.xcan.angus.core.storage.infra.store.ObjectProperties;
import cloud.xcan.angus.core.utils.SpringAppDirUtils;
import cloud.xcan.angus.remote.message.SysException;
import jakarta.annotation.Resource;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StorageSettingQueryImpl implements StorageSettingQuery {

  private static final String MASK = "******";

  @Resource
  private StorageSettingRepo storageSettingRepo;

  @Resource
  private ObjectProperties objectProperties;

  @Resource
  private SpringAppDirUtils appDirUtils;

  @Override
  public SettingData setting() {
    return new BizTemplate<SettingData>() {

      @Override
      protected SettingData process() {
        StorageSetting setting = storageSettingRepo.findByPkey(StorageSettingKey.SETTING);
        SettingData settingData;
        try {
          if (Objects.nonNull(setting) && Objects.nonNull(setting.getPvalue())) {
            settingData = setting.toSetting();
          } else {
            settingData = new SettingData();
            BeanUtils.copyProperties(objectProperties, settingData);
          }
        } catch (Exception e) {
          log.error("Parse storage setting error", e);
          throw SysException.of("Parse storage setting error:" + e.getMessage());
        }
        // Defaults: LOCAL + {APP_HOME}/data/storage_files
        if (settingData.getStoreType() == null) {
          settingData.setStoreType(PlatformStoreType.LOCAL);
        }
        if (isBlank(settingData.getLocalDir())) {
          settingData.setLocalDir(appDirUtils.getBizDataDir("storage_files"));
        }
        settingData.setDefaultProxyAddress(objectProperties.getProxyAddress());
        // Mask secrets for API clients
        if (!isBlank(settingData.getSecretKey())) {
          settingData.setSecretKey(MASK);
        }
        return settingData;
      }
    }.execute();
  }
}
