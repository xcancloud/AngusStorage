package cloud.xcan.angus.core.storage.application.query.space.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertNotNull;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertUnauthorized;
import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.findFirstValue;
import static cloud.xcan.angus.remote.CommonMessage.SHARE_PASSWORD_ERROR_T;
import static cloud.xcan.angus.remote.CommonMessage.SHARE_TOKEN_ERROR_T;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.jpa.criteria.CriteriaUtils;
import cloud.xcan.angus.core.storage.application.query.space.SpaceAuthQuery;
import cloud.xcan.angus.core.storage.application.query.space.SpaceObjectQuery;
import cloud.xcan.angus.core.storage.application.query.space.SpaceShareQuery;
import cloud.xcan.angus.core.storage.application.query.space.SpaceShareSearch;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObject;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObjectRepo;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObjectSearchRepo;
import cloud.xcan.angus.core.storage.domain.space.share.SpaceShare;
import cloud.xcan.angus.core.storage.domain.space.share.SpaceShareSearchRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Biz
public class SpaceShareSearchImpl implements SpaceShareSearch {

  @Resource
  private SpaceShareQuery spaceShareQuery;

  @Resource
  private SpaceObjectQuery spaceObjectQuery;

  @Resource
  private SpaceAuthQuery spaceAuthQuery;

  @Resource
  private SpaceObjectRepo spaceObjectRepo;

  @Resource
  private SpaceObjectSearchRepo spaceObjectSearchRepo;

  @Resource
  private SpaceShareSearchRepo spaceShareSearchRepo;

  @Override
  public Page<SpaceShare> search(Set<SearchCriteria> criteria, Pageable pageable,
      Class<SpaceShare> clazz) {
    return new BizTemplate<Page<SpaceShare>>() {
      String spaceId;

      @Override
      protected void checkParams() {
        spaceId = findFirstValue(criteria, "spaceId");
        assertNotNull(spaceId, "spaceId is required");
        spaceAuthQuery.checkShareAuth(getUserId(), Long.valueOf(spaceId));
      }

      @Override
      protected Page<SpaceShare> process() {
        return spaceShareSearchRepo.find(criteria, pageable, SpaceShare.class,
            new String[]{"remark"});
      }
    }.execute();
  }

  @Override
  public Page<SpaceObject> objectSearchPub(Set<SearchCriteria> criteria, PageRequest pageable,
      Class<SpaceObject> clazz) {
    return new BizTemplate<Page<SpaceObject>>(false) {
      SpaceShare spaceShareDb;
      final String spaceId = findFirstValue(criteria, "spaceId");

      @Override
      protected void checkParams() {
        assertNotNull(spaceId, "spaceId is required");
        String sid = findFirstValue(criteria, "sid");
        assertNotNull(sid, "sid is required");
        String spt = findFirstValue(criteria, "spt");
        assertNotNull(spt, "spt is required");
        String password = findFirstValue(criteria, "password");

        // Check share exits
        spaceShareDb = spaceShareQuery.checkAndFind(Long.parseLong(sid));
        // Check spt(public token) authorization
        assertUnauthorized(spt.equals(spaceShareDb.getPublicToken()), SHARE_TOKEN_ERROR_T);
        // Check password where public0 = false
        assertUnauthorized(spaceShareDb.getPublic0() ||
            spaceShareDb.getPassword().equals(password), SHARE_PASSWORD_ERROR_T);
      }

      @Override
      protected Page<SpaceObject> process() {
        // Set filter tenant
        PrincipalContext.get().setTenantId(spaceShareDb.getTenantId());

        if (!spaceShareDb.getAll()) {
          int minLevel = spaceObjectRepo.findMinLevelByIdIn(Long.parseLong(spaceId),
              spaceShareDb.getObjectIds());
          // Return to the first level of sharing
          if (minLevel > 0) {
            CriteriaUtils.containsAndRemove(criteria, "parentDirectoryId");
            criteria.add(SearchCriteria.equal("level", minLevel));
          }
          // Filter sharing objectIds
          criteria.add(SearchCriteria.in("id", spaceShareDb.getObjectIds()));
        }

        Page<SpaceObject> page = spaceObjectSearchRepo
            .find(criteria, pageable, SpaceObject.class, null);
        spaceObjectQuery.setObjectStatsAndSummary(page.getContent());
        return page;
      }
    }.execute();
  }

}
