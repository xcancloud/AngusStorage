package cloud.xcan.angus.core.storage.domain.space.object;

import cloud.xcan.angus.persistence.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SpaceObjectSearchRepo extends CustomBaseRepository<SpaceObject> {

}
