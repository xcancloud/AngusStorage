package cloud.xcan.angus.api.storage.file.dto;

import static cloud.xcan.angus.api.commonlink.StorageConstant.DEFAULT_DATA_FILE_BIZ_KEY;
import static cloud.xcan.angus.api.commonlink.StorageConstant.MAX_REQUEST_FILES_NUM;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BIZ_KEY_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Accessors(chain = true)
public class FileUploadDto implements Serializable {

  @Schema(description = "Space id. At least one parameter of spaceId and bizKey is required")
  private Long spaceId;

  @Length(max = MAX_BIZ_KEY_LENGTH)
  @Schema(description = "Business key. At least one parameter of spaceId and bizKey is required", example = DEFAULT_DATA_FILE_BIZ_KEY)
  private String bizKey;

  @Schema(description = "Parent directory id. The first level directory is not required")
  private Long parentDirectoryId;

  @Schema(description = "Whether to extract the file structure inside the compressed package")
  private Boolean extraFiles;

  @NotNull
  @Size(max = MAX_REQUEST_FILES_NUM)
  @Schema(description = "Upload files, per request request supports up to "
      + MAX_REQUEST_FILES_NUM + " files", requiredMode = RequiredMode.REQUIRED)
  private MultipartFile[] files;

  //  private MultipartFile file1;
  //  private MultipartFile file2;
  //  private MultipartFile file3;

}
