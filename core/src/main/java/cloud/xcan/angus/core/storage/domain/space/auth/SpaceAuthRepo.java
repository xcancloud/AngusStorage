package cloud.xcan.angus.core.storage.domain.space.auth;

import cloud.xcan.angus.api.enums.AuthObjectType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SpaceAuthRepo extends BaseRepository<SpaceAuth, Long> {

  List<SpaceAuth> findAllBySpaceIdAndAuthObjectIdIn(Long spaceId, List<Long> orgIds);

  List<SpaceAuth> findAllByAuthObjectIdIn(Collection<Long> orgIds);

  List<SpaceAuth> findAllBySpaceIdInAndAuthObjectIdIn(Collection<Long> spaceIds,
      Collection<Long> orgIds);

  Long countBySpaceIdAndAuthObjectIdAndAuthObjectType(Long spaceId, Long authObjectId,
      AuthObjectType authObjectType);

  @Modifying
  @Query(value = "DELETE FROM object_space_auth WHERE space_id in ?1", nativeQuery = true)
  void deleteBySpaceIdIn(Collection<Long> spaceIds);

  @Modifying
  @Query(value = "DELETE FROM object_space_auth WHERE space_id = ?1 and creator = ?2", nativeQuery = true)
  void deleteBySpaceIdAndCreator(Long spaceId, Boolean creator);

}
