package cloud.xcan.angus.core.storage.infra.store.model;

import cloud.xcan.angus.spec.experimental.Value;
import lombok.Getter;

@Getter
public enum UploadType implements Value<String> {
  NORMAL, PART, interruption_POINT_CONTINUE, PART_AND_INTERRUPTION_POINT_CONTINUE;

  @Override
  public String getValue() {
    return this.name();
  }
}
