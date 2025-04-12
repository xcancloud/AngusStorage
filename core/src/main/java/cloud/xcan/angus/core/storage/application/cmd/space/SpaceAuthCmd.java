package cloud.xcan.angus.core.storage.application.cmd.space;

import cloud.xcan.angus.core.storage.domain.space.auth.SpaceAuth;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.Set;

public interface SpaceAuthCmd {

  IdKey<Long, Object> add(SpaceAuth spaceAuth);

  void addCreatorAuth(Set<Long> creatorIds, Long spaceId);

  void replace(SpaceAuth spaceAuth);

  void delete(SpaceAuth spaceAuth);

  void enabled(Long spaceId, Boolean enabled);

}




