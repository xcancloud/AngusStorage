package cloud.xcan.angus.core.storage.interfaces.bucket.facade.to;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class BucketAssignBizTo implements Serializable {

  private String bizKey;

  private String remark;

}
