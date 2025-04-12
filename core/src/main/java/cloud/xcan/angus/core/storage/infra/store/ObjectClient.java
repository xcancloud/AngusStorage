package cloud.xcan.angus.core.storage.infra.store;

import cloud.xcan.angus.api.enums.PlatformStoreType;
import cloud.xcan.angus.core.storage.domain.bucket.Bucket;
import cloud.xcan.angus.core.storage.infra.store.operation.BucketOperation;
import cloud.xcan.angus.core.storage.infra.store.operation.ObjectOperation;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ObjectClient implements BucketOperation, ObjectOperation {

  /**
   * initialization and directory
   */
  abstract public void init(List<Bucket> buckets) throws Exception;

  abstract public PlatformStoreType getStoreType();

  abstract public ObjectProperties getProperties();

}
