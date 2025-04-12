package cloud.xcan.angus.core.storage.infra.search;

import cloud.xcan.angus.core.jpa.repository.AbstractSearchRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.core.storage.domain.space.Space;
import cloud.xcan.angus.core.storage.domain.space.SpaceListRepo;
import cloud.xcan.angus.core.storage.domain.space.SpaceSearchRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class SpaceSearchRepoMysql extends AbstractSearchRepository<Space> implements
    SpaceSearchRepo {

  @Resource
  private SpaceListRepo spaceListRepo;

  /**
   * Non-main mainClz conditions and joins need to be assembled by themselves
   */
  @Override
  public StringBuilder getSqlTemplate(Set<SearchCriteria> criteria, Class<Space> mainClz,
      Object[] params, String... matches) {
    return spaceListRepo.getSqlTemplate0(getSearchMode(), criteria, mainClz,
        "object_space", matches);
  }

  @Override
  public String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params) {
    return spaceListRepo.getReturnFieldsCondition(criteria, params);
  }

  @Override
  public SearchMode getSearchMode() {
    return SearchMode.MATCH;
  }
}
