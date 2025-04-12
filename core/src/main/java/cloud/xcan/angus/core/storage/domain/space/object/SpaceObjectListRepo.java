package cloud.xcan.angus.core.storage.domain.space.object;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SpaceObjectListRepo extends CustomBaseRepository<SpaceObject> {

  StringBuilder getSqlTemplate0(SearchMode mode, Set<SearchCriteria> criteria,
      Class<SpaceObject> mainClz, String tableName, String... matches);

  String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params);

}
