package cloud.xcan.angus.core.storage.domain.space;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.jpa.repository.NameJoinRepository;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SpaceRepo extends BaseRepository<Space, Long>, NameJoinRepository<Space, Long> {

  int countByName(String name);

  long countByTenantId(Long optTenantId);

  List<Space> findByTenantId(Long optTenantId);

  List<Space> findByNameAndIdNot(String name, Long id);

  @Query(value = "SELECT id FROM object_space WHERE bucket_name = ?1", nativeQuery = true)
  Set<Long> findIdByBucketName(String name);

  @Query(value = "SELECT s.* FROM object_space s WHERE s.id IN (SELECT so.space_id FROM object_space_object so WHERE so.space_id in (?1))  limit 1", nativeQuery = true)
  Space findNotEmptyBySpaceIdInLimit1(Collection<Long> spaceIds);

  @Query(value = "SELECT s.* FROM object_space s WHERE s.biz_key = ?1 limit 1", nativeQuery = true)
  Space findByBizKeyLimit1(String bizKey);

  @Query(value = "SELECT s.* FROM object_space s WHERE s.tenant_id = ?1 AND s.biz_key = ?2 limit 1", nativeQuery = true)
  Space findByTenantIdAndBizKeyLimit1(Long optTenantId, String bizKey);

  @Query(value = "SELECT id FROM object_space WHERE id IN ?1 AND auth = ?2 ", nativeQuery = true)
  List<Long> findIds0ByIdInAndAuth(Collection<Long> scriptIds, boolean auth);

  @Modifying
  @Query("update Space s set s.auth=?2 where s.id=?1")
  void updateAuthById(Long id, Boolean auth);

  @Modifying
  @Query(value = "DELETE FROM object_space WHERE id in ?1", nativeQuery = true)
  void deleteByIdIn(Collection<Long> ids);

}
