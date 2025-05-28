package cloud.xcan.angus.core.storage.application.query.space;

import cloud.xcan.angus.core.storage.domain.space.object.SpaceObject;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SpaceObjectSearch {

  Page<SpaceObject> search(Set<SearchCriteria> criteria, Pageable pageable,
      Class<SpaceObject> clazz);

}




