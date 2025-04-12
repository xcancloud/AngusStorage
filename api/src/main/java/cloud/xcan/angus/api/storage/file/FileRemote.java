package cloud.xcan.angus.api.storage.file;

import cloud.xcan.angus.api.storage.file.dto.FileDownloadDto;
import cloud.xcan.angus.api.storage.file.vo.FileUploadVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import feign.Headers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "${xcan.service.storage:XCAN-ANGUSSTORAGE.BOOT}")
public interface FileRemote {

  @Operation(description = "Upload file by multipart/form-data", operationId = "file:upload")
  @PostMapping("/api/v1/file/upload")
  @Headers("Content-Type: multipart/form-data")
  ApiLocaleResult<List<FileUploadVo>> upload(
      @RequestPart("files") MultipartFile[] files, @RequestPart("spaceId") String spaceId,
      @RequestPart("bizKey") String bizKey, @RequestPart("parentDirectoryId") Long parentDirId);

  @Operation(description = "Download file", operationId = "file:download")
  @GetMapping(value = "/api/v1/file/{filename:.+}")
  ResponseEntity<org.springframework.core.io.Resource> download(
      @Parameter(name = "filename", description = "File name", required = true)
      @PathVariable("filename") String filename,
      @SpringQueryMap FileDownloadDto dto);
}
