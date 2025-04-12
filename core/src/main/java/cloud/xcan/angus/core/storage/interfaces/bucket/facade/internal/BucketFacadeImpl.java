package cloud.xcan.angus.core.storage.interfaces.bucket.facade.internal;


import static cloud.xcan.angus.core.storage.interfaces.bucket.facade.internal.assembler.BucketAssembler.addDtoToBucket;
import static cloud.xcan.angus.core.storage.interfaces.bucket.facade.internal.assembler.BucketAssembler.getSpecification;
import static cloud.xcan.angus.core.storage.interfaces.bucket.facade.internal.assembler.BucketAssembler.toBucketVo;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.storage.application.cmd.bucket.BucketCmd;
import cloud.xcan.angus.core.storage.application.query.bucket.BucketQuery;
import cloud.xcan.angus.core.storage.domain.bucket.Bucket;
import cloud.xcan.angus.core.storage.interfaces.bucket.facade.BucketFacade;
import cloud.xcan.angus.core.storage.interfaces.bucket.facade.dto.BucketAddDto;
import cloud.xcan.angus.core.storage.interfaces.bucket.facade.dto.BucketFindDto;
import cloud.xcan.angus.core.storage.interfaces.bucket.facade.internal.assembler.BucketAssembler;
import cloud.xcan.angus.core.storage.interfaces.bucket.facade.vo.BucketVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;


@Component
public class BucketFacadeImpl implements BucketFacade {

  @Resource
  private BucketQuery bucketQuery;

  @Resource
  private BucketCmd bucketCmd;

  @Override
  public IdKey<Long, Object> add(BucketAddDto dto) {
    return bucketCmd.add(addDtoToBucket(dto));
  }

  @Override
  public void delete(String name) {
    bucketCmd.delete(name);
  }

  @NameJoin
  @Override
  public BucketVo detail(String name) {
    return toBucketVo(bucketQuery.detail(name));
  }

  @NameJoin
  @Override
  public PageResult<BucketVo> find(BucketFindDto dto) {
    Page<Bucket> page = bucketQuery.find(getSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, BucketAssembler::toBucketVo);
  }

}
