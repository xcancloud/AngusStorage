package cloud.xcan.angus.core.storage.interfaces.file.facade;

import cloud.xcan.angus.api.storage.file.dto.FileCompressDto;
import cloud.xcan.angus.api.storage.file.dto.FileDownloadDto;
import cloud.xcan.angus.api.storage.file.dto.FileUploadDto;
import cloud.xcan.angus.api.storage.file.vo.FileUploadVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;


public interface FileFacade {

  List<FileUploadVo> upload(FileUploadDto dto);

  void download(String filename, FileDownloadDto dto,
      HttpServletRequest request, HttpServletResponse response);

  FileUploadVo compress(FileCompressDto dto);

}
