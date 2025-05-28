package cloud.xcan.angus.core.storage.interfaces.space.facade.internal.assembler;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.core.storage.domain.space.auth.SpaceAuth;
import cloud.xcan.angus.core.storage.domain.space.auth.SpaceAuthCurrent;
import cloud.xcan.angus.core.storage.domain.space.auth.SpacePermission;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.auth.SpaceAuthAddDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.auth.SpaceAuthFindDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.auth.SpaceAuthReplaceDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.auth.SpaceAuthCurrentVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.auth.SpaceAuthDeptDetailVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.auth.SpaceAuthGroupDetailVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.auth.SpaceAuthUserDetailVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.auth.SpaceAuthVo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.jpa.domain.Specification;

public class SpaceAuthAssembler {

  public static SpaceAuth addDtoToDomain(Long id, SpaceAuthAddDto dto) {
    Set<SpacePermission> permissions = new HashSet<>();
    permissions.add(SpacePermission.VIEW);
    if (isNotEmpty(dto.getPermissions())) {
      permissions.addAll(dto.getPermissions());
    }
    return new SpaceAuth()
        .setSpaceId(id)
        .setCreator(false)
        .setAuthObjectType(dto.getAuthObjectType())
        .setAuthObjectId(dto.getAuthObjectId())
        .setAuths(new ArrayList<>(permissions));
  }

  public static SpaceAuth replaceDtoToDomain(Long id, SpaceAuthReplaceDto dto) {
    dto.getPermissions().add(SpacePermission.VIEW);
    return new SpaceAuth().setId(id)
        .setAuths(new ArrayList<>(dto.getPermissions()));
  }

  public static SpaceAuth deleteDtoToDomain(Long id) {
    return new SpaceAuth().setId(id);
  }

  public static SpaceAuthVo toDetailVo(SpaceAuth spaceAuth) {
    SpaceAuthVo authVo = switch (spaceAuth.getAuthObjectType()) {
      case USER -> new SpaceAuthUserDetailVo();
      case GROUP -> new SpaceAuthGroupDetailVo();
      case DEPT -> new SpaceAuthDeptDetailVo();
    };
    authVo.setId(spaceAuth.getId());
    authVo.setPermissions(spaceAuth.getAuths());
    authVo.setAuthObjectType(spaceAuth.getAuthObjectType());
    authVo.setAuthObjectId(spaceAuth.getAuthObjectId());
    authVo.setCreator(spaceAuth.getCreator());
    authVo.setSpaceId(spaceAuth.getSpaceId());
    return authVo;
  }

  public static SpaceAuthCurrentVo toAuthCurrentVo(SpaceAuthCurrent authCurrent) {
    return new SpaceAuthCurrentVo()
        .setSpaceAuth(authCurrent.isSpaceAuth())
        .setPermissions(authCurrent.getPermissions());
  }

  public static Specification<SpaceAuth> getSpecification(SpaceAuthFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate")
        .orderByFields("id", "spaceId", "createdDate")
        .inAndNotFields("id", "spaceId")
        .build();
    return new GenericSpecification<>(filters);
  }

}
