package cloud.xcan.angus.core.storage.interfaces.space.facade;

import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share.SpaceShareAddDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share.SpaceShareDetailDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share.SpaceShareFindDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share.SpaceShareObjectSearchDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share.SpaceShareQuickAddDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share.SpaceShareSearchDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share.SpaceShareUpdateDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.share.SpaceShareAddVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.share.SpaceShareDetailVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.share.SpaceShareObjectDetailVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.share.SpaceShareObjectVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.share.SpaceShareVo;
import java.util.HashSet;

public interface SpaceShareFacade {

  SpaceShareAddVo add(SpaceShareAddDto dto);

  String quickAdd(SpaceShareQuickAddDto dto);

  void update(SpaceShareUpdateDto dto);

  void delete(HashSet<Long> ids);

  SpaceShareVo detail(Long id);

  PageResult<SpaceShareVo> list(SpaceShareFindDto dto);

  PageResult<SpaceShareVo> search(SpaceShareSearchDto dto);

  SpaceShareDetailVo detailPub(SpaceShareDetailDto dto);

  SpaceShareObjectDetailVo objectDetailPub(Long id, SpaceShareDetailDto dto);

  PageResult<SpaceShareObjectVo> objectSearchPub(SpaceShareObjectSearchDto dto);

}
