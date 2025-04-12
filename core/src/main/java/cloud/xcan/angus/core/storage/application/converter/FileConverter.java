package cloud.xcan.angus.core.storage.application.converter;

import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.api.enums.PlatformStoreType;
import cloud.xcan.angus.core.spring.boot.ApplicationInfo;
import cloud.xcan.angus.core.storage.domain.bucket.config.BucketBizConfig;
import cloud.xcan.angus.core.storage.domain.file.ObjectFile;
import cloud.xcan.angus.core.storage.domain.space.Space;
import cloud.xcan.angus.core.storage.infra.store.model.UploadType;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public class FileConverter {

  public static ObjectFile toUploadObjectFile(MultipartFile file, String fileName,
      String objectName, String uniqueName, String downloadUrl, Long fid, Long oid,
      String storeAddress, Space spaceDb, String publicToken, PlatformStoreType storeType,
      BucketBizConfig bizConfigDb, Long parentDirId, ApplicationInfo applicationInfo) {
    ObjectFile objectFile = new ObjectFile().setId(fid)
        .setProjectId(nullSafe(spaceDb.getProjectId(), -1L))
        .setName(fileName)
        .setUniqueName(uniqueName)
        .setOid(oid)
        .setPath(objectName)
        .setSize(file.getSize())
        .setContentType(file.getContentType())
        .setStoreAddress(storeAddress)
        .setSpaceId(spaceDb.getId())
        .setStoreType(storeType)
        .setParentDirectoryId(parentDirId)
        .setInstanceId(applicationInfo.getInstanceId())
        .setBizKey(bizConfigDb.getBizKey())
        .setBucketName(bizConfigDb.getBucketName())
        .setUploadId(UUID.randomUUID().toString())
        .setUploadType(UploadType.NORMAL)
        .setCompleted(true)
        .setStoreDeleted(false)
        .setDeletedRetryNum(0)
        .setDownloadUrl(downloadUrl)
        .setPublicToken(publicToken);
    // Fix:: Value is null when multi tenant control is turned off or /innerapi upload
    objectFile.setTenantId(getOptTenantId());
    objectFile.setCreatedBy(getUserId()).setCreatedDate(LocalDateTime.now())
        .setLastModifiedBy(getUserId()).setLastModifiedDate(LocalDateTime.now());
    return objectFile;
  }

}
