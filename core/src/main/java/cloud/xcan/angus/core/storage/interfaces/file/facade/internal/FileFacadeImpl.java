package cloud.xcan.angus.core.storage.interfaces.file.facade.internal;

import static cloud.xcan.angus.core.storage.application.converter.SpaceObjectConverter.formatShareDownloadUrl;
import static cloud.xcan.angus.core.storage.interfaces.file.facade.internal.assembler.FileAssembler.toUploadVo;
import static cloud.xcan.angus.core.utils.CoreUtils.getUrlInputStream;
import static cloud.xcan.angus.core.utils.ServletUtils.buildSupportRangeDownload;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.api.storage.file.dto.FileCompressDto;
import cloud.xcan.angus.api.storage.file.dto.FileDownloadDto;
import cloud.xcan.angus.api.storage.file.dto.FileUploadDto;
import cloud.xcan.angus.api.storage.file.dto.FileUploadInnerDto;
import cloud.xcan.angus.api.storage.file.vo.FileUploadVo;
import cloud.xcan.angus.core.storage.application.cmd.file.ObjectFileCmd;
import cloud.xcan.angus.core.storage.domain.file.ObjectFile;
import cloud.xcan.angus.core.storage.infra.store.impl.ObjectClientFactory;
import cloud.xcan.angus.core.storage.infra.store.utils.FileBytesCache;
import cloud.xcan.angus.core.storage.interfaces.file.facade.FileFacade;
import cloud.xcan.angus.core.storage.interfaces.file.facade.internal.assembler.FileAssembler;
import cloud.xcan.angus.core.utils.PrincipalContextUtils;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import cloud.xcan.angus.spec.utils.DateUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;


@Component
public class FileFacadeImpl implements FileFacade {

  @Resource
  private ObjectFileCmd objectFileCmd;

  @Resource
  private FileBytesCache fileBytesCache;

  @Override
  public List<FileUploadVo> upload(FileUploadDto dto) {
    if (PrincipalContextUtils.isInnerApi()) {
      PrincipalContext.get().setOptTenantId(((FileUploadInnerDto) dto).getTenantId());
    }
    return objectFileCmd.upload(dto.getBizKey(), dto.getSpaceId(), dto.getParentDirectoryId(),
            dto.getProjectId(),false, null, nullSafe(dto.getExtraFiles(), false), dto.getFiles())
        .stream().map(FileAssembler::toUploadVo)
        .collect(Collectors.toList());
  }

  @SneakyThrows
  @Override
  public void download(String filename, FileDownloadDto dto, HttpServletRequest request,
      HttpServletResponse response) {
    ObjectFile objectFileDb = objectFileCmd.download(filename, dto.getFid(),
        dto.getFpt(), dto.getFproc(), dto.getSid(), dto.getSpt(), dto.getPassword());

    // Range and large file downloads have performance issues
    // Is improving performance based on memory caching byte data?
    byte[] data = getFileBytes(objectFileDb, dto);
    buildSupportRangeDownload(objectFileDb.getCacheAge(), objectFileDb.getMediaType(),
        objectFileDb.getName(), data.length, DateUtils.asDate(objectFileDb.getLastModifiedDate()),
        new ByteArrayInputStream(data), request, response);
  }

  @Override
  public FileUploadVo compress(FileCompressDto dto) {
    return toUploadVo(objectFileCmd.compress(dto.getName(), dto.getParentDirectoryId(),
        dto.getFormat(), dto.getUrls(), dto.getIds()));
  }

  private @NotNull byte[] getFileBytes(ObjectFile objectFileDb, FileDownloadDto dto)
      throws IOException {
    byte[] data = fileBytesCache.getFileBytes(objectFileDb.getId());
    if (isNull(data)) {
      InputStream inputStream = isNotEmpty(objectFileDb.getForwardUrl())
          ? getUrlInputStream(formatShareDownloadUrl(objectFileDb.getForwardUrl(), dto.getSid(),
          dto.getSpt(), dto.getPassword())) : ObjectClientFactory.of(objectFileDb.getStoreType())
          .getObject(objectFileDb.getBucketName(), objectFileDb.getPath()).getObjectContent();
      data = inputStream.readAllBytes();
      fileBytesCache.cacheFileBytes(objectFileDb.getId(), data);
    }
    return data;
  }

}
