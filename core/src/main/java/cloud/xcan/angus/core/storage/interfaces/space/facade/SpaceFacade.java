package cloud.xcan.angus.core.storage.interfaces.space.facade;

import cloud.xcan.angus.api.commonlink.space.StorageResourcesCount;
import cloud.xcan.angus.api.commonlink.space.StorageResourcesCreationCount;
import cloud.xcan.angus.api.storage.space.dto.SpaceAssetsCountDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.SpaceAddDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.SpaceFindDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.SpaceSearchDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.SpaceUpdateDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.SpaceDetailVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.SpaceVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;

public interface SpaceFacade {

  IdKey<Long, Object> add(SpaceAddDto dto);

  void update(SpaceUpdateDto dto);

  void delete(HashSet<Long> ids);

  SpaceDetailVo detail(Long id);

  PageResult<SpaceVo> list(SpaceFindDto dto);

  PageResult<SpaceVo> search(SpaceSearchDto dto);

  StorageResourcesCount resourcesStatistics(SpaceAssetsCountDto dto);

  StorageResourcesCreationCount resourcesCreationStatistics(SpaceAssetsCountDto dto);
}
