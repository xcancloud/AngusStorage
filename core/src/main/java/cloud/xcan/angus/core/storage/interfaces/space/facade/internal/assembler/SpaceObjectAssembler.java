package cloud.xcan.angus.core.storage.interfaces.space.facade.internal.assembler;

import static cloud.xcan.angus.core.storage.interfaces.file.facade.internal.assembler.FileAssembler.toUploadVo;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static cloud.xcan.angus.spec.utils.ObjectUtils.pidSafe;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.enums.FileType;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObject;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObjectSummary;
import cloud.xcan.angus.core.storage.infra.store.impl.ObjectClientFactory;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.object.SpaceDirectoryAddDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.object.SpaceObjectFindDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.to.SpaceObjectNavigationTo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.object.SpaceObjectAddressVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.object.SpaceObjectDetailVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.object.SpaceObjectNavigationVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.object.SpaceObjectSummaryVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.object.SpaceObjectVo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.spec.unit.DataSize;
import cloud.xcan.angus.spec.unit.DataUnit;
import java.util.Set;
import java.util.stream.Collectors;

public class SpaceObjectAssembler {

  public static SpaceObject addDtoToDirectoryObject(SpaceDirectoryAddDto dto) {
    return new SpaceObject()
        .setSpaceId(dto.getSpaceId())
        .setParentDirectoryId(pidSafe(dto.getParentDirectoryId()))
        .setName(dto.getName())
        .setType(FileType.DIRECTORY)
        .setStoreType(ObjectClientFactory.current().getStoreType())
        .setSize(0L);
  }

  public static SpaceObjectDetailVo toDetailVo(SpaceObject object) {
    return new SpaceObjectDetailVo()
        .setId(object.getId())
        .setName(object.getName())
        .setType(object.getType())
        .setStoreType(object.getStoreType())
        .setLevel(object.getLevel())
        .setSpaceId(object.getSpaceId())
        .setParentDirectoryId(pidSafe(object.getParentDirectoryId(), null))
        .setCreatedBy(object.getCreatedBy())
        .setCreatedDate(object.getCreatedDate())
        .setLastModifiedBy(object.getLastModifiedBy())
        .setLastModifiedDate(object.getLastModifiedDate())
        .setSummary(toSummaryVo(object.getSummary()))
        .setFile(toUploadVo(object.getFile()));
  }

  public static SpaceObjectVo toVo(SpaceObject object) {
    return new SpaceObjectVo().setId(object.getId())
        .setType(object.getType())
        .setName(object.getName())
        .setLevel(object.getLevel())
        .setParentDirectoryId(pidSafe(object.getParentDirectoryId(), null))
        .setParentDirectoryName(object.getParentName())
        .setCreatedBy(object.getCreatedBy())
        .setCreatedDate(object.getCreatedDate())
        .setLastModifiedBy(object.getLastModifiedBy())
        .setLastModifiedDate(object.getLastModifiedDate())
        .setSummary(toSummaryVo(object.getSummary()));
  }

  public static SpaceObjectSummaryVo toSummaryVo(SpaceObjectSummary summary) {
    return isNull(summary) ? null : new SpaceObjectSummaryVo()
        .setUsedSize(DataSize.of(summary.getUsedSize(), DataUnit.Bytes))
        .setSubDirectoryNum(summary.getSubDirectoryNum())
        .setSubFileNum(summary.getSubFileNum());
  }

  public static SpaceObjectNavigationVo toNavigationVo(SpaceObject navigation) {
    return new SpaceObjectNavigationVo()
        .setSpaceId(navigation.getSpaceId())
        .setCurrent(toNavigationTo(navigation))
        .setParentChain(isEmpty(navigation.getParentChain()) ? null :
            navigation.getParentChain().stream().map(SpaceObjectAssembler::toNavigationTo)
                .collect(Collectors.toList()));
  }

  public static SpaceObjectNavigationTo toNavigationTo(SpaceObject navigation) {
    return new SpaceObjectNavigationTo()
        .setId(navigation.getId())
        .setName(navigation.getName())
        .setType(navigation.getType())
        .setLevel(navigation.getLevel());
  }

  public static SpaceObjectAddressVo toAddressVo(SpaceObject address) {
    SpaceObjectAddressVo addressVo = new SpaceObjectAddressVo();
    addressVo.setStoreType(address.getStoreType());
    if (nonNull(address.getFile())) {
      addressVo.setUrl(address.getFile().getDownloadUrl());
      addressVo.setStoreAddress(address.getFile().getStoreAddress());
    }
    return addressVo;
  }

  public static GenericSpecification<SpaceObject> getSpecification(SpaceObjectFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate")
        .orderByFields("id", "type", "createdBy", "createdDate", "lastModifiedBy",
            "lastModifiedDate")
        .matchSearchFields("name")
        .build();
    return new GenericSpecification<>(filters);
  }

}

