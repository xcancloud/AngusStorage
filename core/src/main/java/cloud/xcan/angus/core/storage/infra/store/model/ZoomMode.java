package cloud.xcan.angus.core.storage.infra.store.model;

import cloud.xcan.angus.spec.experimental.Value;
import lombok.Getter;

@Getter
public enum ZoomMode implements Value<String> {
  SCALE("m_scale"),
  FIXED("m_fixed");

  private String value;

  ZoomMode(String value) {
    this.value = value;
  }

  @Override
  public String getValue() {
    return this.value;
  }

  public static ZoomMode of(String value) {
    for (ZoomMode mode : ZoomMode.values()) {
      if (mode.value.equalsIgnoreCase(value)) {
        return mode;
      }
    }
    return null;
  }
}
