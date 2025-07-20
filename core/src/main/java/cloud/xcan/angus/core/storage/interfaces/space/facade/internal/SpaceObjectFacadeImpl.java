package cloud.xcan.angus.core.storage.interfaces.space.facade.internal;

import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.storage.interfaces.space.facade.internal.assembler.SpaceObjectAssembler.addDtoToDirectoryObject;
import static cloud.xcan.angus.core.storage.interfaces.space.facade.internal.assembler.SpaceObjectAssembler.getSpecification;
import static cloud.xcan.angus.core.storage.interfaces.space.facade.internal.assembler.SpaceObjectAssembler.toAddressVo;
import static cloud.xcan.angus.core.storage.interfaces.space.facade.internal.assembler.SpaceObjectAssembler.toDetailVo;
import static cloud.xcan.angus.core.storage.interfaces.space.facade.internal.assembler.SpaceObjectAssembler.toNavigationVo;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.storage.application.cmd.space.SpaceObjectCmd;
import cloud.xcan.angus.core.storage.application.query.space.SpaceObjectQuery;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObject;
import cloud.xcan.angus.core.storage.interfaces.space.facade.SpaceObjectFacade;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.object.SpaceDirectoryAddDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.object.SpaceObjectFindDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.object.SpaceObjectMoveDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.internal.assembler.SpaceObjectAssembler;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.object.SpaceObjectAddressVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.object.SpaceObjectDetailVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.object.SpaceObjectNavigationVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.object.SpaceObjectVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashSet;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class SpaceObjectFacadeImpl implements SpaceObjectFacade {

  @Resource
  private SpaceObjectCmd spaceObjectCmd;

  @Resource
  private SpaceObjectQuery spaceObjectQuery;

  @Override
  public IdKey<Long, Object> directoryAdd(SpaceDirectoryAddDto dto) {
    return spaceObjectCmd.directoryAdd(addDtoToDirectoryObject(dto));
  }

  @Override
  public void rename(Long id, String name) {
    spaceObjectCmd.rename(id, name);
  }

  @Override
  public void move(SpaceObjectMoveDto dto) {
    spaceObjectCmd.move(dto.getObjectIds(), dto.getTargetSpaceId(), dto.getTargetDirectoryId());
  }

  @Override
  public void delete(HashSet<Long> ids) {
    spaceObjectCmd.delete(ids);
  }

  @NameJoin
  @Override
  public SpaceObjectNavigationVo navigation(Long id) {
    return toNavigationVo(spaceObjectQuery.navigation(id));
  }

  @Override
  public SpaceObjectAddressVo address(Long id) {
    return toAddressVo(spaceObjectQuery.address(id));
  }

  @NameJoin
  @Override
  public SpaceObjectDetailVo detail(Long id) {
    return toDetailVo(spaceObjectQuery.detail(id));
  }

  @NameJoin
  @Override
  public PageResult<SpaceObjectVo> list(SpaceObjectFindDto dto) {
    Page<SpaceObject> page = spaceObjectQuery.list(getSpecification(dto), dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, SpaceObjectAssembler::toVo);
  }

}
