package cloud.xcan.angus.core.storage.application.query.space;

import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.storage.domain.space.Space;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObject;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface SpaceObjectQuery {

  SpaceObject detail(Long id);

  SpaceObject navigation(Long id);

  SpaceObject address(Long id);

  Page<SpaceObject> find(GenericSpecification<SpaceObject> spec, PageRequest pageable);

  SpaceObject checkAndFind(Long id);

  SpaceObject checkAndDirectory(Long id);

  void checkAddDirectoryNameExists(Long spaceId, Long parentDirectoryId, String name);

  void checkUpdateDirectoryNameExists(Long spaceId, Long parentDirectoryId, Long id, String name);

  SpaceObject checkAndFindMovedTargetObject(Long targetSpaceId, Long targetDirectoryId);

  List<SpaceObject> checkAndFind(Set<Long> objectIds);

  List<SpaceObject> checkAndFind(Long spaceId, Set<Long> objectIds);

  void checkNestedDuplicates(List<SpaceObject> objectsDb);

  void associateFile(SpaceObject objectDb);

  void setObjectStatsAndSummary(List<SpaceObject> objects);

  void setSpaceStats(List<Space> spaces);

  void setObjectStats(List<SpaceObject> objects);
}
