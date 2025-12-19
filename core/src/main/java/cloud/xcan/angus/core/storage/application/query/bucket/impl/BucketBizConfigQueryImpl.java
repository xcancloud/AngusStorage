package cloud.xcan.angus.core.storage.application.query.bucket.impl;


import cloud.xcan.angus.core.storage.application.query.bucket.BucketBizConfigQuery;
import cloud.xcan.angus.core.storage.domain.bucket.config.BucketBizConfig;
import cloud.xcan.angus.core.storage.domain.bucket.config.BucketBizConfigRepo;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.Objects;

@org.springframework.stereotype.Service
public class BucketBizConfigQueryImpl implements BucketBizConfigQuery {

  @Resource
  private BucketBizConfigRepo bucketBizConfigRepo;

  @Override
  public BucketBizConfig findByBizKey(String bizKey) {
    BucketBizConfig config = bucketBizConfigRepo.findByBizKey(bizKey);
    if (Objects.isNull(config)) {
      throw ResourceNotFound.of(bizKey, "BucketBizConfig");
    }
    return config;
  }
}
