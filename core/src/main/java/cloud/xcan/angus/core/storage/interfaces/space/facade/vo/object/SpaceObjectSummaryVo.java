package cloud.xcan.angus.core.storage.interfaces.space.facade.vo.object;

import cloud.xcan.angus.spec.jackson.serializer.DataSizeFormat;
import cloud.xcan.angus.spec.unit.DataSize;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SpaceObjectSummaryVo implements Serializable {

  @DataSizeFormat
  private DataSize usedSize;

  private long subDirectoryNum;

  private long subFileNum;

}
