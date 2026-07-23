package cloud.xcan.angus.core.storage.domain.space.object;

import cloud.xcan.angus.api.enums.FileType;
import cloud.xcan.angus.api.enums.PlatformStoreType;
import cloud.xcan.angus.core.biz.NameJoinRepository;
import cloud.xcan.angus.persistence.jpa.repository.BaseRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SpaceObjectRepo extends BaseRepository<SpaceObject, Long>,
    NameJoinRepository<SpaceObject, Long> {

  int countBySpaceIdAndParentDirectoryIdAndNameAndType(Long spaceId, Long parentDirectoryId,
      String name, FileType type);

  int countBySpaceIdAndParentDirectoryIdAndNameAndIdNot(Long spaceId, Long parentDirectoryId,
      String name, Long id);

  @Query(value = "SELECT SUM(size) FROM storage_space_object WHERE tenant_id = ?1 AND type = 'FILE'", nativeQuery = true)
  Long sumSizeByTenantId(Long optTenantId);

  @Query(value = "SELECT SUM(size) FROM storage_space_object WHERE space_id = ?1 AND type = 'FILE'", nativeQuery = true)
  Long sumSizeBySpaceId(Long spaceId);

  List<SpaceObject> findByIdInAndType(Set<Long> ids, FileType type);

  List<SpaceObject> findAllBySpaceIdAndIdIn(Long spaceId, Set<Long> objectIds);

  List<SpaceObject> findAllBySpaceIdInAndIdNotIn(Set<Long> spaceIds, Set<Long> objectIds);

  List<SpaceObject> findAllBySpaceIdIn(Set<Long> spaceIds);

  @Query(value = "SELECT distinct space_id FROM storage_space_object WHERE id in ?1", nativeQuery = true)
  List<Long> findSpaceIdByIdIn(Set<Long> ids);

  List<SpaceObject> findByParentDirectoryIdAndType(long fileRootParentId, String type);

  List<SpaceObject> findByParentDirectoryIdAndNameAndType(long fileRootParentId, String name,
      String type);

  @Query(value = "SELECT parent_like_id FROM storage_space_object WHERE id = ?1", nativeQuery = true)
  Optional<String> findParentLikeIdById(Long id);

  @Query(value = "SELECT id FROM storage_space_object WHERE parent_like_id LIKE CONCAT(?1,'%')", nativeQuery = true)
  List<Long> findIdByParentLikeId(String subParentLikeId);

  @Query(value = "SELECT * FROM storage_space_object WHERE parent_like_id LIKE CONCAT(?1,'%')", nativeQuery = true)
  List<SpaceObject> findByParentLikeId(String subParentLikeId);

  @Query(value = "SELECT level FROM storage_space_object WHERE space_id = ?1 AND id IN (?2) ORDER BY level ASC LIMIT 1", nativeQuery = true)
  int findMinLevelByIdIn(Long spaceId, Collection<Long> ids);

  /**
   * 是否存在与指定 storeType 不同的对象。
   * <p>勿用 native {@code count(*)} 映射 boolean（驱动返回 Long，会 ClassCastException）。
   */
  boolean existsByStoreTypeNot(PlatformStoreType storeType);

  @Modifying
  @Query(value = "DELETE FROM storage_space_object WHERE space_id in ?1", nativeQuery = true)
  void deleteBySpaceIdIn(Collection<Long> spaceIds);

  @Modifying
  @Query(value =
      "UPDATE storage_space_object SET space_id = ?1, level = level + ?2, parent_like_id = REPLACE(parent_like_id, ?3, ?4) "
          + " WHERE parent_like_id LIKE CONCAT(?3,'%')", nativeQuery = true)
  void updateSubParentByOldParentLikeId(Long targetSpaceId, int newDiffLevel,
      String oldMovedSubParentLikeId, String newMovedSubParentLikeId);

  @Modifying
  @Query(value = "DELETE FROM storage_space_object WHERE id in ?1", nativeQuery = true)
  void deleteByIdIn(List<Long> ids);

}
