package cloud.xcan.angus.core.storage.interfaces.space.facade.vo.auth;

import cloud.xcan.angus.core.storage.domain.space.auth.SpacePermission;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class SpaceAuthCurrentVo {

  private boolean spaceAuth;

  private Set<SpacePermission> permissions;
}
