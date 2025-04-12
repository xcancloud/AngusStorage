package cloud.xcan.angus.core.storage.domain.file;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface ObjectFileRepo extends BaseRepository<ObjectFile, Long> {

  @Query(value = "SELECT * FROM object_file om WHERE om.store_deleted=0 AND om.unique_name=?1", nativeQuery = true)
  Optional<ObjectFile> findValidByUniqueName(String filename);

  @Query(value = "SELECT * FROM object_file om WHERE om.id=?1", nativeQuery = true)
  Optional<ObjectFile> findValidById(Long id);

  List<ObjectFile> findByParentDirectoryIdAndName(Long parentId, String name);

  @Query(value = "SELECT count(*) FROM object_file om WHERE om.bucket_name =?1 limit 1", nativeQuery = true)
  boolean findByBucketNameLimit1(String bucketName);

  @Query(value = "SELECT count(*) FROM object_file om WHERE om.bucket_name =?1 AND om.biz_key=?2 limit 1", nativeQuery = true)
  boolean findByBucketNameLimit1(String bucketName, String bizKey);

  @Query(value = "SELECT * FROM object_file om WHERE om.store_deleted =1 AND om.deleted_retry_num = 0 limit ?1", nativeQuery = true)
  List<ObjectFile> findStoreDeletedInFirst(Long count);

  @Query(value = "SELECT * FROM object_file om WHERE om.store_deleted =1 AND om.deleted_retry_num > 0 AND om.deleted_retry_num < ?1 ORDER BY deleted_retry_num ASC limit ?2", nativeQuery = true)
  List<ObjectFile> findStoreDeletedRetryLess(int maxRetryNum, Long count);

  @Modifying
  @Query(value = "DELETE FROM object_file WHERE bucket_name = ?1 and biz_key = ?2", nativeQuery = true)
  void deleteByBucketNameAndBizKey(String bucketName, String bizKey);

  @Modifying
  @Query(value = "DELETE FROM object_file WHERE space_id in ?1", nativeQuery = true)
  void deleteBySpaceIdIn(Collection<Long> spaceIds);

  @Modifying
  @Query(value = "UPDATE object_file set store_deleted = 1 AND id in ("
      + " SELECT fid FROM object_space_object WHERE id in ?1 AND type = 'FILE' ) ", nativeQuery = true)
  void updateToBeDeleted(List<Long> allDeletedObjectIds);

}
