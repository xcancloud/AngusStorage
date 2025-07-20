package cloud.xcan.angus.core.storage.interfaces.space.facade.internal;

import static cloud.xcan.angus.core.storage.interfaces.space.facade.internal.assembler.SpaceShareAssembler.addDtoToDomain;
import static cloud.xcan.angus.core.storage.interfaces.space.facade.internal.assembler.SpaceShareAssembler.getShareObjectSearchCriteria;
import static cloud.xcan.angus.core.storage.interfaces.space.facade.internal.assembler.SpaceShareAssembler.getSpecification;
import static cloud.xcan.angus.core.storage.interfaces.space.facade.internal.assembler.SpaceShareAssembler.toShareAddVo;
import static cloud.xcan.angus.core.storage.interfaces.space.facade.internal.assembler.SpaceShareAssembler.toShareDetailVo;
import static cloud.xcan.angus.core.storage.interfaces.space.facade.internal.assembler.SpaceShareAssembler.toShareObjectDetailVo;
import static cloud.xcan.angus.core.storage.interfaces.space.facade.internal.assembler.SpaceShareAssembler.toVo;
import static cloud.xcan.angus.core.storage.interfaces.space.facade.internal.assembler.SpaceShareAssembler.updateDtoToDomain;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.storage.application.cmd.space.SpaceShareCmd;
import cloud.xcan.angus.core.storage.application.query.space.SpaceShareQuery;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObject;
import cloud.xcan.angus.core.storage.domain.space.share.SpaceShare;
import cloud.xcan.angus.core.storage.interfaces.space.facade.SpaceShareFacade;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share.SpaceShareAddDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share.SpaceShareDetailDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share.SpaceShareFindDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share.SpaceShareObjectFindDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share.SpaceShareQuickAddDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share.SpaceShareUpdateDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.internal.assembler.SpaceShareAssembler;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.share.SpaceShareAddVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.share.SpaceShareDetailVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.share.SpaceShareObjectDetailVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.share.SpaceShareObjectVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.share.SpaceShareVo;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import java.util.HashSet;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class SpaceShareFacadeImpl implements SpaceShareFacade {

  @Resource
  private SpaceShareQuery spaceShareQuery;

  @Resource
  private SpaceShareCmd spaceShareCmd;

  @Override
  public SpaceShareAddVo add(SpaceShareAddDto dto) {
    return toShareAddVo(spaceShareCmd.add(addDtoToDomain(dto)));
  }

  @Override
  public String quickAdd(SpaceShareQuickAddDto dto) {
    return spaceShareCmd.quickAdd(addDtoToDomain(dto)).getUrl();
  }

  @Override
  public void update(SpaceShareUpdateDto dto) {
    spaceShareCmd.update(updateDtoToDomain(dto));
  }

  @Override
  public void delete(HashSet<Long> ids) {
    spaceShareCmd.delete(ids);
  }

  @NameJoin
  @Override
  public SpaceShareVo detail(Long id) {
    return toVo(spaceShareQuery.detail(id));
  }

  @NameJoin
  @Override
  public PageResult<SpaceShareVo> list(SpaceShareFindDto dto) {
    Page<SpaceShare> page = spaceShareQuery.list(getSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, SpaceShareAssembler::toVo);
  }

  @NameJoin
  @Override
  public SpaceShareDetailVo shareDetailPub(SpaceShareDetailDto dto) {
    return toShareDetailVo(spaceShareQuery.shareDetailPub(
        dto.getSid(), dto.getSpt(), dto.getPassword()));
  }

  @NameJoin
  @Override
  public SpaceShareObjectDetailVo objectDetailPub(Long id, SpaceShareDetailDto dto) {
    return toShareObjectDetailVo(spaceShareQuery.objectDetailPub(id, dto.getSid(),
        dto.getSpt(), dto.getPassword()));
  }

  @NameJoin
  @Override
  public PageResult<SpaceShareObjectVo> objectListPub(SpaceShareObjectFindDto dto) {
    Page<SpaceObject> page = spaceShareQuery.objectListPub(
        getShareObjectSearchCriteria(dto), dto.tranPage());
    return buildVoPageResult(page, SpaceShareAssembler::toShareObjectVo);
  }

}
