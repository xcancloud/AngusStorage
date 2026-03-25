package cloud.xcan.angus.api.storage.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class FileDownloadInnerDto extends FileDownloadDto {

  @NotNull
  @Schema(description = "Target tenant id for multi-tenant data scope (inner service calls).",
      requiredMode = RequiredMode.REQUIRED)
  private Long tenantId;

}
