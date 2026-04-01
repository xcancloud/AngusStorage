package cloud.xcan.angus.core.storage.domain.space;

import cloud.xcan.angus.persistence.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SpaceSearchRepo extends CustomBaseRepository<Space> {

}
