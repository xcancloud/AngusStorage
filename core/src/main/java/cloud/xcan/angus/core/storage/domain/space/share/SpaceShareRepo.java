package cloud.xcan.angus.core.storage.domain.space.share;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Collection;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface SpaceShareRepo extends BaseRepository<SpaceShare, Long> {

  SpaceShare findByQuickObjectId(Long quickObjectId);

  @Modifying
  @Query(value = "DELETE FROM object_space_share WHERE id in ?1", nativeQuery = true)
  void deleteByIdIn(Collection<Long> ids);

}
