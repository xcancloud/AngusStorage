package cloud.xcan.angus.core.storage.infra.search;


import cloud.xcan.angus.core.jpa.repository.AbstractSearchRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObject;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObjectListRepo;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObjectSearchRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class SpaceObjectSearchRepoMysql extends AbstractSearchRepository<SpaceObject> implements
    SpaceObjectSearchRepo {

  @Resource
  private SpaceObjectListRepo spaceObjectListRepo;

  /**
   * Non-main mainClz conditions and joins need to be assembled by themselves
   */
  @Override
  public StringBuilder getSqlTemplate(Set<SearchCriteria> criteria, Class<SpaceObject> mainClz,
      Object[] params, String... matches) {
    return spaceObjectListRepo.getSqlTemplate0(getSearchMode(), criteria, mainClz,
        "object_space_object", matches);
  }

  @Override
  public String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params) {
    return spaceObjectListRepo.getReturnFieldsCondition(criteria, params);
  }

  @Override
  public SearchMode getSearchMode() {
    return SearchMode.MATCH;
  }

}
