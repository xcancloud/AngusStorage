package cloud.xcan.angus.core.storage.application.query.space;

import cloud.xcan.angus.core.storage.domain.space.object.SpaceObject;
import cloud.xcan.angus.core.storage.domain.space.share.SpaceShare;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface SpaceShareSearch {

  Page<SpaceShare> search(Set<SearchCriteria> criteria, Pageable pageable,
      Class<SpaceShare> clazz);

  Page<SpaceObject> objectSearchPub(Set<SearchCriteria> criteria, PageRequest pageable,
      Class<SpaceObject> clazz);
}
