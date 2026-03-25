package cloud.xcan.angus.api.storage.file;

import cloud.xcan.angus.api.storage.file.FileRemote.FeignUploadConfig;
import cloud.xcan.angus.api.storage.file.dto.FileDeleteByFidsDto;
import cloud.xcan.angus.api.storage.file.dto.FileDownloadDto;
import cloud.xcan.angus.api.storage.file.vo.FileUploadVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import feign.Request;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "${xcan.service.storage:XCAN-ANGUSSTORAGE.BOOT}",
    path = "/api/v1/file",
    configuration = FeignUploadConfig.class)
public interface FileRemote {

  @Operation(summary = "Upload files by multipart/form-data", operationId = "uploadFiles")
  @PostMapping(
      value = "/upload",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  ApiLocaleResult<List<FileUploadVo>> upload(
      @RequestPart("files") MultipartFile[] files,
      @RequestPart("spaceId") String spaceId,
      @RequestPart("bizKey") String bizKey,
      @RequestPart("parentDirectoryId") Long parentDirId,
      @RequestPart(value = "projectId", required = false) Long projectId,
      @RequestPart(value = "extraFiles", required = false) Boolean extraFiles);

  @Operation(summary = "Download file", operationId = "downloadFile")
  @GetMapping(value = "/{filename:.+}")
  ResponseEntity<org.springframework.core.io.Resource> download(
      @Parameter(name = "filename", description = "File name", required = true)
      @PathVariable("filename") String filename,
      @SpringQueryMap FileDownloadDto dto);

  @Operation(summary = "Delete files by object file ids (fid)", operationId = "deleteFilesByFids")
  @DeleteMapping
  void deleteByFids(@RequestBody FileDeleteByFidsDto dto);

  @Configuration
  public class FeignUploadConfig {

    @Bean
    public Request.Options feignOptions() {
      // 连接超时60秒，读取超时600秒
      return new Request.Options(60 * 000, 10 * 60 * 000);
    }
  }
}
