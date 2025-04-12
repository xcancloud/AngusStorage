package cloud.xcan.angus.core.storage.application.query.bucket;

import cloud.xcan.angus.core.storage.domain.bucket.config.BucketBizConfig;

public interface BucketBizConfigQuery {

  BucketBizConfig findByBizKey(String bizKey);
}
