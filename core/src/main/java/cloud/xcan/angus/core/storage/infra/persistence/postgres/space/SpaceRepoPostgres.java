package cloud.xcan.angus.core.storage.infra.persistence.postgres.space;

import cloud.xcan.angus.core.storage.domain.space.SpaceRepo;
import org.springframework.stereotype.Repository;

@Repository("spaceRepo")
public interface SpaceRepoPostgres extends SpaceRepo {

}
