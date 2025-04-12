package cloud.xcan.angus.core.storage.application.cmd.space;

import cloud.xcan.angus.core.storage.domain.bucket.config.BucketBizConfig;
import cloud.xcan.angus.core.storage.domain.space.Space;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.Set;

public interface SpaceCmd {

  IdKey<Long, Object> add(Space space);

  void update(Space space);

  void delete(Set<Long> ids, boolean deleteStore);

  void authEnabled(Long spaceId, Boolean enabled);

  Space findAndInitByBizKey(BucketBizConfig config, String bizKey);

}
