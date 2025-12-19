package cloud.xcan.angus.core.storage.interfaces.bucket.facade.dto;

import cloud.xcan.angus.remote.PageQuery;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class BucketFindDto extends PageQuery implements Serializable {

  private Long id;

  private String name;

}
