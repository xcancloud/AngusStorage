package cloud.xcan.angus.core.storage.application.cmd.space;

import cloud.xcan.angus.core.storage.domain.space.object.SpaceObject;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface SpaceObjectCmd {

  IdKey<Long, Object> directoryAdd(SpaceObject directory);

  void rename(Long id, String name);

  void move(Set<Long> objectIds, Long targetSpaceId, Long targetDirectoryId);

  void delete(HashSet<Long> ids);

  void fileAdd0(Long spaceId, SpaceObject parentDirectoryDb, List<SpaceObject> files);

}
