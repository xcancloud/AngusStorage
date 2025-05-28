package cloud.xcan.angus.core.storage.interfaces.bucket.facade.internal.assembler;


import static cloud.xcan.angus.api.commonlink.StorageConstant.DEFAULT_APP_CODE;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.core.storage.domain.bucket.Bucket;
import cloud.xcan.angus.core.storage.domain.bucket.config.BucketBizConfig;
import cloud.xcan.angus.core.storage.interfaces.bucket.facade.dto.BucketAddDto;
import cloud.xcan.angus.core.storage.interfaces.bucket.facade.dto.BucketConfigDto;
import cloud.xcan.angus.core.storage.interfaces.bucket.facade.dto.BucketFindDto;
import cloud.xcan.angus.core.storage.interfaces.bucket.facade.vo.BucketBizConfigVo;
import cloud.xcan.angus.core.storage.interfaces.bucket.facade.vo.BucketVo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.jpa.domain.Specification;

public class BucketAssembler {

  public static Bucket addDtoToBucket(BucketAddDto dto) {
    return new Bucket()
        .setName(dto.getName())
        .setAcl(dto.getAcl())
        .setTenantCreated(true);
  }

  public static BucketVo toBucketVo(Bucket bucket) {
    BucketVo vo = new BucketVo()
        .setId(bucket.getId())
        .setAcl(bucket.getAcl())
        .setName(bucket.getName())
        .setTenantCreated(bucket.getTenantCreated());
    if (isNotEmpty(bucket.getConfigs())) {
      vo.setConfigs(bucket.getConfigs()
          .stream().map(BucketAssembler::toConfigVo)
          .collect(Collectors.toList()));
    }
    return vo;
  }

  public static BucketBizConfigVo toConfigVo(BucketBizConfig config) {
    return new BucketBizConfigVo()
        .setBizKey(config.getBizKey())
        .setBucketName(config.getBucketName())
        .setRemark(config.getRemark())
        .setPublicAccess(config.getPublicAccess())
        .setEncrypt(config.getEncrypt())
        .setMultiTenantCtrl(config.getMultiTenantCtrl())
        .setAppCode(config.getAppCode())
        .setAppAdminCode(config.getAppAdminCode())
        .setCacheAge(config.getCacheAge());
  }

  public static BucketBizConfig addDtoToConfig(BucketConfigDto dto) {
    return new BucketBizConfig()
        .setBucketName(dto.getBucketName())
        .setEnabledAuth(false)
        .setAppCode(isEmpty(dto.getAppCode()) ? DEFAULT_APP_CODE : dto.getAppCode())
        .setAppAdminCode(isEmpty(dto.getAppAdminCode()) ? DEFAULT_APP_CODE : dto.getAppAdminCode())
        .setBizKey(dto.getBizKey())
        .setRemark(dto.getRemark())
        .setCacheAge(dto.getCacheAge())
        .setAllowTenantCreated(dto.getAllowTenantCreated());
  }

  public static Specification<Bucket> getSpecification(BucketFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .matchSearchFields("name")
        .orderByFields("id", "createdDate")
        .build();
    return new GenericSpecification<>(filters);
  }

}
