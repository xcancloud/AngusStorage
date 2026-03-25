package cloud.xcan.angus.api.storage.file.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class FileDeleteByFidsDto implements Serializable {

  @NotEmpty
  @Size(max = MAX_BATCH_SIZE)
  @Schema(description = "Object file ids (fid in download URL).", requiredMode = RequiredMode.REQUIRED)
  private HashSet<Long> fids;

}
