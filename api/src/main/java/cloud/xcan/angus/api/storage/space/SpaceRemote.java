package cloud.xcan.angus.api.storage.space;

import cloud.xcan.angus.api.commonlink.space.StorageResourcesCount;
import cloud.xcan.angus.api.commonlink.space.StorageResourcesCreationCount;
import cloud.xcan.angus.api.storage.space.dto.SpaceAssetsCountDto;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "${xcan.service.storage:XCAN-ANGUSSTORAGE.BOOT}")
public interface SpaceRemote {

  @Operation(summary = "Space data resources statistics", operationId = "space:resources:count")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/api/v1/space/resources/count")
  ApiLocaleResult<StorageResourcesCount> resourcesStatistics(
      @Valid @SpringQueryMap SpaceAssetsCountDto dto);

  @Operation(summary = "Space data resources creation statistics", operationId = "space:resources:creation:count")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/api/v1/space/resources/creation/count")
  ApiLocaleResult<StorageResourcesCreationCount> resourcesCreationStatistics(
      @Valid @SpringQueryMap SpaceAssetsCountDto dto);

}
