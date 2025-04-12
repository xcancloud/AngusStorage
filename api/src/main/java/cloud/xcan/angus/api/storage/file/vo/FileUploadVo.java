package cloud.xcan.angus.api.storage.file.vo;

import cloud.xcan.angus.api.enums.PlatformStoreType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class FileUploadVo implements Serializable {

  @Schema(description = "File id")
  private Long id;

  @Schema(description = "File original name")
  private String name;

  @Schema(description = "File name during actual storage, unique. Format：name + .id + .extension")
  private String uniqueName;

  @Schema(description = "File download url")
  private String url;

  @Schema(description = "File store address(Oss url or local path)")
  private String storeAddress;

  private PlatformStoreType storeType;

}
