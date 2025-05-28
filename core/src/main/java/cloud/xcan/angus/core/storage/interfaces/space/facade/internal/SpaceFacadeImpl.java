package cloud.xcan.angus.core.storage.interfaces.space.facade.internal;

import static cloud.xcan.angus.core.storage.interfaces.space.facade.internal.assembler.SpaceAssembler.addDtoToDomain;
import static cloud.xcan.angus.core.storage.interfaces.space.facade.internal.assembler.SpaceAssembler.getSearchCriteria;
import static cloud.xcan.angus.core.storage.interfaces.space.facade.internal.assembler.SpaceAssembler.getSpecification;
import static cloud.xcan.angus.core.storage.interfaces.space.facade.internal.assembler.SpaceAssembler.toDetailVo;
import static cloud.xcan.angus.core.storage.interfaces.space.facade.internal.assembler.SpaceAssembler.updateDtoToDomain;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.commonlink.space.StorageResourcesCount;
import cloud.xcan.angus.api.commonlink.space.StorageResourcesCreationCount;
import cloud.xcan.angus.api.storage.space.dto.SpaceAssetsCountDto;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.storage.application.cmd.space.SpaceCmd;
import cloud.xcan.angus.core.storage.application.query.space.SpaceQuery;
import cloud.xcan.angus.core.storage.application.query.space.SpaceSearch;
import cloud.xcan.angus.core.storage.domain.space.Space;
import cloud.xcan.angus.core.storage.interfaces.space.facade.SpaceFacade;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.SpaceAddDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.SpaceFindDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.SpaceSearchDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.SpaceUpdateDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.internal.assembler.SpaceAssembler;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.SpaceDetailVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.SpaceVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashSet;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class SpaceFacadeImpl implements SpaceFacade {

  @Resource
  private SpaceCmd spaceCmd;

  @Resource
  private SpaceQuery spaceQuery;

  @Resource
  private SpaceSearch spaceSearch;

  @Override
  public IdKey<Long, Object> add(SpaceAddDto dto) {
    return spaceCmd.add(addDtoToDomain(dto));
  }

  @Override
  public void update(SpaceUpdateDto dto) {
    spaceCmd.update(updateDtoToDomain(dto));
  }

  @Override
  public void delete(HashSet<Long> ids) {
    spaceCmd.delete(ids, true);
  }

  @NameJoin
  @Override
  public SpaceDetailVo detail(Long id) {
    return toDetailVo(spaceQuery.detail(id));
  }

  @NameJoin
  @Override
  public PageResult<SpaceVo> list(SpaceFindDto dto) {
    Page<Space> page = spaceQuery.find(getSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, SpaceAssembler::toVo);
  }

  @NameJoin
  @Override
  public PageResult<SpaceVo> search(SpaceSearchDto dto) {
    Page<Space> page = spaceSearch.search(getSearchCriteria(dto), dto.tranPage(), Space.class);
    return buildVoPageResult(page, SpaceAssembler::toVo);
  }

  @Override
  public StorageResourcesCount resourcesStatistics(SpaceAssetsCountDto dto) {
    return spaceQuery.countStatistics(dto.getProjectId(), dto.getCreatorObjectType(),
        dto.getCreatorObjectId(), dto.getCreatedDateStart(), dto.getCreatedDateEnd());
  }

  @Override
  public StorageResourcesCreationCount resourcesCreationStatistics(SpaceAssetsCountDto dto) {
    return spaceQuery.resourcesCreationStatistics(dto.getProjectId(), dto.getCreatorObjectType(),
        dto.getCreatorObjectId(), dto.getCreatedDateStart(), dto.getCreatedDateEnd());
  }

}
