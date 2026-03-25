package cloud.xcan.angus.api.storage.file;

import cloud.xcan.angus.api.storage.file.FileRemote.FeignUploadConfig;
import cloud.xcan.angus.api.storage.file.dto.FileDeleteByFidsDto;
import cloud.xcan.angus.api.storage.file.dto.FileDownloadDto;
import cloud.xcan.angus.api.storage.file.vo.FileUploadVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
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
    path = "/innerapi/v1/file",
    configuration = FeignUploadConfig.class)
public interface FileInnerRemote {

  @Operation(summary = "Upload files by multipart/form-data (inner API)",
      operationId = "uploadFilesByInnerApi")
  @PostMapping(
      value = "/upload",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  ApiLocaleResult<List<FileUploadVo>> upload(
      @RequestPart("files") MultipartFile[] files,
      @RequestPart("tenantId") Long tenantId,
      @RequestPart("spaceId") String spaceId,
      @RequestPart("bizKey") String bizKey,
      @RequestPart("parentDirectoryId") Long parentDirId,
      @RequestPart(value = "projectId", required = false) Long projectId,
      @RequestPart(value = "extraFiles", required = false) Boolean extraFiles);

  @Operation(summary = "Download file (inner API)", operationId = "downloadFileByInnerApi")
  @GetMapping(value = "/{filename:.+}")
  ResponseEntity<org.springframework.core.io.Resource> download(
      @Parameter(name = "filename", description = "File name", required = true)
      @PathVariable("filename") String filename,
      @SpringQueryMap FileDownloadDto dto);

  @Operation(summary = "Delete files by object file ids (fid, inner API)",
      operationId = "deleteFilesByFidsByInnerApi")
  @DeleteMapping
  void deleteByFids(@RequestBody FileDeleteByFidsDto dto);

}
