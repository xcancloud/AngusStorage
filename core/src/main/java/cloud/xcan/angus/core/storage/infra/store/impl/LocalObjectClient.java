package cloud.xcan.angus.core.storage.infra.store.impl;

import static cloud.xcan.angus.core.storage.domain.StorageMessage.BUCKET_CREATE_FAIL_CODE;
import static cloud.xcan.angus.core.storage.domain.StorageMessage.BUCKET_CREATE_FAIL_T;
import static cloud.xcan.angus.core.storage.domain.StorageMessage.OBJECT_LOCAL_DOWNLOAD_ERROR_CODE;
import static cloud.xcan.angus.core.storage.domain.StorageMessage.OBJECT_LOCAL_DOWNLOAD_ERROR_T;
import static cloud.xcan.angus.spec.experimental.BizConstant.DEFAULT_ROOT_PID;
import static cloud.xcan.angus.spec.http.ContentType.TYPE_OCTET_STREAM;

import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.enums.FileType;
import cloud.xcan.angus.api.enums.PlatformStoreType;
import cloud.xcan.angus.api.manager.UserManager;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObject;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObjectRepo;
import cloud.xcan.angus.core.storage.infra.store.ObjectClient;
import cloud.xcan.angus.core.storage.infra.store.ObjectProperties;
import cloud.xcan.angus.core.storage.infra.store.model.AccessControl;
import cloud.xcan.angus.remote.message.SysException;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.spec.annotations.DoInFuture;
import cloud.xcan.angus.spec.utils.DateUtils;
import cloud.xcan.angus.spec.utils.FileUtils;
import cloud.xcan.angus.spec.utils.ObjectUtils;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.Owner;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.activation.MimetypesFileTypeMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Local object storage client.
 * <p>
 * Object URL expiration is not supported.
 */
@Slf4j
@Component("localObjectClient")
public class LocalObjectClient extends ObjectClient {

  private final SpaceObjectRepo spaceObjectRepo;

  private final ObjectProperties objectProperties;

  private final UserManager userManager;

  public LocalObjectClient(SpaceObjectRepo spaceObjectRepo,
      ObjectProperties objectProperties, UserManager userManager) {
    this.spaceObjectRepo = spaceObjectRepo;
    this.objectProperties = objectProperties;
    this.userManager = userManager;
  }

  /**
   * initialization and directory
   */
  @Override
  public void init(List<cloud.xcan.angus.core.storage.domain.bucket.Bucket> buckets)
      throws Exception {
    for (cloud.xcan.angus.core.storage.domain.bucket.Bucket bucket : buckets) {
      File dir = new File(objectProperties.getLocalDir()
          + File.separator + bucket.getName());
      if (!dir.exists()) {
        FileUtils.forceMkdir(dir);
        log.info("Create storage directory: {}", dir.getAbsolutePath());
      }
    }
  }

  @Override
  public PlatformStoreType getStoreType() {
    return PlatformStoreType.LOCAL;
  }

  @Override
  public ObjectProperties getProperties() {
    return this.objectProperties;
  }

  @Override
  public boolean isBucketExisted(String bucketName) {
    return new File(getBucketPath(bucketName)).exists();
  }

  @Override
  public void createBucket(CreateBucketRequest createBucketRequest) {
    String bucketName = createBucketRequest.getBucketName();
    try {
      if (isBucketExisted(bucketName)) {
        return;
      }
      File bucketDir = new File(getBucketPath(bucketName));
      if (!bucketDir.exists()) {
        bucketDir.mkdir();
      }
    } catch (Exception e) {
      log.error("Create S3 bucket exception: ", e);
      throw SysException
          .of(BUCKET_CREATE_FAIL_CODE, BUCKET_CREATE_FAIL_T, new Object[]{bucketName});
    }
  }

  @Override
  public List<Bucket> getAllBuckets() {
    File rootPath = new File(objectProperties.getLocalDir());
    if (!rootPath.exists()) {
      return null;
    }
    File[] buckets = rootPath.listFiles(File::isDirectory);
    if (ObjectUtils.isEmpty(buckets)) {
      return null;
    }

    List<SpaceObject> objectFiles = spaceObjectRepo.findByParentDirectoryIdAndType(DEFAULT_ROOT_PID,
        FileType.DIRECTORY.getValue());
    if (ObjectUtils.isEmpty(objectFiles)) {
      log.warn("The corresponding object metas of files not exists, file path: {}",
          Stream.of(buckets).map(File::getName).collect(Collectors.toList()));
      return Stream.of(buckets).map(dir -> {
        Bucket bucket = new Bucket();
        bucket.setName(dir.getName());
        bucket.setCreationDate(null);
        bucket.setOwner(null);
        return bucket;
      }).collect(Collectors.toList());
    }

    Map<Long, User> userInfoMap = userManager.getUserInfoMap(
        objectFiles.stream().map(SpaceObject::getCreatedBy).collect(Collectors.toSet()));
    // Important:: File names must be unique under the same parent
    Map<String, SpaceObject> objectMetaMap = objectFiles.stream()
        .collect(Collectors.toMap(SpaceObject::getName, x -> x));
    return List.of(buckets).stream().map(dir -> {
      SpaceObject objectFile = objectMetaMap.get(dir.getName());
      Bucket bucket = new Bucket();
      bucket.setName(dir.getName());
      bucket.setCreationDate(DateUtils.asDate(objectFile.getCreatedDate()));
      bucket.setOwner(new Owner(String.valueOf(objectFile.getCreatedBy()),
          userInfoMap.get(objectFile.getCreatedBy()).getFullName()));
      return bucket;
    }).collect(Collectors.toList());
  }

  @Override
  public Bucket getBucket(String bucketName) {
    File rootPath = new File(objectProperties.getLocalDir());
    if (!rootPath.exists()) {
      return null;
    }
    File[] dirs = rootPath.listFiles(File::isDirectory);
    if (ObjectUtils.isEmpty(dirs)) {
      return null;
    }
    List<File> buckets = Stream.of(dirs).filter(dir -> dir.getName().equals(bucketName))
        .collect(Collectors.toList());
    if (ObjectUtils.isEmpty(buckets)) {
      return null;
    }

    List<SpaceObject> objectFiles = spaceObjectRepo.findByParentDirectoryIdAndNameAndType(
        DEFAULT_ROOT_PID, bucketName, FileType.DIRECTORY.getValue());
    if (ObjectUtils.isEmpty(objectFiles)) {
      log.warn("The corresponding object meta of file not exists, file path: {}",
          Stream.of(dirs).map(File::getName).collect(Collectors.toList()));
      Bucket bucket = new Bucket();
      bucket.setName(bucketName);
      bucket.setCreationDate(null);
      bucket.setOwner(null);
      return bucket;
    }

    Map<Long, User> userInfoMap = userManager.getUserInfoMap(
        objectFiles.stream().map(SpaceObject::getCreatedBy).collect(Collectors.toSet()));
    Bucket bucket = new Bucket();
    bucket.setName(bucketName);
    bucket.setCreationDate(DateUtils.asDate(objectFiles.get(0).getCreatedDate()));
    bucket.setOwner(new Owner(String.valueOf(objectFiles.get(0).getCreatedBy()),
        userInfoMap.get(objectFiles.get(0).getCreatedBy()).getFullName()));
    return bucket;
  }

  @Override
  public void removeBucket(String bucketName) {
    File bucketDir = new File(getBucketPath(bucketName));
    if (!bucketDir.isDirectory()) {
      return;
    }
    bucketDir.delete();
  }

  @Override
  public List<S3ObjectSummary> getAllObjectsByPrefix(String bucketName, String prefix) {
    String bucketDir = getBucketPath(bucketName + (ObjectUtils.isNotEmpty(prefix) ? prefix : ""));
    File bucketFiles = new File(bucketDir);
    File[] files = bucketFiles.listFiles();
    if (ObjectUtils.isEmpty(files)) {
      return null;
    }

    List<SpaceObject> objectFiles = spaceObjectRepo.findByParentDirectoryIdAndNameAndType(
        DEFAULT_ROOT_PID, bucketName, FileType.DIRECTORY.getValue());
    if (ObjectUtils.isEmpty(objectFiles)) {
      log.warn("The corresponding object meta of file not exists, file path: {}",
          List.of(files).stream().map(File::getName).collect(Collectors.toList()));
      return List.of(files).stream().map(file -> {
        S3ObjectSummary summary = new S3ObjectSummary();
        summary.setBucketName(bucketName);
        summary.setKey(bucketDir + File.separator + file.getName());
        summary.setETag(null); // Noop!!! How to deal with large files?
        summary.setSize(file.getUsableSpace());
        summary.setLastModified(new Date(file.lastModified()));
        summary.setOwner(null);
        return summary;
      }).collect(Collectors.toList());
    }

    Map<Long, User> userInfoMap = userManager.getUserInfoMap(
        objectFiles.stream().map(SpaceObject::getCreatedBy).collect(Collectors.toSet()));
    // Important:: File names must be unique under the same parent
    Map<String, SpaceObject> objectMetaMap = objectFiles.stream()
        .collect(Collectors.toMap(SpaceObject::getName, x -> x));
    return Stream.of(files).map(file -> {
      SpaceObject objectFile = objectMetaMap.get(file.getName());
      S3ObjectSummary summary = new S3ObjectSummary();
      summary.setBucketName(bucketName);
      summary.setKey(bucketDir + File.separator + file.getName());
      summary.setETag(null); // Noop!!! How to deal with large files?
      summary.setSize(file.getUsableSpace());
      summary.setLastModified(new Date(file.lastModified()));
      summary.setOwner(new Owner(String.valueOf(objectFile.getCreatedBy()),
          userInfoMap.get(objectFile.getCreatedBy()).getFullName()));
      return summary;
    }).collect(Collectors.toList());
  }

  /**
   * @param objectName Equal to S3 object key.
   * @param expires    Local storage not supported
   */
  @Override
  public String getObjectUrl(String bucketName, String objectName, Duration expires) {
    return getObjectUrl(bucketName, objectName);
  }

  @Override
  public String getObjectUrl(String bucketName, String objectName) {
    if (notBucketObject(bucketName, objectName)) {
      return null;
    }
    return getObjectPath(objectName);
  }

  @Override
  public String getObjectUrl(String bucketName, String objectName, Duration expires,
      HttpMethod method) {
    return getObjectUrl(bucketName, objectName);
  }

  @Override
  public S3Object getObject(String bucketName, String objectName) {
    GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, objectName);
    try {
      return getObject(getObjectRequest);
    } catch (IOException e) {
      log.error("Download local object exception: ", e);
      throw SysException.of(OBJECT_LOCAL_DOWNLOAD_ERROR_CODE, OBJECT_LOCAL_DOWNLOAD_ERROR_T,
          new Object[]{objectName});
    }
  }

  @Override
  public S3Object getObject(GetObjectRequest getObjectRequest) throws IOException {
    // Get request parameters
    String bucketName = getObjectRequest.getBucketName();
    String objectName = getObjectRequest.getKey();

    // Check if the file exists
    if (!doesObjectExist(bucketName, objectName)) {
      throw ResourceNotFound.of("File " + objectName + " not found");
    }

    // Get object file
    File file = new File(getObjectPath(objectName));
    S3Object s3Object = new S3Object();
    s3Object.setObjectContent(new FileInputStream(file));
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setLastModified(new Date(file.lastModified()));
    metadata.setContentType(new MimetypesFileTypeMap().getContentType(file));
    s3Object.setObjectMetadata(metadata);
    s3Object.setKey(objectName);
    return s3Object;
  }

  @Override
  public PutObjectResult putObject(String bucketName, String objectName, InputStream stream,
      AccessControl objectAcl) throws IOException {
    return putObject(bucketName, objectName, stream, stream.available(), TYPE_OCTET_STREAM,
        objectAcl);
  }

  @Override
  public PutObjectResult putObject(String bucketName, String objectName, InputStream stream,
      long size, String contextType, AccessControl objectAcl) throws IOException {
    File file = new File(getObjectPath(objectName));
    if (!file.getParentFile().exists()) {
      FileUtils.forceMkdir(file.getParentFile());
    }
    writeInputStreamToFile(objectName, stream, file);
    return null;
  }

  @Override
  public void removeObject(String bucketName, String objectName) {
    if (notBucketObject(bucketName, objectName)) {
      return;
    }
    try {
      File file = new File(getObjectPath(objectName));
      if (file.exists()) {
        file.delete();
      }
    } catch (Exception e) {
      log.error("Delete object {} exception: {}", objectName, e.getMessage());
    }
  }

  @Override
  public void removeObjects(String bucketName, String prefix) {
    if (notBucketObject(bucketName, prefix)) {
      return;
    }
    try {
      File file = new File(getObjectPath(prefix));
      if (file.exists()) {
        file.delete();
      }
    } catch (Exception e) {
      log.error("Delete objects {} exception: {}", prefix, e.getMessage());
    }
  }

  @Override
  public void removeObjects(String bucketName, Set<String> objectNames) {
    if (ObjectUtils.isEmpty(bucketName)) {
      return;
    }
    for (String objectName : objectNames) {
      try {
        if (!objectName.startsWith(File.separator + bucketName)) {
          continue;
        }
        String filePath = getObjectPath(objectName);
        File file = new File(filePath);
        if (file.exists()) {
          file.delete();
        }
      } catch (Exception e) {
        log.error("Delete object {} exception: {}", objectName, e.getMessage());
      }
    }
  }

  @Override
  public boolean doesObjectExist(String bucketName, String objectName) {
    if (notBucketObject(bucketName, objectName)) {
      return false;
    }
    File file = new File(getObjectPath(objectName));
    return file.exists() && file.isFile();
  }

  @Override
  public boolean renameObject(String bucketName, String objectName, String targetObjectName) {
    if (notBucketObject(bucketName, objectName)) {
      return false;
    }
    File file = new File(getObjectPath(objectName));
    return file.renameTo(new File(getObjectPath(targetObjectName)));
  }

  @DoInFuture("Support copy folder")
  @Override
  public boolean copyObject(String bucketName, String objectName, String targetObjectName) {
    if (notBucketObject(bucketName, objectName)) {
      return false;
    }
    try {
      FileUtils.copyFile(new File(getObjectPath(objectName)),
          new File(getObjectPath(targetObjectName)), false);
      return true;
    } catch (IOException e) {
      log.error("Copy file exception", e);
    }
    return false;
  }

  @Override
  public String getObjectNamePrefix(String bucketName, String bizKey) {
    return bucketName + File.separator + bizKey;
  }

  @Override
  public String getObjectNamePrefix(String bucketName, String bizKey, Long tenantId, Long spaceId) {
    return getObjectNamePrefix(bucketName, bizKey) + File.separator + tenantId + File.separator
        + spaceId;
  }

  @Override
  public String getObjectName(String prefix) {
    return prefix;
  }

  @Override
  public String getObjectName(String prefix, String fileName) {
    return prefix + File.separator + fileName;
  }

  @Override
  public String getObjectName(String bucketName, String bizKey, Long tenantId, Long spaceId,
      String fileName) {
    return getObjectNamePrefix(bucketName, bizKey, tenantId, spaceId) + File.separator + fileName;
  }

  /**
   * objectPath = bucketName + dirName + fileName
   */
  @Override
  public String getObjectPath(String objectName) {
    return objectProperties.getLocalDir() + File.separator + objectName;
  }

  /**
   * bucketPath = storeLocalDirPath + bucketName
   */
  @Override
  public String getBucketPath(String bucketName) {
    return objectProperties.getLocalDir() + File.separator + bucketName;
  }

  private boolean notBucketObject(String bucketName, String objectName) {
    return ObjectUtils.isEmpty(bucketName) || ObjectUtils.isEmpty(objectName)
        || !objectName.startsWith(bucketName);
  }

  private void writeInputStreamToFile(String objectName, InputStream stream, File file)
      throws IOException {
    FileOutputStream os = null;
    try {
      if (file.exists()) {
        file.delete();
      } else {
        file.createNewFile();
      }
      os = new FileOutputStream(file);
      byte[] flush = new byte[4096];
      int len;
      while ((len = stream.read(flush)) != -1) {
        os.write(flush, 0, len);
      }
      os.flush();
    } catch (IOException e) {
      log.error("Write file {} to disk error: {}", file.getName(), e.getMessage());
      try {
        if (file.exists()) {
          file.delete();
        }
      } catch (Exception ex) {
        // NOOP
      }
      throw new IOException("Write file " + objectName + " to disk error: " + e.getMessage());
    } finally {
      if (!Objects.isNull(os)) {
        os.close();
      }
      if (!Objects.isNull(stream)) {
        stream.close();
      }
    }
  }
}
