package cloud.xcan.angus.core.storage.interfaces.space.facade.internal;

import static cloud.xcan.angus.core.storage.interfaces.space.facade.internal.assembler.SpaceAuthAssembler.addDtoToDomain;
import static cloud.xcan.angus.core.storage.interfaces.space.facade.internal.assembler.SpaceAuthAssembler.deleteDtoToDomain;
import static cloud.xcan.angus.core.storage.interfaces.space.facade.internal.assembler.SpaceAuthAssembler.getSpecification;
import static cloud.xcan.angus.core.storage.interfaces.space.facade.internal.assembler.SpaceAuthAssembler.replaceDtoToDomain;
import static cloud.xcan.angus.core.storage.interfaces.space.facade.internal.assembler.SpaceAuthAssembler.toAuthCurrentVo;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.storage.application.cmd.space.SpaceAuthCmd;
import cloud.xcan.angus.core.storage.application.query.space.SpaceAuthQuery;
import cloud.xcan.angus.core.storage.domain.space.auth.SpaceAuth;
import cloud.xcan.angus.core.storage.domain.space.auth.SpaceAuthCurrent;
import cloud.xcan.angus.core.storage.domain.space.auth.SpacePermission;
import cloud.xcan.angus.core.storage.interfaces.space.facade.SpaceAuthFacade;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.auth.SpaceAuthAddDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.auth.SpaceAuthFindDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.auth.SpaceAuthReplaceDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.internal.assembler.SpaceAuthAssembler;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.auth.SpaceAuthCurrentVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.auth.SpaceAuthVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class SpaceAuthFacadeImpl implements SpaceAuthFacade {

  @Resource
  private SpaceAuthCmd spaceAuthCmd;

  @Resource
  private SpaceAuthQuery spaceAuthQuery;

  @Override
  public IdKey<Long, Object> add(Long spaceId, SpaceAuthAddDto dto) {
    return spaceAuthCmd.add(addDtoToDomain(spaceId, dto));
  }

  @Override
  public void replace(Long spaceId, SpaceAuthReplaceDto dto) {
    spaceAuthCmd.replace(replaceDtoToDomain(spaceId, dto));
  }

  @Override
  public void delete(Long spaceId) {
    spaceAuthCmd.delete(deleteDtoToDomain(spaceId));
  }

  @Override
  public void enabled(Long spaceId, Boolean enabled) {
    spaceAuthCmd.enabled(spaceId, enabled);
  }

  @Override
  public Boolean status(Long spaceId) {
    return spaceAuthQuery.status(spaceId);
  }

  @Override
  public List<SpacePermission> userAuth(Long spaceId, Long userId, Boolean admin) {
    return spaceAuthQuery.userAuth(spaceId, userId, admin);
  }

  @Override
  public SpaceAuthCurrentVo currentUserAuth(Long spaceId, Boolean admin) {
    SpaceAuthCurrent authCurrent = spaceAuthQuery.currentUserAuth(spaceId, admin);
    return toAuthCurrentVo(authCurrent);
  }

  @Override
  public Map<Long, SpaceAuthCurrentVo> currentUserAuths(HashSet<Long> spaceIds, Boolean admin) {
    Map<Long, SpaceAuthCurrent> authCurrentsMap = spaceAuthQuery.currentUserAuths(spaceIds, admin);
    return isEmpty(authCurrentsMap) ? null : authCurrentsMap.entrySet()
        .stream().collect(Collectors.toMap(Entry::getKey, x -> toAuthCurrentVo(x.getValue())));
  }

  @Override
  public void authCheck(Long spaceId, SpacePermission authPermission, Long userId) {
    spaceAuthQuery.check(spaceId, authPermission, userId);
  }

  @Override
  @NameJoin
  public PageResult<SpaceAuthVo> list(SpaceAuthFindDto dto) {
    List<String> spaceIds = dto.getFilterInValue("spaceId");
    if (dto.getSpaceId() != null) {
      spaceIds.add(String.valueOf(dto.getSpaceId()));
    }
    Page<SpaceAuth> page = spaceAuthQuery.find(getSpecification(dto), spaceIds, dto.tranPage());
    return buildVoPageResult(page, SpaceAuthAssembler::toDetailVo);
  }

}




