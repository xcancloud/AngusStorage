package cloud.xcan.angus.core.storage.interfaces.space.facade;

import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share.SpaceShareAddDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share.SpaceShareDetailDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share.SpaceShareFindDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share.SpaceShareObjectFindDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share.SpaceShareQuickAddDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share.SpaceShareUpdateDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.share.SpaceShareAddVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.share.SpaceShareDetailVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.share.SpaceShareObjectDetailVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.share.SpaceShareObjectVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.share.SpaceShareVo;
import cloud.xcan.angus.remote.PageResult;
import java.util.HashSet;

public interface SpaceShareFacade {

  SpaceShareAddVo add(SpaceShareAddDto dto);

  String quickAdd(SpaceShareQuickAddDto dto);

  void update(SpaceShareUpdateDto dto);

  void delete(HashSet<Long> ids);

  SpaceShareVo detail(Long id);

  PageResult<SpaceShareVo> list(SpaceShareFindDto dto);

  SpaceShareDetailVo shareDetailPub(SpaceShareDetailDto dto);

  SpaceShareObjectDetailVo objectDetailPub(Long id, SpaceShareDetailDto dto);

  PageResult<SpaceShareObjectVo> objectListPub(SpaceShareObjectFindDto dto);

}
