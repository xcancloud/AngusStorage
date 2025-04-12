package cloud.xcan.angus.core.storage.interfaces.bucket.facade.internal;


import static cloud.xcan.angus.core.storage.interfaces.bucket.facade.internal.assembler.BucketAssembler.addDtoToConfig;

import cloud.xcan.angus.core.storage.application.cmd.bucket.BucketBizConfigCmd;
import cloud.xcan.angus.core.storage.interfaces.bucket.facade.BucketBizConfigFacade;
import cloud.xcan.angus.core.storage.interfaces.bucket.facade.dto.BucketConfigDto;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;


@Component
public class BucketBizConfigFacadeImpl implements BucketBizConfigFacade {

  @Resource
  private BucketBizConfigCmd bucketBizConfigCmd;

  @Override
  public void config(BucketConfigDto dto) {
    bucketBizConfigCmd.config(addDtoToConfig(dto));
  }

  @Override
  public void configDelete(String bizKey) {
    bucketBizConfigCmd.configDelete(bizKey);
  }

}
