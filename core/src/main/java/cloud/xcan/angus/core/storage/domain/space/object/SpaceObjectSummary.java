package cloud.xcan.angus.core.storage.domain.space.object;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class SpaceObjectSummary implements Serializable {

  private long usedSize;

  private long subDirectoryNum;

  private long subFileNum;

}
