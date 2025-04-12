package cloud.xcan.angus.core.storage.infra.search;


import cloud.xcan.angus.core.jpa.repository.AbstractSearchRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.core.storage.domain.space.share.SpaceShare;
import cloud.xcan.angus.core.storage.domain.space.share.SpaceShareSearchRepo;
import cloud.xcan.angus.core.storage.infra.persistence.mysql.space.SpaceShareListRepoMySql;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.stereotype.Repository;

/**
 * Complete assembly custom sql method reference: {@link SpaceShareListRepoMySql}
 */
@Repository
public class SpaceShareSearchRepoMysql extends AbstractSearchRepository<SpaceShare> implements
    SpaceShareSearchRepo {

  @Resource
  private SpaceShareListRepoMySql spaceShareListRepoMySql;

  /**
   * Non-main mainClz conditions and joins need to be assembled by themselves
   */
  @Override
  public StringBuilder getSqlTemplate(Set<SearchCriteria> criteria, Class<SpaceShare> mainClz,
      Object[] params, String... matches) {
    return spaceShareListRepoMySql.getSqlTemplate0(getSearchMode(), criteria, mainClz,
        "object_space_share", matches);
  }

  @Override
  public String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params) {
    return spaceShareListRepoMySql.getReturnFieldsCondition(criteria, params);
  }

  @Override
  public SearchMode getSearchMode() {
    return SearchMode.MATCH;
  }

}
