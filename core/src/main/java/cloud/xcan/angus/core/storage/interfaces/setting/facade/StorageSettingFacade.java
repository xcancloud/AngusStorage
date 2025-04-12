package cloud.xcan.angus.core.storage.interfaces.setting.facade;

import cloud.xcan.angus.core.storage.interfaces.setting.facade.dto.StorageSettingReplaceDto;
import cloud.xcan.angus.core.storage.interfaces.setting.facade.vo.StorageSettingDetailVo;

public interface StorageSettingFacade {

  void settingReplace(StorageSettingReplaceDto dto);

  StorageSettingDetailVo settingDetail();

}
