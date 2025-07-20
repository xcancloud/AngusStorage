package cloud.xcan.angus.core.storage.interfaces.space.facade.internal.assembler;

import static cloud.xcan.angus.core.storage.interfaces.file.facade.internal.assembler.FileAssembler.toUploadVo;
import static cloud.xcan.angus.core.storage.interfaces.space.facade.internal.assembler.SpaceObjectAssembler.toSummaryVo;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObject;
import cloud.xcan.angus.core.storage.domain.space.share.SpaceShare;
import cloud.xcan.angus.core.storage.domain.space.share.SpaceShareType;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share.SpaceShareAddDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share.SpaceShareFindDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share.SpaceShareObjectFindDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share.SpaceShareQuickAddDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share.SpaceShareUpdateDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.share.SpaceShareAddVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.share.SpaceShareDetailVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.share.SpaceShareObjectDetailVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.share.SpaceShareObjectVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.share.SpaceShareVo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.spec.unit.TimeValue;
import java.time.LocalDateTime;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

public class SpaceShareAssembler {

  public static SpaceShare addDtoToDomain(SpaceShareAddDto dto) {
    return new SpaceShare()
        .setSpaceId(dto.getSpaceId())
        .setShareType(SpaceShareType.SPACE_OBJECTS)
        .setObjectIds(dto.getObjectIds())
        .setAll(isEmpty(dto.getObjectIds()))
        .setUrl(dto.getUrl())
        .setExpired(dto.getExpired())
        .setExpiredDuration(safeExpiredDuration(dto.getExpired(), dto.getExpiredDuration()))
        .setExpiredDate(safeExpiredDate(dto.getExpired(), dto.getExpiredDuration()))
        .setPublic0(dto.getPublic0())
        .setPassword(safePassword(dto.getPublic0(), dto.getPassword()))
        .setRemark(dto.getRemark());
  }

  public static SpaceShare addDtoToDomain(SpaceShareQuickAddDto dto) {
    return new SpaceShare()
        .setShareType(SpaceShareType.QUICK_OBJECT)
        .setQuickObjectId(dto.getObjectId())
        .setAll(false)
        //.setUrl(dto.getUrl())
        .setExpired(false)
        .setExpiredDuration(null)
        .setExpiredDate(null)
        .setPublic0(true)
        .setPassword(null)
        .setRemark(null);
  }

  public static SpaceShareAddVo toShareAddVo(SpaceShare share) {
    return new SpaceShareAddVo()
        .setId(share.getId())
        .setUrl(share.getUrl())
        .setPassword(share.getPassword());
  }

  public static SpaceShare updateDtoToDomain(SpaceShareUpdateDto dto) {
    return new SpaceShare()
        .setId(dto.getId())
        .setObjectIds(dto.getObjectIds())
        .setAll(isEmpty(dto.getObjectIds()))
        .setExpired(dto.getExpired())
        .setExpiredDuration(safeExpiredDuration(dto.getExpired(), dto.getExpiredDuration()))
        .setExpiredDate(safeExpiredDate(dto.getExpired(), dto.getExpiredDuration()))
        //.setPublic0(dto.getPublic0())
        .setPassword(dto.getPassword())
        .setRemark(dto.getRemark());
  }

  public static SpaceShareVo toVo(SpaceShare share) {
    return new SpaceShareVo()
        .setId(share.getId())
        .setObjectIds(share.getObjectIds())
        .setAll(share.getAll())
        .setUrl(share.getUrl())
        .setExpired(share.getExpired())
        .setExpiredDuration(isNotEmpty(share.getExpiredDuration())
            ? TimeValue.parse(share.getExpiredDuration()) : null)
        .setExpiredDate(share.getExpiredDate())
        .setPublic0(share.getPublic0())
        .setPassword(share.getPassword())
        .setRemark(share.getRemark())
        .setCreatedBy(share.getCreatedBy())
        .setCreatedDate(share.getCreatedDate());
  }


  public static SpaceShareDetailVo toShareDetailVo(SpaceShare share) {
    return new SpaceShareDetailVo()
        .setId(share.getId())
        .setSpaceId(share.getSpaceId())
        .setObjectIds(share.getObjectIds())
        .setAll(share.getAll())
        .setExpired(share.getExpired())
        .setExpiredDuration(isNotEmpty(share.getExpiredDuration())
            ? TimeValue.parse(share.getExpiredDuration()) : null)
        .setExpiredDate(share.getExpiredDate())
        .setPublic0(share.getPublic0())
        .setCreatedBy(share.getCreatedBy())
        .setCreatedByName(share.getCreatedByName())
        .setAvatar(share.getAvatar())
        .setCreatedDate(share.getCreatedDate());
  }

  public static SpaceShareObjectDetailVo toShareObjectDetailVo(SpaceObject object) {
    return new SpaceShareObjectDetailVo()
        .setId(object.getId())
        .setType(object.getType())
        .setStoreType(object.getStoreType())
        .setName(object.getName())
        .setLevel(object.getLevel())
        .setSpaceId(object.getSpaceId())
        .setParentDirectoryId(object.getParentDirectoryId())
        .setCreatedBy(object.getCreatedBy())
        .setCreatedDate(object.getCreatedDate())
        .setSummary(toSummaryVo(object.getSummary()))
        .setFile(toUploadVo(object.getFile()));
  }

  public static SpaceShareObjectVo toShareObjectVo(SpaceObject object) {
    return new SpaceShareObjectVo()
        .setId(object.getId())
        .setType(object.getType())
        .setStoreType(object.getStoreType())
        .setName(object.getName())
        .setLevel(object.getLevel())
        .setSpaceId(object.getSpaceId())
        .setParentDirectoryId(object.getParentDirectoryId())
        .setCreatedBy(object.getCreatedBy())
        .setCreatedDate(object.getCreatedDate())
        .setSummary(toSummaryVo(object.getSummary()));
  }

  @Nullable
  private static String safeExpiredDuration(Boolean expired, TimeValue duration) {
    if (isNull(expired)) {
      return null;
    }
    return expired ? (nonNull(duration) ? duration.toString() : null) : null;
  }

  @Nullable
  private static LocalDateTime safeExpiredDate(Boolean expired, TimeValue expiredDuration) {
    if (isNull(expired) || !expired || isNull(expiredDuration)) {
      return null;
    }
    return LocalDateTime.now().plusSeconds((long) expiredDuration.toSecond());
  }

  @Nullable
  private static String safePassword(Boolean public0, String password) {
    if (isNull(public0)) {
      return null;
    }
    return !public0 ? password : null;
  }

  public static GenericSpecification<SpaceShare> getSpecification(SpaceShareFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate")
        .orderByFields("id", "createdBy", "createdDate")
        .matchSearchFields("remark")
        .build();
    return new GenericSpecification<>(filters);
  }

  public static Set<SearchCriteria> getShareObjectSearchCriteria(SpaceShareObjectFindDto dto) {
    // Build the final filters
    return new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate")
        .orderByFields("id", "type", "createdBy", "createdDate", "lastModifiedBy",
            "lastModifiedDate")
        .matchSearchFields("name")
        .build();
  }


}
