package cloud.xcan.angus.core.storage.application.query.space;

import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObject;
import cloud.xcan.angus.core.storage.domain.space.share.SpaceShare;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface SpaceShareQuery {

  SpaceShare detail(Long id);

  Page<SpaceShare> list(GenericSpecification<SpaceShare> spec, PageRequest pageable);

  SpaceShare shareDetailPub(Long sid, String spt, String password);

  SpaceObject objectDetailPub(Long oid, Long sid, String spt, String password);

  Page<SpaceObject> objectListPub(Set<SearchCriteria> criteria, PageRequest pageable);

  SpaceShare findByQuickObjectId(Long quickObjectId);

  SpaceShare checkAndFind(Long id);

  List<SpaceShare> checkAndGetObjects(HashSet<Long> ids);

  void checkMaxShareNum(SpaceShare share);

}
