package cloud.xcan.angus.api.storage.file.dto;



import static cloud.xcan.angus.api.commonlink.FileProxyConstant.MAX_COMPRESS_FILE_NUM;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH_X2;

import cloud.xcan.angus.api.commonlink.CompressFormat;


import cloud.xcan.angus.validator.EnumPart;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.io.Serializable;
import java.util.Set;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Accessors(chain = true)
public class FileCompressDto implements Serializable {

  @Schema(description = "Compressed file name. If it is empty, use the current time as the file name")
  @Length(max = MAX_NAME_LENGTH_X2)
  private String name;

  @Schema(description = "Compressed file directory id. The first level file is not required")
  private Long parentDirectoryId;

  @NotNull
  @EnumPart(enumClass = CompressFormat.class, allowableValues = {"zip"})
  @Schema(description = "Compressed file format", allowableValues = "zip", requiredMode = RequiredMode.REQUIRED)
  private CompressFormat format;

  @Size(max = MAX_COMPRESS_FILE_NUM)
  @Schema(description = "Compress file or directory ids. One of the parameters ids and urls is required and ids has higher priority than urls")
  private Set<Long> ids;

  @Size(max = MAX_COMPRESS_FILE_NUM)
  @Schema(description = "Compress file urls. One of the parameters urls and ids is required and ids has higher priority than urls")
  private Set<String> urls;

}
