package cloud.xcan.angus.api.commonlink.space;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class StorageResourcesCount {

  @Schema(description = "Total number of spaces")
  private long allSpaces;

  @Schema(description = "Total number of space files")
  private long allSpaceFiles;

  @Schema(description = "Total number of space directories")
  private long allSpaceDirectories;

}
