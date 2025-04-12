package cloud.xcan.angus.core.storage.application.cmd.setting;

import cloud.xcan.angus.core.storage.domain.setting.SettingData;


public interface StorageSettingCmd {

  void settingReplace(SettingData setting);
}
