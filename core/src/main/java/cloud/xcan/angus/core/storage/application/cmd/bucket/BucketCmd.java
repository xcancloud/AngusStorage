package cloud.xcan.angus.core.storage.application.cmd.bucket;

import cloud.xcan.angus.core.storage.domain.bucket.Bucket;
import cloud.xcan.angus.spec.experimental.IdKey;


public interface BucketCmd {

  IdKey<Long, Object> add(Bucket bucket);

  void delete(String name);

}
