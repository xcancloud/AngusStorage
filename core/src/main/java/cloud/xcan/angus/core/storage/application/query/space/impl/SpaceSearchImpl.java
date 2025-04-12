package cloud.xcan.angus.core.storage.application.query.space.impl;

import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.findFirstValueAndRemove;
import static cloud.xcan.angus.core.storage.application.query.space.impl.SpaceQueryImpl.assembleFilterParam;

import cloud.xcan.angus.api.manager.UserManager;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.ProtocolAssert;
import cloud.xcan.angus.core.storage.application.query.space.SpaceAuthQuery;
import cloud.xcan.angus.core.storage.application.query.space.SpaceSearch;
import cloud.xcan.angus.core.storage.domain.bucket.config.BucketBizConfigRepo;
import cloud.xcan.angus.core.storage.domain.space.Space;
import cloud.xcan.angus.core.storage.domain.space.SpaceSearchRepo;
import java.util.Set;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Biz
public class SpaceSearchImpl implements SpaceSearch {

  @Resource
  private SpaceSearchRepo scenarioSearchRepo;

  @Resource
  private BucketBizConfigRepo bucketBizConfigRepo;

  @Resource
  private SpaceAuthQuery spaceAuthQuery;

  @Resource
  private UserManager userManager;

  @Override
  public Page<Space> search(Set<SearchCriteria> criteria, Pageable pageable, Class<Space> clazz) {
    return new BizTemplate<Page<Space>>() {
      @Override
      protected void checkParams() {
        // NOOP
      }

      @Override
      protected Page<Space> process() {
        String appCode = findFirstValueAndRemove(criteria, "appCode");
        ProtocolAssert.assertNotNull(appCode, "appCode is required");

        assembleFilterParam(criteria, spaceAuthQuery, userManager, bucketBizConfigRepo, appCode);

        return scenarioSearchRepo.find(criteria, pageable, Space.class, null);
      }
    }.execute();
  }
}




