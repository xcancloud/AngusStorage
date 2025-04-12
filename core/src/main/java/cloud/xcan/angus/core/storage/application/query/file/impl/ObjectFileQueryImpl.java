package cloud.xcan.angus.core.storage.application.query.file.impl;

import static cloud.xcan.angus.api.commonlink.FileProxyConstant.DOWNLOAD_FILE_API_PATH;
import static cloud.xcan.angus.api.commonlink.FileProxyConstant.DOWNLOAD_FILE_PUB_API_PATH;
import static cloud.xcan.angus.api.commonlink.FileProxyConstant.FILE_PUBLIC_TOKEN_NAME;
import static cloud.xcan.angus.api.commonlink.FileProxyConstant.FILE_QUERY_ID_NAME;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isPrivateEdition;
import static cloud.xcan.angus.spec.SpecConstant.DEFAULT_ENCODING;

import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.storage.application.query.file.ObjectFileQuery;
import cloud.xcan.angus.core.storage.domain.bucket.config.BucketBizConfig;
import cloud.xcan.angus.core.storage.domain.file.ObjectFile;
import cloud.xcan.angus.core.storage.domain.file.ObjectFileRepo;
import cloud.xcan.angus.core.storage.infra.store.ObjectProperties;
import java.net.URLEncoder;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Biz
@Slf4j
public class ObjectFileQueryImpl implements ObjectFileQuery {

  @Resource
  private ObjectFileRepo objectFileRepo;

  @Resource
  private ObjectProperties objectProperties;

  @Override
  public ObjectFile checkAndFind(Long id) {
    return objectFileRepo.findValidById(id)
        .orElseThrow(() -> ResourceNotFound.of(id, "File"));
  }

  @Override
  public ObjectFile findByUniqueName(String filename) {
    return objectFileRepo.findValidByUniqueName(filename)
        .orElseThrow(() -> ResourceNotFound.of(filename, "File"));
  }

  @SneakyThrows
  @Override
  public String assembleDownloadUrl(Long fid, String fileName, BucketBizConfig bucketBizConfigDb,
      String publicToken) {
    return objectProperties.getProxyAddress() + (isPrivateEdition() ? "" : "/storage")
        + (bucketBizConfigDb.getPublicAccess()
        ? DOWNLOAD_FILE_PUB_API_PATH : DOWNLOAD_FILE_API_PATH)
        + "/" + URLEncoder.encode(fileName, DEFAULT_ENCODING)
        + "?" + FILE_QUERY_ID_NAME + "=" + fid
        + (bucketBizConfigDb.getPublicTokenAuth()
        ? "&" + FILE_PUBLIC_TOKEN_NAME + "=" + publicToken : "");
  }

}
