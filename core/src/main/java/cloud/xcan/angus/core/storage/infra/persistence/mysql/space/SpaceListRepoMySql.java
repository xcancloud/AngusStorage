package cloud.xcan.angus.core.storage.infra.persistence.mysql.space;

import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.assembleGrantPermissionCondition;
import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.getFilterInFirstValue;
import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.getInConditionValue;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;

import cloud.xcan.angus.core.jpa.repository.AbstractSearchRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.core.storage.domain.space.Space;
import cloud.xcan.angus.core.storage.domain.space.SpaceListRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.spec.utils.ObjectUtils;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class SpaceListRepoMySql extends AbstractSearchRepository<Space> implements
    SpaceListRepo {

  /**
   * Non-main mainClz conditions and joins need to be assembled by themselves
   */
  @Override
  public StringBuilder getSqlTemplate(Set<SearchCriteria> criteria, Class<Space> mainClz,
      Object[] params, String... matches) {
    return getSqlTemplate0(getSearchMode(), criteria, mainClz, "object_space", matches);
  }

  @Override
  public StringBuilder getSqlTemplate0(SearchMode mode, Set<SearchCriteria> criteria,
      Class<Space> mainClz, String tableName, String... matches) {
    String mainAlis = "a";
    // Assemble mainClz table
    StringBuilder sql = new StringBuilder(
        "SELECT %s FROM " + tableName + " " + mainAlis + " WHERE 1=1 ");

    // Assemble mainClz Conditions
    sql.append(getCriteriaAliasCondition(criteria, mainClz, mainAlis, mode, false, matches));

    // Assemble non mainClz authObjectId and grant0 Conditions
    assembleAuthJoinTargetCondition(sql, criteria);
    return sql;
  }

  @Override
  public String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params) {
    return "a.*";
  }

  public static void assembleAuthJoinTargetCondition(StringBuilder sql,
      Set<SearchCriteria> criteria) {
    String authObjectIds = getFilterInFirstValue(criteria, "authObjectId");
    if (ObjectUtils.isEmpty(authObjectIds)) {
      // Admin query all resource when authObjectIds is empty. No query conditions!
      return;
    }
    String grantFilter = assembleGrantPermissionCondition(criteria, "a3", "GRANT");
    // Non-Admin query own resource when authObjectIds is not empty
    String authObjectIdsInValue = getInConditionValue(authObjectIds);
    long tenantId = getOptTenantId();
    // @formatter:off
    if (ObjectUtils.isEmpty(grantFilter)){
      // Query has `VIEW` permission resource
      sql.append(
          " AND a.id IN (SELECT a1.id FROM object_space a1 WHERE a1.tenant_id=" + tenantId + " AND a1.auth = 0 "
              + "UNION SELECT a2.id FROM object_space a2 INNER JOIN object_space_auth a3 "
              +   "ON a2.id = a3.space_id AND a3.auth_object_id IN ")
          .append(authObjectIdsInValue).append(" AND a2.auth = 1 )");
    }else {
      // Query has `GRANT` permission resource
      sql.append(
          " AND a.id IN (SELECT a2.id FROM object_space a2 INNER JOIN object_space_auth a3 "
              +   "ON a2.id = a3.space_id AND a3.auth_object_id IN ")
          .append(authObjectIdsInValue).append(grantFilter + ")");
    }
    // @formatter:on
  }


}
