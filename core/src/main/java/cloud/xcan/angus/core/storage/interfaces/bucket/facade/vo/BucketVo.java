package cloud.xcan.angus.core.storage.interfaces.bucket.facade.vo;

import cloud.xcan.angus.remote.NameJoinField;
import cloud.xcan.angus.core.storage.infra.store.model.AccessControl;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class BucketVo implements Serializable {

  private Long id;

  private String name;

  private AccessControl acl;

  private Boolean tenantCreated;

  private List<BucketBizConfigVo> configs;

  private Long createdBy;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String createdByName;

}
