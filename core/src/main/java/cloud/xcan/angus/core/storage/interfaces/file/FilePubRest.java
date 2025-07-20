package cloud.xcan.angus.core.storage.interfaces.file;

import cloud.xcan.angus.api.storage.file.dto.FileDownloadDto;
import cloud.xcan.angus.core.storage.interfaces.file.facade.FileFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "FilePub", description =
    "A publicly accessible interface for external users to download files "
        + "via public APIs or web portals without authentication requirements.")
@Validated
@RestController
@RequestMapping("/pubapi/v1/file")
public class FilePubRest {

  @Resource
  private FileFacade fileFacade;

  @Operation(summary = "Download file.", operationId = "file:download:pub")
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
