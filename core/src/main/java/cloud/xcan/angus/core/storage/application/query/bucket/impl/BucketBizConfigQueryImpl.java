package cloud.xcan.angus.core.storage.application.query.bucket.impl;

import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.storage.application.query.bucket.BucketBizConfigQuery;
import cloud.xcan.angus.core.storage.domain.bucket.config.BucketBizConfig;
import cloud.xcan.angus.core.storage.domain.bucket.config.BucketBizConfigRepo;
import java.util.Objects;
import jakarta.annotation.Resource;

@Biz
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
