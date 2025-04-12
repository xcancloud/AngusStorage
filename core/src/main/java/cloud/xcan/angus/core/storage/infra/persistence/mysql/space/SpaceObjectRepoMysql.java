package cloud.xcan.angus.core.storage.infra.persistence.mysql.space;

import cloud.xcan.angus.core.storage.domain.space.object.SpaceObjectRepo;
import org.springframework.stereotype.Repository;

@Repository("spaceObjectRepo")
public interface SpaceObjectRepoMysql extends SpaceObjectRepo {

}
