package cloud.xcan.angus.core.storage.domain.file;


import cloud.xcan.angus.persistence.jpa.repository.BaseRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface ObjectFileRepo extends BaseRepository<ObjectFile, Long> {

  @Query(value = "SELECT * FROM storage_object_file om WHERE om.store_deleted=0 AND om.unique_name=?1", nativeQuery = true)
  Optional<ObjectFile> findValidByUniqueName(String filename);

  @Query(value = "SELECT * FROM storage_object_file om WHERE om.id=?1", nativeQuery = true)
  Optional<ObjectFile> findValidById(Long id);

  @Query(value = "SELECT count(*) FROM storage_object_file om WHERE om.bucket_name =?1 limit 1", nativeQuery = true)
  boolean findByBucketNameLimit1(String bucketName);

  @Query(value = "SELECT count(*) FROM storage_object_file om WHERE om.bucket_name =?1 AND om.biz_key=?2 limit 1", nativeQuery = true)
  boolean findByBucketNameLimit1(String bucketName, String bizKey);

  @Modifying
  @Query(value = "DELETE FROM storage_object_file WHERE bucket_name = ?1 and biz_key = ?2", nativeQuery = true)
  void deleteByBucketNameAndBizKey(String bucketName, String bizKey);

  @Modifying
  @Query(value = "DELETE FROM storage_object_file WHERE space_id in ?1", nativeQuery = true)
  void deleteBySpaceIdIn(Collection<Long> spaceIds);

  @Modifying
  @Query(value = "UPDATE storage_object_file set store_deleted = 1 AND id in ("
      + " SELECT fid FROM storage_space_object WHERE id in ?1 AND type = 'FILE' ) ", nativeQuery = true)
  void updateToBeDeleted(List<Long> allDeletedObjectIds);

}
