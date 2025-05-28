package cloud.xcan.angus.core.storage.interfaces.space.facade;

import cloud.xcan.angus.core.storage.domain.space.auth.SpacePermission;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.auth.SpaceAuthAddDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.auth.SpaceAuthFindDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.auth.SpaceAuthReplaceDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.auth.SpaceAuthCurrentVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.auth.SpaceAuthVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface SpaceAuthFacade {

  IdKey<Long, Object> add(Long spaceId, SpaceAuthAddDto dto);

  void delete(Long spaceId);

  List<SpacePermission> userAuth(Long spaceId, Long userId, Boolean admin);

  SpaceAuthCurrentVo currentUserAuth(Long spaceId, Boolean admin);

  Map<Long, SpaceAuthCurrentVo> currentUserAuths(HashSet<Long> ids, Boolean admin);

  void replace(Long spaceId, SpaceAuthReplaceDto dto);

  void enabled(Long spaceId, Boolean enabled);

  Boolean status(Long spaceId);

  void authCheck(Long spaceId, SpacePermission authPermission, Long userId);

  PageResult<SpaceAuthVo> list(SpaceAuthFindDto dto);

}
