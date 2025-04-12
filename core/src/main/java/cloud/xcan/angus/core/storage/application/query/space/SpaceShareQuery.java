package cloud.xcan.angus.core.storage.application.query.space;

import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObject;
import cloud.xcan.angus.core.storage.domain.space.share.SpaceShare;
import java.util.HashSet;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface SpaceShareQuery {

  SpaceShare detail(Long id);

  Page<SpaceShare> find(GenericSpecification<SpaceShare> spec, PageRequest pageable);

  SpaceShare shareDetailPub(Long sid, String spt, String password);

  SpaceObject objectDetailPub(Long oid, Long sid, String spt, String password);

  SpaceShare findByQuickObjectId(Long quickObjectId);

  SpaceShare checkAndFind(Long id);

  List<SpaceShare> checkAndGetObjects(HashSet<Long> ids);

  void checkMaxShareNum(SpaceShare share);

}
