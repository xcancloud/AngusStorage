package cloud.xcan.angus.core.storage.domain.space.share;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SpaceShareSearchRepo extends CustomBaseRepository<SpaceShare> {

}
