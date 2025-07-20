package cloud.xcan.angus.core.storage.interfaces.space.facade;

import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.object.SpaceDirectoryAddDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.object.SpaceObjectFindDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.object.SpaceObjectMoveDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.object.SpaceObjectAddressVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.object.SpaceObjectDetailVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.object.SpaceObjectNavigationVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.object.SpaceObjectVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;

public interface SpaceObjectFacade {

  IdKey<Long, Object> directoryAdd(SpaceDirectoryAddDto dto);

  void rename(Long id, String name);

  void move(SpaceObjectMoveDto dto);

  void delete(HashSet<Long> ids);

  SpaceObjectNavigationVo navigation(Long id);

  SpaceObjectAddressVo address(Long id);

  SpaceObjectDetailVo detail(Long id);

  PageResult<SpaceObjectVo> list(SpaceObjectFindDto dto);

}
