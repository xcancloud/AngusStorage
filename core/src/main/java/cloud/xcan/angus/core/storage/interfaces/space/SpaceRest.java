package cloud.xcan.angus.core.storage.interfaces.space;


import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.api.commonlink.space.StorageResourcesCount;
import cloud.xcan.angus.api.commonlink.space.StorageResourcesCreationCount;
import cloud.xcan.angus.api.storage.space.dto.SpaceAssetsCountDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.SpaceFacade;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.SpaceAddDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.SpaceFindDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.SpaceSearchDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.SpaceUpdateDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.SpaceDetailVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.SpaceVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * A bizKey can correspond to multiple spaces, and a space can only belong to one bizKey.
 *
 * @author XiaoLong Liu
 */
@Tag(name = "Space", description = "Granular space quota allocation and access policy enforcement.")
@Validated
@RestController
@RequestMapping("/api/v1/space")
public class SpaceRest {

  @Resource
  private SpaceFacade spaceFacade;

   @Operation(summary = "Add space.", operationId = "space:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<IdKey<Long, Object>> add(@Valid @RequestBody SpaceAddDto dto) {
    return ApiLocaleResult.success(spaceFacade.add(dto));
  }

   @Operation(summary = "Update space.", operationId = "space:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated successfully"),
      @ApiResponse(responseCode = "404", description = "Space does not exist")})
  @PatchMapping
  public ApiLocaleResult<?> update(@Valid @RequestBody SpaceUpdateDto dto) {
    spaceFacade.update(dto);
    return ApiLocaleResult.success();
  }

   @Operation(summary = "Delete space.", operationId = "space:delete")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @DeleteMapping
  public void delete(
      @Valid @Size(max = MAX_BATCH_SIZE) @Parameter(name = "ids", description = "Space ids", required = true)
      @RequestParam("ids")  HashSet<Long> ids) {
    spaceFacade.delete(ids);
  }

   @Operation(summary = "Query the detail of space.", operationId = "space:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Space does not exist")})
  @GetMapping(value = "/{id}")
  public ApiLocaleResult<SpaceDetailVo> detail(
      @Parameter(name = "id", description = "Space id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(spaceFacade.detail(id));
  }

   @Operation(summary = "Query the list of space.", operationId = "space:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<SpaceVo>> list(@Valid SpaceFindDto dto) {
    return ApiLocaleResult.success(spaceFacade.list(dto));
  }

   @Operation(summary = "Fulltext search the space.", operationId = "space:search")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/search")
  public ApiLocaleResult<PageResult<SpaceVo>> search(@Valid SpaceSearchDto dto) {
    return ApiLocaleResult.success(spaceFacade.search(dto));
  }

   @Operation(summary = "Space data resources statistics.", operationId = "space:resources:count")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/resources/count")
  public ApiLocaleResult<StorageResourcesCount> resourcesStatistics(
      @Valid SpaceAssetsCountDto dto) {
    return ApiLocaleResult.success(spaceFacade.resourcesStatistics(dto));
  }

   @Operation(summary = "Space data resources creation statistics.", operationId = "space:resources:creation:count")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/resources/creation/count")
  public ApiLocaleResult<StorageResourcesCreationCount> resourcesCreationStatistics(
      @Valid SpaceAssetsCountDto dto) {
    return ApiLocaleResult.success(spaceFacade.resourcesCreationStatistics(dto));
  }

}
