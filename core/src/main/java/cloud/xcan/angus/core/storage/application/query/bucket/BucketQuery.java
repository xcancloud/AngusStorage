package cloud.xcan.angus.core.storage.application.query.bucket;

import cloud.xcan.angus.core.storage.domain.bucket.Bucket;
import cloud.xcan.angus.core.storage.domain.bucket.config.BucketBizConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


public interface BucketQuery {

  Bucket detail(String name);

  Page<Bucket> find(Specification<Bucket> spec, Pageable pageable);

  Bucket checkAndFind(String name);

  Bucket find0(String name);

  boolean isBucketEmpty(String bucketName);

  boolean isBucketBizEmpty(String bucketName, String bizKey);

  BucketBizConfig checkAndFindByBizKey(String bizKey);

  void checkOperateBucketPermission();
}
