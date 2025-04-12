package cloud.xcan.angus.core.storage.domain.space.auth;

import cloud.xcan.angus.spec.ValueObject;
import cloud.xcan.angus.spec.experimental.EndpointRegister;
import cloud.xcan.angus.spec.locale.EnumMessage;
import java.util.List;
import lombok.Getter;

@Getter
@EndpointRegister
public enum SpacePermission implements ValueObject<SpacePermission>, EnumMessage<String> {
  VIEW, MODIFY, DELETE, SHARE, GRANT, OBJECT_READ, OBJECT_WRITE, OBJECT_DELETE;

  public static final List<SpacePermission> ALL = List.of(VIEW, MODIFY, DELETE, SHARE, GRANT,
      OBJECT_READ, OBJECT_WRITE, OBJECT_DELETE);

  @Override
  public String getValue() {
    return this.name();
  }

  public boolean isGrant() {
    return this.equals(GRANT);
  }
}
