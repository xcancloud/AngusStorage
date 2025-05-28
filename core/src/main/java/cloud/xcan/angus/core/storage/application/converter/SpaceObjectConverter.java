package cloud.xcan.angus.core.storage.application.converter;

import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.enums.FileType;
import cloud.xcan.angus.api.enums.PlatformStoreType;
import cloud.xcan.angus.core.storage.domain.space.Space;
import cloud.xcan.angus.core.storage.domain.space.SpaceSummary;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObject;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObjectSummary;
import cloud.xcan.angus.spec.unit.DataSize;
import java.time.LocalDateTime;
import org.springframework.web.multipart.MultipartFile;

public class SpaceObjectConverter {

  public static SpaceSummary toSpaceSummary(Space spaceDb, long tenantQuotaSize) {
    long actualQuotaSize = nonNull(spaceDb.getQuotaSize())
        ? DataSize.parse(spaceDb.getQuotaSize()).toBytes() : tenantQuotaSize;
    long availableSize = actualQuotaSize - spaceDb.getSize();
    return new SpaceSummary().setTenantQuotaSize(tenantQuotaSize)
        .setQuotaSize(spaceDb.getQuotaSize())
        .setUsedSize(spaceDb.getSize())
        .setAvailableSize(availableSize)
        .setUsage(actualQuotaSize > 0 ? (double) spaceDb.getSize() / actualQuotaSize : null)
        .setSubDirectoryNum(spaceDb.getSubDirectoryNum())
        .setSubFileNum(spaceDb.getSubFileNum());
  }

  public static SpaceObject toUpdateSpaceObject(MultipartFile file, String fileName, Long fid,
      Long ofid, Space spaceDb, Long parentDirectoryId, int level, String parentLikeId,
      PlatformStoreType storeType) {
    SpaceObject spaceObject = new SpaceObject().setId(ofid)
        .setProjectId(nullSafe(spaceDb.getProjectId(), -1L))
        .setName(fileName)
        .setType(FileType.FILE)
        .setStoreType(storeType)
        .setFid(fid)
        .setLevel(level)
        .setSize(file.getSize())
        .setSpaceId(spaceDb.getId())
        .setParentDirectoryId(parentDirectoryId)
        .setParentLikeId(parentLikeId);
    // Fix:: Value is null when multi tenant control is turned off or /innerapi upload
    spaceObject.setTenantId(getOptTenantId());
    spaceObject.setCreatedBy(getUserId()).setCreatedDate(LocalDateTime.now())
        .setLastModifiedBy(getUserId()).setLastModifiedDate(LocalDateTime.now());
    return spaceObject;
  }

  public static SpaceObjectSummary toSpaceObjectSummary(SpaceObject objectDb) {
    return new SpaceObjectSummary()
        .setUsedSize(objectDb.getSize())
        .setSubDirectoryNum(objectDb.getSubDirectoryNum())
        .setSubFileNum(objectDb.getSubFileNum());
  }

  public static String formatShareDownloadUrl(String fileDownloadUrl, Long sid, String spt,
      String password) {
    StringBuilder params = new StringBuilder(fileDownloadUrl);
    if (nonNull(sid)) {
      int idx = fileDownloadUrl.indexOf("?");
      params.append(idx > 0 && idx != fileDownloadUrl.length() - 1 ? "&" : "")
          .append("sid=").append(sid);
    }
    if (isNotEmpty(spt)) {
      int idx = fileDownloadUrl.indexOf("?");
      params.append(idx > 0 && idx != fileDownloadUrl.length() - 1 ? "&" : "")
          .append("spt=").append(spt);
    }
    if (isNotEmpty(spt)) {
      int idx = fileDownloadUrl.indexOf("?");
      params.append(idx > 0 && idx != fileDownloadUrl.length() - 1 ? "&" : "")
          .append("password=").append(password);
    }
    return params.toString();
  }
}
