package cloud.xcan.angus.core.storage.interfaces.space.facade.vo.object;

import cloud.xcan.angus.remote.NameJoinField;
import cloud.xcan.angus.core.storage.interfaces.space.facade.to.SpaceObjectNavigationTo;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SpaceObjectNavigationVo implements Serializable {

  private Long spaceId;

  @NameJoinField(id = "spaceId", repository = "spaceRepo")
  private String spaceName;

  private SpaceObjectNavigationTo current;

  private List<SpaceObjectNavigationTo> parentChain;

}
