package cloud.xcan.angus.core.storage.application.cmd.bucket;

import cloud.xcan.angus.core.storage.domain.bucket.config.BucketBizConfig;

public interface BucketBizConfigCmd {

  void config(BucketBizConfig grant);

  void configDelete(String bizKey);
}
