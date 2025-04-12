package cloud.xcan.angus.core.storage.application.query.space.impl;

import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.findFirstValue;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;

import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.ProtocolAssert;
import cloud.xcan.angus.core.storage.application.query.space.SpaceAuthQuery;
import cloud.xcan.angus.core.storage.application.query.space.SpaceObjectQuery;
import cloud.xcan.angus.core.storage.application.query.space.SpaceObjectSearch;
import cloud.xcan.angus.core.storage.application.query.space.SpaceQuery;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObject;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObjectSearchRepo;
import java.util.Set;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Biz
public class SpaceObjectSearchImpl implements SpaceObjectSearch {

  @Resource
  private SpaceObjectSearchRepo spaceObjectSearchRepo;

  @Resource
  private SpaceObjectQuery spaceObjectQuery;

  @Resource
  private SpaceQuery spaceQuery;

  @Resource
  private SpaceAuthQuery spaceAuthQuery;

  @Override
  public Page<SpaceObject> search(Set<SearchCriteria> criteria, Pageable pageable,
      Class<SpaceObject> clazz) {
    return new BizTemplate<Page<SpaceObject>>() {
      @Override
      protected void checkParams() {
        String spaceId = findFirstValue(criteria, "spaceId");
        ProtocolAssert.assertNotNull(spaceId, "spaceId is required");
        spaceQuery.check(Long.parseLong(spaceId));
        spaceAuthQuery.checkObjectReadAuth(getUserId(), Long.valueOf(spaceId));
      }

      @Override
      protected Page<SpaceObject> process() {
        Page<SpaceObject> page = spaceObjectSearchRepo
            .find(criteria, pageable, SpaceObject.class, null);
        spaceObjectQuery.setObjectSummary(page.getContent());
        return page;
      }
    }.execute();
  }
}




