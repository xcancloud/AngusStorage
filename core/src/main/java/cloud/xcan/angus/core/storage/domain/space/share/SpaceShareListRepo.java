package cloud.xcan.angus.core.storage.domain.space.share;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SpaceShareListRepo extends CustomBaseRepository<SpaceShare> {

  StringBuilder getSqlTemplate0(SearchMode mode,
      Set<SearchCriteria> criteria, Class<SpaceShare> mainClz, String tableName, String... matches);

  String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params);

}
