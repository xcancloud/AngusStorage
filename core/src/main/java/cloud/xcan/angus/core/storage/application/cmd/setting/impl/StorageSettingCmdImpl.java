package cloud.xcan.angus.core.storage.application.cmd.setting.impl;

import static cloud.xcan.angus.core.biz.BizAssert.assertTrue;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isOpClient;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isSysAdmin;
import static cloud.xcan.angus.remote.message.http.Forbidden.M.NO_SYS_ADMIN_PERMISSION;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertForbidden;
import static cloud.xcan.angus.core.storage.domain.StorageMessage.STORAGE_SETTING_MODIFY_TYPE_WARN;
import static cloud.xcan.angus.core.storage.domain.StorageMessage.STORAGE_SETTING_MODIFY_TYPE_WARN_CODE;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizAssert;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.spring.boot.ApplicationInfo;
import cloud.xcan.angus.core.storage.application.cmd.setting.StorageSettingCmd;
import cloud.xcan.angus.core.storage.domain.setting.SettingData;
import cloud.xcan.angus.core.storage.domain.setting.StorageSetting;
import cloud.xcan.angus.core.storage.domain.setting.StorageSettingKey;
import cloud.xcan.angus.core.storage.domain.setting.StorageSettingRepo;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObjectRepo;
import cloud.xcan.angus.core.storage.infra.store.ObjectProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

@Biz
@Slf4j
public class StorageSettingCmdImpl extends CommCmd<StorageSetting, Long> implements
    StorageSettingCmd {

  @Resource
  private StorageSettingRepo storageSettingRepo;

  @Resource
  private SpaceObjectRepo spaceObjectRepo;

  @Resource
  private ApplicationInfo applicationInfo;

  @Resource
  private ObjectProperties objectProperties;

  @Resource
  private ObjectMapper objectMapper;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void settingReplace(SettingData setting) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // Check the system admin permission by edition
        if (applicationInfo.isCloudServiceEdition()) {
          assertForbidden(isOpClient() && isSysAdmin(), NO_SYS_ADMIN_PERMISSION);
        } else {
          assertForbidden(isSysAdmin(), NO_SYS_ADMIN_PERMISSION);
        }

        // Check whether there is business data. When there is data, it cannot be modified by default
        assertTrue(setting.getStoreType().equals(objectProperties.getStoreType())
                || !spaceObjectRepo.existsByStoreTypeNot(setting.getStoreType().getValue())
                || (Objects.nonNull(setting.getForce()) && setting.getForce()),
            STORAGE_SETTING_MODIFY_TYPE_WARN_CODE, STORAGE_SETTING_MODIFY_TYPE_WARN);
      }

      @SneakyThrows
      @Override
      protected Void process() {
        StorageSetting settingDb = storageSettingRepo.findByPkey(StorageSettingKey.SETTING);
        String value = objectMapper.writeValueAsString(setting);
        if (Objects.nonNull(settingDb)) {
          storageSettingRepo.updateValueByKey(StorageSettingKey.SETTING, value);
        } else {
          settingDb = new StorageSetting()
              .setId(uidGenerator.getUID()).setPkey(StorageSettingKey.SETTING).setPvalue(value);
          storageSettingRepo.save(settingDb);
        }
        BeanUtils.copyProperties(setting, objectProperties);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<StorageSetting, Long> getRepository() {
    return this.storageSettingRepo;
  }
}
