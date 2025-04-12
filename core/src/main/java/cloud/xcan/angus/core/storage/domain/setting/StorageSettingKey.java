package cloud.xcan.angus.core.storage.domain.setting;

import cloud.xcan.angus.spec.experimental.Value;
import lombok.Getter;

@Getter
public enum StorageSettingKey implements Value<String> {
  SETTING;

  @Override
  public String getValue() {
    return this.name();
  }
}
