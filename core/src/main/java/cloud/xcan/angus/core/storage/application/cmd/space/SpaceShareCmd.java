package cloud.xcan.angus.core.storage.application.cmd.space;

import cloud.xcan.angus.core.storage.domain.space.share.SpaceShare;
import java.util.HashSet;

public interface SpaceShareCmd {

  SpaceShare add(SpaceShare share);

  SpaceShare quickAdd(SpaceShare share);

  void update(SpaceShare share);

  void delete(HashSet<Long> ids);

}
