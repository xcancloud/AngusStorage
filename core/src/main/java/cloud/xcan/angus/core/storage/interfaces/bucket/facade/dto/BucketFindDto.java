package cloud.xcan.angus.core.storage.interfaces.bucket.facade.dto;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;

import cloud.xcan.angus.remote.PageQuery;
import jakarta.validation.Valid;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;


@Valid
@Getter
@Setter
@Accessors(chain = true)
public class BucketFindDto extends PageQuery implements Serializable {

  private Long id;

  private String name;

  @DateTimeFormat(pattern = DATE_FMT)
  private LocalDateTime createdDate;

}
