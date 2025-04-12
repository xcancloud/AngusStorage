package cloud.xcan.angus.core.storage.interfaces.space.facade.to;

import cloud.xcan.angus.api.enums.FileType;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SpaceObjectNavigationTo implements Serializable {

  private Long id;

  private FileType type;

  private String name;

  private int level;

}
