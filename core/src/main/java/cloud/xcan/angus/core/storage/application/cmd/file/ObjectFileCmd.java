package cloud.xcan.angus.core.storage.application.cmd.file;

import cloud.xcan.angus.api.commonlink.CompressFormat;
import cloud.xcan.angus.core.storage.domain.file.ObjectFile;
import cloud.xcan.angus.core.utils.SpringAppDirUtils;
import jakarta.validation.constraints.NotNull;
import java.io.File;
import java.util.List;
import java.util.Set;
import org.springframework.web.multipart.MultipartFile;

public interface ObjectFileCmd {

  String COMPRESS_FILE_TEM_PATH = new SpringAppDirUtils().getTmpDir()
      + "compress" + File.separator;

  String COMPRESS_FILE_DES_TEM_PATH = new SpringAppDirUtils().getTmpDir()
      + "compress" + File.separator + "des";

  List<ObjectFile> upload(String bizKey, Long spaceId, Long parentDirId,
      boolean ignoreLocalStore, Long outFid, MultipartFile[] files);

  ObjectFile download(@NotNull String filename, Long fid, String fpt, String fproc, Long sid,
      String spt, String password);

  ObjectFile compress(String name, Long parentDirectoryId, CompressFormat format,
      Set<String> urls, Set<Long> ids);

}
