package cloud.xcan.angus.core.storage.infra.persistence.mysql.space;

import cloud.xcan.angus.core.storage.domain.space.SpaceRepo;
import org.springframework.stereotype.Repository;

@Repository("spaceRepo")
public interface SpaceRepoMySql extends SpaceRepo {

}
