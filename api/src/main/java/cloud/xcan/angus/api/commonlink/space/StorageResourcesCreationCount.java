package cloud.xcan.angus.api.commonlink.space;

import cloud.xcan.angus.api.enums.FileResourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class StorageResourcesCreationCount {

  @Schema(description = "Total number of file")
  private long allSpaceFile;

  @Schema(description = "Number of new file added in the past 7 days")
  private long spaceFileByLast7Day;

  @Schema(description = "Number of new file added in the past 30 days")
  private long spaceFileByLast30Day;

  private Map<FileResourceType, Long> spaceFileByResourceType;

}
