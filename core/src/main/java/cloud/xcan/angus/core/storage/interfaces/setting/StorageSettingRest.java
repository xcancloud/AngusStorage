package cloud.xcan.angus.core.storage.interfaces.setting;

import cloud.xcan.angus.core.storage.interfaces.setting.facade.StorageSettingFacade;
import cloud.xcan.angus.core.storage.interfaces.setting.facade.dto.StorageSettingReplaceDto;
import cloud.xcan.angus.core.storage.interfaces.setting.facade.vo.StorageSettingDetailVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "StorageSetting", description = "Manage storage backend settings (S3-compatible object storage, local disk)"
    + " with multi-node file routing support.")
@Validated
@RestController
@RequestMapping("/api/v1/storage/setting")
public class StorageSettingRest {

  @Resource
  private StorageSettingFacade storageSettingFacade;

  @Operation(summary = "Update storage setting.", operationId = "storage:setting:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Update successfully")})
  @PutMapping
  public ApiLocaleResult<?> settingReplace(@Valid @RequestBody StorageSettingReplaceDto dto) {
    storageSettingFacade.settingReplace(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Find the storage setting detail.", operationId = "storage:setting:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<StorageSettingDetailVo> settingDetail() {
    return ApiLocaleResult.success(storageSettingFacade.settingDetail());
  }

}
