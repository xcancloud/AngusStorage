package cloud.xcan.angus.core.storage.interfaces.setting.facade;

import cloud.xcan.angus.core.storage.interfaces.setting.facade.dto.StorageSettingReplaceDto;
import cloud.xcan.angus.core.storage.interfaces.setting.facade.vo.StorageSettingDetailVo;
import cloud.xcan.angus.core.storage.interfaces.setting.facade.vo.StorageSettingTestVo;

public interface StorageSettingFacade {

  void settingReplace(StorageSettingReplaceDto dto);

  StorageSettingDetailVo settingDetail();

  StorageSettingTestVo settingTest(StorageSettingReplaceDto dto);

}
