package cloud.xcan.angus.core.storage.interfaces.space;

import cloud.xcan.angus.core.storage.domain.space.auth.SpacePermission;
import cloud.xcan.angus.core.storage.interfaces.space.facade.SpaceAuthFacade;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.auth.SpaceAuthAddDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.auth.SpaceAuthFindDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.dto.auth.SpaceAuthReplaceDto;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.auth.SpaceAuthCurrentVo;
import cloud.xcan.angus.core.storage.interfaces.space.facade.vo.auth.SpaceAuthVo;
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
import jakarta.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

@Tag(name = "SpaceAuthorization", description = "Based on data level access control for users/groups/departments at space level.")
@Validated
@RestController
@RequestMapping("/api/v1/space")
public class SpaceAuthRest {

  @Resource
  private SpaceAuthFacade spaceAuthFacade;

  @Operation(summary = "Add the authorization of space.", operationId = "space:auth:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(value = "/{id}/auth")
  public ApiLocaleResult<IdKey<Long, Object>> add(
      @Parameter(name = "id", description = "Space id", required = true) @PathVariable("id") Long spaceId,
      @Valid @RequestBody SpaceAuthAddDto dto) {
    return ApiLocaleResult.success(spaceAuthFacade.add(spaceId, dto));
  }

  @Operation(summary = "Replace the authorization of space.", operationId = "space:auth:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  @PutMapping("/auth/{id}")
  public ApiLocaleResult<?> replace(
      @Parameter(name = "id", description = "Space authorization id", required = true) @PathVariable("id") Long id,
      @Valid @RequestBody SpaceAuthReplaceDto dto) {
    spaceAuthFacade.replace(id, dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Enabled or disabled the authorization of space.", operationId = "space:auth:enabled")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Enabled successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/{id}/auth/enabled")
  public ApiLocaleResult<?> enabled(
      @Parameter(name = "id", description = "Space id", required = true) @PathVariable("id") Long spaceId,
      @Parameter(name = "enabled", description = "Enabled or Disabled", required = true) @RequestParam(value = "enabled", required = true) Boolean enabled) {
    spaceAuthFacade.enabled(spaceId, enabled);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Query authorization status of space.", operationId = "space:auth:status")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}/auth/status")
  public ApiLocaleResult<Boolean> status(
      @Parameter(name = "id", description = "Space id", required = true) @PathVariable("id") Long spaceId) {
    return ApiLocaleResult.success(spaceAuthFacade.status(spaceId));
  }

  @Operation(summary = "Delete the authorization of space", operationId = "space:auth:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/auth/{id}")
  public void delete(
      @Parameter(name = "id", description = "Space authorization id", required = true) @PathVariable("id") Long id) {
    spaceAuthFacade.delete(id);
  }

  @Operation(summary = "Query the user authorization permission of space.", operationId = "space:user:auth:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}/user/{userId}/auth")
  public ApiLocaleResult<List<SpacePermission>> userAuth(
      @Parameter(name = "id", description = "Space id", required = true) @PathVariable("id") Long spaceId,
      @Parameter(name = "userId", description = "userId", required = true) @PathVariable("userId") Long userId,
      @Parameter(name = "admin", description = "Required when the query contains administrator permissions") Boolean admin) {
    return ApiLocaleResult.success(spaceAuthFacade.userAuth(spaceId, userId, admin));
  }

  @Operation(summary = "Query the current user authorization permission", operationId = "space:user:auth:current")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}/user/auth/current")
  public ApiLocaleResult<SpaceAuthCurrentVo> currentUserAuth(
      @Parameter(name = "id", description = "Space id", required = true) @PathVariable("id") Long spaceId,
      @Parameter(name = "admin", description = "Required when the query contains administrator permissions") Boolean admin) {
    return ApiLocaleResult.success(spaceAuthFacade.currentUserAuth(spaceId, admin));
  }

  @Operation(summary = "Query the current user authorization permission", operationId = "space:user:auth:current:batch")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/user/auth/current")
  public ApiLocaleResult<Map<Long, SpaceAuthCurrentVo>> currentUserAuths(
      @Parameter(name = "ids", description = "Space ids", required = true) @RequestParam(value = "ids") @NotEmpty HashSet<Long> ids,
      @Parameter(name = "admin", description = "Required when the query contains administrator permissions") Boolean admin) {
    return ApiLocaleResult.success(spaceAuthFacade.currentUserAuths(ids, admin));
  }

  @Operation(summary = "Check the user authorization or administrator permission of space", operationId = "space:auth:check")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Resource existed")})
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}/auth/{permission}/check")
  public ApiLocaleResult<?> authCheck(
      @Parameter(name = "id", description = "Space id", required = true) @PathVariable("id") Long spaceId,
      @Parameter(name = "permission", description = "Space authorized permission", required = true) @PathVariable("permission") SpacePermission permission,
      @Parameter(name = "userId", description = "Authorization user id", required = true) Long userId) {
    spaceAuthFacade.authCheck(spaceId, permission, userId);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Query the list of space authorization.", operationId = "space:auth:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/auth")
  public ApiLocaleResult<PageResult<SpaceAuthVo>> list(@Valid SpaceAuthFindDto dto) {
    return ApiLocaleResult.success(spaceAuthFacade.list(dto));
  }

}







