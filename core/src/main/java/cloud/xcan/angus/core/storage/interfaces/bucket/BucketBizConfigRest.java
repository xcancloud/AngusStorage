package cloud.xcan.angus.core.storage.interfaces.bucket;

import cloud.xcan.angus.core.spring.condition.PrivateEditionCondition;
import cloud.xcan.angus.core.storage.interfaces.bucket.facade.BucketBizConfigFacade;
import cloud.xcan.angus.core.storage.interfaces.bucket.facade.dto.BucketConfigDto;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.spec.annotations.DoInFuture;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Bucket", description = "Map buckets to business units for data categorization and maintenance workflows.")
@DoInFuture("Untested, to be used in a future version")
@Conditional(PrivateEditionCondition.class)
@Validated
@RestController
@RequestMapping("/api/v1/bucket")
public class BucketBizConfigRest {

  @Resource
  private BucketBizConfigFacade bucketBizConfigFacade;

  @Operation(description = "Configure business and bucket, no modification allowed after config.", operationId = "bucket:biz:config")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Assign succeeded")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/biz/config")
  public ApiLocaleResult<?> config(@Valid @RequestBody BucketConfigDto dto) {
    bucketBizConfigFacade.config(dto);
    return ApiLocaleResult.success();
  }

  @Operation(description = "Delete business and bucket configuration.", operationId = "bucket:biz:config:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Configuration succeeded")})
  @ResponseStatus(HttpStatus.CREATED)
  @DeleteMapping("/biz/{bizKey}/config")
  public ApiLocaleResult<?> configDelete(
      @Parameter(name = "name", description = "Business key", required = true) @PathVariable("bizKey") String bizKey) {
    bucketBizConfigFacade.configDelete(bizKey);
    return ApiLocaleResult.success();
  }

}
