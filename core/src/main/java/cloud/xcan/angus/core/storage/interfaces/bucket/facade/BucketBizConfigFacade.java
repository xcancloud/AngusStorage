package cloud.xcan.angus.core.storage.interfaces.bucket.facade;

import cloud.xcan.angus.core.storage.interfaces.bucket.facade.dto.BucketConfigDto;

public interface BucketBizConfigFacade {

  void config(BucketConfigDto dto);

  void configDelete(String bizKey);

}
