package cloud.xcan.angus.core.storage.interfaces.space.facade.internal.assembler;

import static cloud.xcan.angus.api.commonlink.StorageConstant.DEFAULT_DATA_FILE_BIZ_KEY;
import static cloud.xcan.angus.core.storage.interfaces.bucket.facade.internal.assembler.BucketAssembler.toConfigVo;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;
import static cloud.xcan.angus.spec.utils.ObjectUtils.stringSafe;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.core.storage.domain.space.Space;
import cloud.xcan.angus.core.storage.domain.space.SpaceSummary;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.SpaceAddDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.SpaceFindDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.SpaceUpdateDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.SpaceDetailVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.SpaceSummaryVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.SpaceVo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.spec.unit.DataSize;
import cloud.xcan.angus.spec.unit.DataUnit;
import java.util.Set;

public class SpaceAssembler {

  public static Space addDtoToDomain(SpaceAddDto dto) {
    return new Space()
        .setProjectId(dto.getProjectId())
        .setName(dto.getName())
        .setBizKey(isEmpty(dto.getBizKey()) ? DEFAULT_DATA_FILE_BIZ_KEY : dto.getBizKey())
        .setQuotaSize(safeSpaceSize(dto.getQuotaSize()))
        .setAuth(nullSafe(dto.getAuth(), false))
        .setRemark(dto.getRemark());
  }

  public static Space updateDtoToDomain(SpaceUpdateDto dto) {
    return new Space()
        .setId(dto.getId())
        .setName(dto.getName())
        .setQuotaSize(safeSpaceSize(dto.getQuotaSize()))
        .setRemark(stringSafe(dto.getRemark()));
  }

  private static String safeSpaceSize(DataSize quotaSize) {
    return nonNull(quotaSize) ? quotaSize.toString() : null;
  }

  public static SpaceDetailVo toDetailVo(Space space) {
    return new SpaceDetailVo().setId(space.getId())
        .setProjectId(space.getProjectId())
        .setName(space.getName())
        .setStoreType(space.getStoreType())
        .setBizKey(space.getBizKey())
        .setQuotaSize(nonNull(space.getQuotaSize()) ? DataSize.parse(space.getQuotaSize()) : null)
        .setAuth(space.getAuth())
        .setRemark(space.getRemark())
        .setBucketName(space.getBucketName())
        .setCreatedBy(space.getCreatedBy())
        .setCreatedDate(space.getCreatedDate())
        .setModifiedBy(space.getModifiedBy())
        .setModifiedDate(space.getModifiedDate())
        .setSummary(toSummaryVo(space.getSummary()))
        .setConfig(toConfigVo(space.getConfig()));
  }

  public static SpaceVo toVo(Space space) {
    return new SpaceVo().setId(space.getId())
        .setProjectId(space.getProjectId())
        .setName(space.getName())
        .setBizKey(space.getBizKey())
        .setQuotaSize(nonNull(space.getQuotaSize()) ? DataSize.parse(space.getQuotaSize()) : null)
        .setSize(DataSize.of(space.getSize(), DataUnit.Bytes))
        .setSubDirectoryNum(space.getSubDirectoryNum())
        .setSubFileNum(space.getSubFileNum())
        .setAuth(space.getAuth())
        .setRemark(space.getRemark())
        .setCreatedBy(space.getCreatedBy())
        .setCreatedDate(space.getCreatedDate());
  }

  public static SpaceSummaryVo toSummaryVo(SpaceSummary summary) {
    return isNull(summary) ? null : new SpaceSummaryVo()
        .setTenantQuotaSize(nonNull(summary.getTenantQuotaSize())
            ? DataSize.of(summary.getTenantQuotaSize(), DataUnit.Bytes) : null)
        .setQuotaSize(nonNull(summary.getQuotaSize())
            ? DataSize.parse(summary.getQuotaSize()) : null)
        .setAvailableSize(DataSize.of(summary.getAvailableSize(), DataUnit.Bytes))
        .setUsedSize(DataSize.of(summary.getUsedSize(), DataUnit.Bytes))
        .setUsage(summary.getUsage())
        .setSubDirectoryNum(summary.getSubDirectoryNum())
        .setSubFileNum(summary.getSubFileNum());
  }

  public static GenericSpecification<Space> getSpecification(SpaceFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate")
        .orderByFields("id", "createdDate", "createdBy")
        .matchSearchFields("name")
        .build();
    return new GenericSpecification<>(filters);
  }

}

