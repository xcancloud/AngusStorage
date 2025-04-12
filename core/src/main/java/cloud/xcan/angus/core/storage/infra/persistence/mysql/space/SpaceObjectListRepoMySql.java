package cloud.xcan.angus.core.storage.infra.persistence.mysql.space;

import cloud.xcan.angus.core.jpa.repository.AbstractSearchRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObject;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObjectListRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class SpaceObjectListRepoMySql extends AbstractSearchRepository<SpaceObject> implements
    SpaceObjectListRepo {

  /**
   * Non-main mainClz conditions and joins need to be assembled by themselves
   */
  @Override
  public StringBuilder getSqlTemplate(Set<SearchCriteria> criteria, Class<SpaceObject> mainClz,
      Object[] params, String... matches) {
    return getSqlTemplate0(getSearchMode(), criteria, mainClz, "object_space_object", matches);
  }

  @Override
  public StringBuilder getSqlTemplate0(SearchMode mode, Set<SearchCriteria> criteria,
      Class<SpaceObject> mainClz, String tableName, String... matches) {
    String mainAlis = "a";
    // Assemble mainClz table
    StringBuilder sql = new StringBuilder(
        "SELECT %s FROM " + tableName + " " + mainAlis + " WHERE 1=1 ");

    // Assemble mainClz Conditions
    sql.append(getCriteriaAliasCondition(criteria, mainClz, mainAlis, mode, false,
        matches));
    return sql;
  }

  @Override
  public String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params) {
    return "a.*";
  }

}
