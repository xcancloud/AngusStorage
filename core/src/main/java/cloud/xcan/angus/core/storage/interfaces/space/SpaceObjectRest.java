package cloud.xcan.angus.core.storage.interfaces.space;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.core.storage.interfaces.space.facade.SpaceObjectFacade;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.object.SpaceDirectoryAddDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.object.SpaceObjectFindDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.object.SpaceObjectMoveDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.object.SpaceObjectSearchDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.object.SpaceObjectAddressVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.object.SpaceObjectDetailVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.object.SpaceObjectNavigationVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.object.SpaceObjectVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "SpaceObject", description = "File/directory CRUD operations and metadata management within spaces.")
@Validated
@RestController
@RequestMapping("/api/v1/space/object")
public class SpaceObjectRest {

  @Resource
  private SpaceObjectFacade spaceObjectFacade;

  @Operation(summary = "Add directory.", operationId = "space:object:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/directory")
  public ApiLocaleResult<IdKey<Long, Object>> directoryAdd(
      @Valid @RequestBody SpaceDirectoryAddDto dto) {
    return ApiLocaleResult.success(spaceObjectFacade.directoryAdd(dto));
  }

  @Operation(summary = "Update directory or file name.", operationId = "space:object:name:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated successfully"),
      @ApiResponse(responseCode = "404", description = "Directory not found")})
  @PutMapping("/{id}/rename")
  public ApiLocaleResult<?> rename(
      @Parameter(name = "id", description = "Directory or file id", required = true) @PathVariable("id") Long id,
      @Parameter(name = "name", description = "Directory or file name", required = true) @RequestParam("name") String name) {
    spaceObjectFacade.rename(id, name);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Move directories or files.", operationId = "space:object:move")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Moved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  @PatchMapping("/move")
  public ApiLocaleResult<?> move(@Valid @RequestBody SpaceObjectMoveDto dto) {
    spaceObjectFacade.move(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Delete directories or files.", operationId = "space:object:delete")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @DeleteMapping
  public void delete(
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestParam("ids")
      @Parameter(name = "ids", description = "Space directory or file ids, max "
          + MAX_BATCH_SIZE, required = true) HashSet<Long> ids) {
    spaceObjectFacade.delete(ids);
  }

  @Operation(summary = "Query the navigation position of directory or file.", operationId = "space:object:navigation:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Directory does not exist")})
  @GetMapping(value = "/{id}/navigation")
  public ApiLocaleResult<SpaceObjectNavigationVo> navigation(
      @Parameter(name = "id", description = "Directory or file id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(spaceObjectFacade.navigation(id));
  }

  @Operation(summary = "Query the download address of file object.", operationId = "space:object:address:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Directory does not exist")})
  @GetMapping(value = "/{id}/address")
  public ApiLocaleResult<SpaceObjectAddressVo> address(
      @Parameter(name = "id", description = "File id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(spaceObjectFacade.address(id));
  }

  @Operation(summary = "Query the detail of directory or file", operationId = "space:object:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Directory does not exist")})
  @GetMapping(value = "/{id}")
  public ApiLocaleResult<SpaceObjectDetailVo> detail(
      @Parameter(name = "id", description = "Directory or file id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(spaceObjectFacade.detail(id));
  }

  @Operation(summary = "Query the list of directory and file.", operationId = "space:object:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Space does not exist")})
  @GetMapping
  public ApiLocaleResult<PageResult<SpaceObjectVo>> list(@Valid SpaceObjectFindDto dto) {
    return ApiLocaleResult.success(spaceObjectFacade.list(dto));
  }

  @Operation(summary = "Fulltext search the list of directory and file.", operationId = "space:object:search")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Space does not exist")})
  @GetMapping("/search")
  public ApiLocaleResult<PageResult<SpaceObjectVo>> search(@Valid SpaceObjectSearchDto dto) {
    return ApiLocaleResult.success(spaceObjectFacade.search(dto));
  }
}
