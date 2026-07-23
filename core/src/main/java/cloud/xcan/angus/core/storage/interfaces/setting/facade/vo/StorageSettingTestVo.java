package cloud.xcan.angus.core.storage.interfaces.setting.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "存储连接测试结果")
public class StorageSettingTestVo implements Serializable {

  @Schema(description = "是否连通")
  private Boolean success;

  @Schema(description = "被测存储类型")
  private String storeType;

  @Schema(description = "结果说明")
  private String message;
}
