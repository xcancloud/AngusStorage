package cloud.xcan.angus.core.storage.interfaces.bucket.facade;

import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.core.storage.interfaces.bucket.facade.dto.BucketAddDto;
import cloud.xcan.angus.core.storage.interfaces.bucket.facade.dto.BucketFindDto;
import cloud.xcan.angus.core.storage.interfaces.bucket.facade.vo.BucketVo;
import cloud.xcan.angus.spec.experimental.IdKey;


public interface BucketFacade {

  IdKey<Long, Object> add(BucketAddDto dto);

  void delete(String id);

  BucketVo detail(String id);

  PageResult<BucketVo> find(BucketFindDto dto);

}
