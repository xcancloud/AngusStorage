package cloud.xcan.angus.core.storage.domain.space;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class SpaceSummary implements Serializable {

  private Long tenantQuotaSize;

  private String quotaSize;

  private long availableSize;

  private long usedSize;

  private Double usage;

  private long subDirectoryNum;

  private long subFileNum;

}
