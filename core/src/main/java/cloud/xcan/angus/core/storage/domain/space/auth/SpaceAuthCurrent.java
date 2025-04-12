package cloud.xcan.angus.core.storage.domain.space.auth;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import java.util.Collection;
import java.util.LinkedHashSet;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class SpaceAuthCurrent {

  private boolean spaceAuth;

  private LinkedHashSet<SpacePermission> permissions;

  public void addPermissions(Collection<SpacePermission> permissions0) {
    if (isEmpty(permissions0)) {
      return;
    }
    if (permissions == null) {
      permissions = new LinkedHashSet<>();
    }
    permissions.addAll(permissions0);
  }
}
