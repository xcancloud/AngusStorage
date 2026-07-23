package cloud.xcan.angus.core.storage.interfaces.setting.facade.internal;

import static cloud.xcan.angus.core.storage.interfaces.setting.facade.internal.assembler.StorageSettingAssembler.toSettingData;
import static cloud.xcan.angus.core.storage.interfaces.setting.facade.internal.assembler.StorageSettingAssembler.toSettingDetailTo;

import cloud.xcan.angus.core.storage.application.cmd.setting.StorageSettingCmd;
import cloud.xcan.angus.core.storage.application.cmd.setting.StorageSettingConnectionTestCmd;
import cloud.xcan.angus.core.storage.application.query.setting.StorageSettingQuery;
import cloud.xcan.angus.core.storage.interfaces.setting.facade.StorageSettingFacade;
import cloud.xcan.angus.core.storage.interfaces.setting.facade.dto.StorageSettingReplaceDto;
import cloud.xcan.angus.core.storage.interfaces.setting.facade.vo.StorageSettingDetailVo;
import cloud.xcan.angus.core.storage.interfaces.setting.facade.vo.StorageSettingTestVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class StorageSettingFacadeImpl implements StorageSettingFacade {

  @Resource
  private StorageSettingQuery storageSettingQuery;

  @Resource
  private StorageSettingCmd storageSettingCmd;

  @Resource
  private StorageSettingConnectionTestCmd storageSettingConnectionTestCmd;

  @Override
  public void settingReplace(StorageSettingReplaceDto dto) {
    storageSettingCmd.settingReplace(toSettingData(dto));
  }

  @Override
  public StorageSettingDetailVo settingDetail() {
    return toSettingDetailTo(storageSettingQuery.setting());
  }

  @Override
  public StorageSettingTestVo settingTest(StorageSettingReplaceDto dto) {
    return storageSettingConnectionTestCmd.test(toSettingData(dto));
  }

}
