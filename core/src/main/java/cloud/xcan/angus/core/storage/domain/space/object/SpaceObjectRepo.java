package cloud.xcan.angus.core.storage.domain.space.object;

import cloud.xcan.angus.api.enums.FileType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.jpa.repository.NameJoinRepository;
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

  @Query(value = "SELECT SUM(size) FROM object_space_object WHERE tenant_id = ?1 AND type = 'FILE'", nativeQuery = true)
  long sumSizeByTenantId(Long optTenantId);

  @Query(value = "SELECT SUM(size) FROM object_space_object WHERE space_id = ?1 AND type = 'FILE'", nativeQuery = true)
  long sumSizeBySpaceId(Long spaceId);

  List<SpaceObject> findByIdInAndType(Set<Long> ids, FileType type);

  List<SpaceObject> findAllBySpaceIdAndIdIn(Long spaceId, Set<Long> objectIds);

  List<SpaceObject> findAllBySpaceIdIn(Set<Long> spaceIds);

  @Query(value = "SELECT distinct space_id FROM object_space_object WHERE id in ?1", nativeQuery = true)
  List<Long> findSpaceIdByIdIn(Set<Long> ids);

  List<SpaceObject> findByParentDirectoryIdAndType(long fileRootParentId, String type);

  List<SpaceObject> findByParentDirectoryIdAndNameAndType(long fileRootParentId, String name,
      String type);

  @Query(value = "SELECT parent_like_id FROM object_space_object WHERE id = ?1", nativeQuery = true)
  Optional<String> findParentLikeIdById(Long id);

  @Query(value = "SELECT id FROM object_space_object WHERE parent_like_id LIKE CONCAT(?1,'%')", nativeQuery = true)
  List<Long> findIdByParentLikeId(String subParentLikeId);

  @Query(value = "SELECT * FROM object_space_object WHERE parent_like_id LIKE CONCAT(?1,'%')", nativeQuery = true)
  List<SpaceObject> findByParentLikeId(String subParentLikeId);

  @Query(value = "SELECT level FROM object_space_object WHERE space_id = ?1 AND id IN (?2) ORDER BY level ASC LIMIT 1", nativeQuery = true)
  int findMinLevelByIdIn(Long spaceId, Collection<Long> ids);

  @Query(value = "SELECT count(*) FROM object_space_object WHERE store_type <> ?1 limit 1", nativeQuery = true)
  boolean existsByStoreTypeNot(String storeType);

  @Modifying
  @Query(value = "DELETE FROM object_space_object WHERE space_id in ?1", nativeQuery = true)
  void deleteBySpaceIdIn(Collection<Long> spaceIds);

  @Modifying
  @Query(value =
      "UPDATE object_space_object SET space_id = ?1, level = level + ?2, parent_like_id = REPLACE(parent_like_id, ?3, ?4) "
          + " WHERE parent_like_id LIKE CONCAT(?3,'%')", nativeQuery = true)
  void updateSubParentByOldParentLikeId(Long targetSpaceId, int newDiffLevel,
      String oldMovedSubParentLikeId, String newMovedSubParentLikeId);

  @Modifying
  @Query(value = "DELETE FROM object_space_object WHERE id in ?1", nativeQuery = true)
  void deleteByIdIn(List<Long> ids);


}
