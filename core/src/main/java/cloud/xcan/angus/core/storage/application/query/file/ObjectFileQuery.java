package cloud.xcan.angus.core.storage.application.query.file;

import cloud.xcan.angus.core.storage.domain.bucket.config.BucketBizConfig;
import cloud.xcan.angus.core.storage.domain.file.ObjectFile;

public interface ObjectFileQuery {

  ObjectFile checkAndFind(Long id);

  ObjectFile findByUniqueName(String filename);

  String assembleDownloadUrl(Long fid, String fileName, BucketBizConfig bucketBizConfigDb,
      String publicToken);

}
