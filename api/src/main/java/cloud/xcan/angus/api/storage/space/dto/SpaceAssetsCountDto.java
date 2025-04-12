package cloud.xcan.angus.api.storage.space.dto;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;

import cloud.xcan.angus.api.enums.AuthObjectType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@Accessors(chain = true)
public class SpaceAssetsCountDto {

  @Schema(description = "Query organization type, default USER")
  private AuthObjectType creatorObjectType;

  @Schema(description = "Query organization id")
  private Long creatorObjectId;

  @NotNull
  @Schema(description = "Project id", requiredMode = RequiredMode.REQUIRED)
  private Long projectId;

  /**
   * Fix Fegin RPC: Failed to convert property value of type 'java.lang.String' to required type
   * 'java.time.LocalDateTime'
   * <p>
   * Parse attempt failed for value [2024-07-21T23:16:36]
   */
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @DateTimeFormat(pattern = DATE_FMT)
  @Schema(description = "Resources creation start date")
  private LocalDateTime createdDateStart;

  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @DateTimeFormat(pattern = DATE_FMT)
  @Schema(description = "Resources creation end date")
  private LocalDateTime createdDateEnd;

}
