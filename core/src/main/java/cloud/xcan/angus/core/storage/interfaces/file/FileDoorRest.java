package cloud.xcan.angus.core.storage.interfaces.file;

import cloud.xcan.angus.api.storage.file.dto.FileUploadInnerDto;
import cloud.xcan.angus.api.storage.file.vo.FileUploadVo;
import cloud.xcan.angus.core.storage.interfaces.file.facade.FileFacade;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "FileInner", description = "A secured interface restricted to authenticated internal services/systems "
    + "for inter-service file transfers, typically requiring service-to-service authentication (OAuth2 client credentials).")
@Validated
@RestController
@RequestMapping("/innerapi/v1/file")
public class FileDoorRest {

  @Resource
  private FileFacade fileFacade;

  @Operation(summary = "Upload file by multipart/form-data.", operationId = "file:upload:inner")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Upload successfully ")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ApiLocaleResult<List<FileUploadVo>> upload(@Valid FileUploadInnerDto dto) {
    return ApiLocaleResult.success(fileFacade.upload(dto));
  }

}
