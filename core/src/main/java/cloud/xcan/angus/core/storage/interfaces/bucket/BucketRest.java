package cloud.xcan.angus.core.storage.interfaces.bucket;

import cloud.xcan.angus.core.spring.condition.PrivateEditionCondition;
import cloud.xcan.angus.core.storage.interfaces.bucket.facade.BucketFacade;
import cloud.xcan.angus.core.storage.interfaces.bucket.facade.dto.BucketAddDto;
import cloud.xcan.angus.core.storage.interfaces.bucket.facade.dto.BucketFindDto;
import cloud.xcan.angus.core.storage.interfaces.bucket.facade.vo.BucketVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.annotations.DoInFuture;
import cloud.xcan.angus.spec.experimental.IdKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Bucket", description = "Create, configure, and manage storage buckets with namespace isolation and permission controls.")
@DoInFuture("Untested, to be used in a future version")
@Conditional(PrivateEditionCondition.class)
@Validated
@RestController
@RequestMapping("/api/v1/bucket")
public class BucketRest {

  @Resource
  private BucketFacade bucketFacade;

  @Operation(summary = "Add bucket.", operationId = "bucket:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<IdKey<Long, Object>> add(@Valid @RequestBody BucketAddDto dto) {
    return ApiLocaleResult.success(bucketFacade.add(dto));
  }

  @Operation(summary = "Delete bucket.", operationId = "bucket:delete")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @DeleteMapping(value = "/{name}")
  public void delete(
      @Parameter(name = "name", description = "Bucket name", required = true) @PathVariable("name") String name) {
    bucketFacade.delete(name);
  }

  @Operation(summary = "Query the detail of bucket.", operationId = "bucket:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Bucket does not exist")})
  @GetMapping(value = "/{name}")
  public ApiLocaleResult<BucketVo> detail(
      @Parameter(name = "name", description = "Bucket name", required = true) @PathVariable("name") String name) {
    return ApiLocaleResult.success(bucketFacade.detail(name));
  }

  @Operation(summary = "Query the list of bucket.", operationId = "bucket:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Query successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<BucketVo>> list(@Valid BucketFindDto dto) {
    return ApiLocaleResult.success(bucketFacade.find(dto));
  }

}
