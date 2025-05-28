package cloud.xcan.angus.core.storage.interfaces.file;

import cloud.xcan.angus.api.storage.file.dto.FileDownloadDto;
import cloud.xcan.angus.api.storage.file.dto.FileUploadDto;
import cloud.xcan.angus.api.storage.file.vo.FileUploadVo;
import cloud.xcan.angus.core.storage.interfaces.file.facade.FileFacade;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "File", description = "Unified upload/download APIs with multi-protocol support (HTTP/S3).")
@Validated
@RestController
@RequestMapping("/api/v1/file")
public class FileRest {

  @Resource
  private FileFacade fileFacade;

  @Operation(summary = "Upload file by multipart/form-data.", operationId = "file:upload")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Upload successfully ")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiLocaleResult<List<FileUploadVo>> upload(
      @Parameter(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE), schema = @Schema(type = "object")) @Valid FileUploadDto dto) {
    return ApiLocaleResult.success(fileFacade.upload(dto));
  }

  @Operation(summary = "Download file.", operationId = "file:download")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Download successfully"),
      @ApiResponse(responseCode = "404", description = "Object does not exist")})
  @GetMapping(value = "/{filename:.+}")
  public void download(
      @Parameter(name = "filename", description = "File name", required = true) @PathVariable("filename") String filename,
      @Valid FileDownloadDto dto, HttpServletRequest request, HttpServletResponse response) {
    fileFacade.download(filename, dto, request, response);
  }

}
