package cloud.xcan.angus.core.storage.domain;

import cloud.xcan.angus.api.enums.FileType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FileSummary {

  private FileType key;
  private long total;

}
