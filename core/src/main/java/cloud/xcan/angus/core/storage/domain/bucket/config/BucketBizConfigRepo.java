package cloud.xcan.angus.core.storage.domain.bucket.config;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.annotations.DoInFuture;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface BucketBizConfigRepo extends BaseRepository<BucketBizConfig, Long> {

  BucketBizConfig findByBizKey(String bizKey);

  List<BucketBizConfig> findByBucketName(String bucketName);

  List<BucketBizConfig> findByBucketNameIn(Collection<String> bucketName);

  @Modifying
  @Query(value = "delete from bucket_biz_config where bucket_name=?1 and biz_key=?2", nativeQuery = true)
  void deleteByBucketNameAndBizKey(String bucketName, String bizKey);

  @DoInFuture("Use cache instead of query")
  @Query(value = "SELECT a.app_admin_code FROM bucket_biz_config a INNER JOIN object_space s ON a.biz_key = s.biz_key WHERE s.id = ? limit 1", nativeQuery = true)
  String findAppAdminCodeBySpaceId(Long spaceId);

  @DoInFuture("Use cache instead of query")
  @Query(value = "SELECT a.app_admin_code FROM bucket_biz_config a WHERE a.biz_key = ? limit 1", nativeQuery = true)
  String findAppAdminCodeByBizKey(String bizKey);

  @DoInFuture("Use cache instead of query")
  @Query(value = "SELECT a.app_admin_code FROM bucket_biz_config a WHERE a.app_code = ? limit 1", nativeQuery = true)
  String findAppAdminCodeByAppCode(String appCode);

}
