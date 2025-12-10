package cloud.xcan.angus.core.storage.application.cmd.space;

import cloud.xcan.angus.core.storage.domain.bucket.config.BucketBizConfig;
import cloud.xcan.angus.core.storage.domain.space.Space;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public interface SpaceCmd {

  IdKey<Long, Object> add(Space space);

  void update(Space space);

  void delete(Set<Long> ids, boolean deleteStore);

  void authEnabled(Long spaceId, Boolean enabled);

  Space findAndInitByBizKey(BucketBizConfig config, String bizKey);

  @NotNull
  Space addCustomized(BucketBizConfig config, String spaceName, Long projectId);

  @NotNull
  Space addNonCustomized(BucketBizConfig config);
}
