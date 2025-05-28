package cloud.xcan.angus.api.storage.file.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_PUBLIC_TOKEN_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
@Accessors(chain = true)
public class FileDownloadDto implements Serializable {

  @Schema(description = "File id.")
  private Long fid;

  @Length(max = MAX_PUBLIC_TOKEN_LENGTH)
  @Schema(description = "File public token.", maxLength = MAX_PUBLIC_TOKEN_LENGTH)
  private String fpt;

  /**
   * `fproc` parameter format: command + "," + param1 + "," + param2 + "," + ...
   */
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = """
      File processing extension parameters, support two scaling modes:\s
      - Proportionally, such as: image/resize, m_scale, s_120, fixed width\s
      - Fixed width and height, such as: image/resize, m_fixed, w_100, h_100""",
      maxLength = MAX_CODE_LENGTH)
  private String fproc;

  @Schema(description = "Share id.")
  private Long sid;

  @Length(max = MAX_PUBLIC_TOKEN_LENGTH)
  @Schema(description = "Share public token.")
  private String spt;

  @Length(max = MAX_PUBLIC_TOKEN_LENGTH)
  @Schema(description = "Access password, required when private share.")
  private String password;

}
