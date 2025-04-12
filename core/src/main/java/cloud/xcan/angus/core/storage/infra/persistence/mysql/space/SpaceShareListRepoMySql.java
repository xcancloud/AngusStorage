package cloud.xcan.angus.core.storage.infra.persistence.mysql.space;

import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.getFilterInFirstValue;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getTenantId;

import cloud.xcan.angus.core.jpa.repository.AbstractSearchRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.core.storage.domain.space.share.SpaceShare;
import cloud.xcan.angus.core.storage.domain.space.share.SpaceShareListRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.spec.utils.ObjectUtils;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class SpaceShareListRepoMySql extends AbstractSearchRepository<SpaceShare> implements
    SpaceShareListRepo {

  /**
   * Non-main mainClz conditions and joins need to be assembled by themselves
   */
  @Override
  public StringBuilder getSqlTemplate(Set<SearchCriteria> criteria, Class<SpaceShare> mainClz,
      Object[] params, String... matches) {
    return getSqlTemplate0(getSearchMode(), criteria, mainClz, "object_space_share",
        matches);
  }

  @Override
  public StringBuilder getSqlTemplate0(SearchMode mode, Set<SearchCriteria> criteria,
      Class<SpaceShare> mainClz, String tableName, String... matches) {
    String mainAlis = "a";
    // Assemble mainClz table
    StringBuilder sql = new StringBuilder(
        "SELECT %s FROM " + tableName + " " + mainAlis + " WHERE 1=1 ");
    // Assemble mainClz Conditions
    sql.append(getCriteriaAliasCondition(criteria, mainClz, mainAlis, mode, false, matches));

    // Assemble non mainClz authObjectId Conditions
    assembleShareAuthJoinTargetCondition(sql, criteria);
    return sql;
  }

  @Override
  public String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params) {
    return "a.*";
  }

  public static void assembleShareAuthJoinTargetCondition(StringBuilder sql,
      Set<SearchCriteria> criteria) {
    String authObjectIds = getFilterInFirstValue(criteria, "authObjectId");
    if (ObjectUtils.isEmpty(authObjectIds)) {
      // Admin query all resource when authObjectIds is empty
      return;
    }
    // Non-Admin query own resource when authObjectIds is not empty
    long tenantId = getTenantId();
    // @formatter:off
    sql.append(
        " AND a.space_id IN (SELECT a1.id FROM object_space a1 WHERE a1.tenant_id=" + tenantId + " AND a1.auth = 0 "
            + " UNION SELECT a2.id FROM object_space a2 INNER JOIN object_space_auth a3 ON a2.id = a3.space_id "
            +     " AND a2.tenant_id=" + tenantId + " AND a3.tenant_id=" + tenantId + " AND a2.auth = 1 AND a3.auth_object_id IN (")
        .append(authObjectIds).append(")").append(" AND a3.auth_data LIKE CONCAT('%','SHARE','%'))");
    // @formatter:on
  }

}
