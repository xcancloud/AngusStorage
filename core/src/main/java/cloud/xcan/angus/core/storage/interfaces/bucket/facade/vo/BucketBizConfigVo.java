package cloud.xcan.angus.core.storage.interfaces.bucket.facade.vo;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class BucketBizConfigVo implements Serializable {

  private String bizKey;

  private String bucketName;

  public String remark;

  private Boolean publicAccess;

  private Boolean encrypt;

  private Boolean multiTenantCtrl;

  private String appCode;

  private String appAdminCode;

  private int cacheAge;

}
