package cloud.xcan.angus.core.storage.interfaces.space.facade.vo;

import cloud.xcan.angus.spec.jackson.serializer.DataSizeFormat;
import cloud.xcan.angus.spec.unit.DataSize;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SpaceSummaryVo implements Serializable {

  @DataSizeFormat
  private DataSize tenantQuotaSize;

  //@DataSizeFormat
  private DataSize quotaSize;

  @DataSizeFormat
  private DataSize availableSize;

  @DataSizeFormat
  private DataSize usedSize;

  private Double usage;

  private long subDirectoryNum;

  private long subFileNum;

}
