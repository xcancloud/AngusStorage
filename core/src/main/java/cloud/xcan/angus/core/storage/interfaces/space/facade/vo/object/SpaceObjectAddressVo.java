package cloud.xcan.angus.core.storage.interfaces.space.facade.vo.object;

import cloud.xcan.angus.api.enums.PlatformStoreType;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SpaceObjectAddressVo implements Serializable {

  private String url;

  private String storeAddress;

  private PlatformStoreType storeType;

}
