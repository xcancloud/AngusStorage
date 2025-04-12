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
public class FileUploadInnerDto extends FileUploadDto {

  @NotNull
  @Schema(description = "Upload tenant id", requiredMode = RequiredMode.REQUIRED)
  private Long tenantId;

}
