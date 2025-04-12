package cloud.xcan.angus.core.storage.interfaces.space;

import cloud.xcan.angus.core.storage.interfaces.space.facade.SpaceShareFacade;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share.SpaceShareDetailDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.share.SpaceShareObjectSearchDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.share.SpaceShareDetailVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.share.SpaceShareObjectDetailVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.share.SpaceShareObjectVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "SpaceSharePub", description = "The entrance of public access to spaces, folders and files.")
@Validated
@RestController
@RequestMapping("/pubapi/v1/space/share")
public class SpaceSharePubRest {

  @Resource
  private SpaceShareFacade spaceShareFacade;

   @Operation(description = "Query the sharing detail of the space.", operationId = "space:share:detail:pub")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Sharing does not exist")})
  @GetMapping
  public ApiLocaleResult<SpaceShareDetailVo> detail(@Valid SpaceShareDetailDto dto) {
    return ApiLocaleResult.success(spaceShareFacade.detailPub(dto));
  }

   @Operation(description = "Query the object detail of the sharing.", operationId = "space:share:object:detail:pub")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/object/{oid}")
  public ApiLocaleResult<SpaceShareObjectDetailVo> objectDetailPub(
      @Parameter(name = "id", description = "Share object id", required = true) @PathVariable("oid") Long oid,
      @Valid SpaceShareDetailDto dto) {
    return ApiLocaleResult.success(spaceShareFacade.objectDetailPub(oid, dto));
  }

   @Operation(description = "Fulltext search the object list of sharing.", operationId = "space:share:object:search:pub")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/object/search")
  public ApiLocaleResult<PageResult<SpaceShareObjectVo>> objectSearchPub(
      @Valid SpaceShareObjectSearchDto dto) {
    return ApiLocaleResult.success(spaceShareFacade.objectSearchPub(dto));
  }

}
