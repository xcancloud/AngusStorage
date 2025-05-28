package cloud.xcan.angus.core.storage.interfaces.space;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.core.storage.interfaces.space.facade.SpaceShareFacade;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share.SpaceShareAddDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share.SpaceShareFindDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share.SpaceShareQuickAddDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share.SpaceShareSearchDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share.SpaceShareUpdateDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.share.SpaceShareAddVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.share.SpaceShareVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "SpaceShare", description = "Secure cross-tenant/cross-user file sharing with link expiration and password protection.")
@Validated
@RestController
@RequestMapping("/api/v1/space/share")
public class SpaceShareRest {

  @Resource
  private SpaceShareFacade spaceShareFacade;

   @Operation(summary = "Add the sharing of space.", operationId = "space:share:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully sharing")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<SpaceShareAddVo> add(@Valid @RequestBody SpaceShareAddDto dto) {
    return ApiLocaleResult.success(spaceShareFacade.add(dto));
  }

   @Operation(summary = "Add the sharing of space.", operationId = "space:share:quick:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully sharing")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/quick")
  public ApiLocaleResult<String> quickAdd(@Valid @RequestBody SpaceShareQuickAddDto dto) {
    return ApiLocaleResult.successData(spaceShareFacade.quickAdd(dto));
  }

   @Operation(summary = "Update the sharing of space.", operationId = "space:share:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated successfully"),
      @ApiResponse(responseCode = "404", description = "Sharing does not exist")})
  @PatchMapping
  public ApiLocaleResult<?> update(@Valid @RequestBody SpaceShareUpdateDto dto) {
    spaceShareFacade.update(dto);
    return ApiLocaleResult.success();
  }

   @Operation(summary = "Delete the sharing of space.", operationId = "space:share:delete")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Sharing does not exist")})
  @DeleteMapping
  public void delete(
      @Valid @Size(max = MAX_BATCH_SIZE) @Parameter(name = "ids", description = "Space sharing ids", required = true)
      @RequestParam("ids") HashSet<Long> ids) {
    spaceShareFacade.delete(ids);
  }

   @Operation(summary = "Query the detail of space sharing.", operationId = "space:share:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Sharing does not exist")})
  @GetMapping(value = "/{id}")
  public ApiLocaleResult<SpaceShareVo> detail(
      @Parameter(name = "id", description = "Share id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(spaceShareFacade.detail(id));
  }

   @Operation(summary = "Query the sharing list of space.", operationId = "space:share:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<SpaceShareVo>> list(@Valid SpaceShareFindDto dto) {
    return ApiLocaleResult.success(spaceShareFacade.list(dto));
  }

   @Operation(summary = "Fulltext search the space sharing.", operationId = "space:share:search")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/search")
  public ApiLocaleResult<PageResult<SpaceShareVo>> search(@Valid SpaceShareSearchDto dto) {
    return ApiLocaleResult.success(spaceShareFacade.search(dto));
  }

}
