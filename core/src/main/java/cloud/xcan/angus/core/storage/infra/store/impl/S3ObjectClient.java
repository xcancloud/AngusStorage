package cloud.xcan.angus.core.storage.infra.store.impl;

import static cloud.xcan.angus.core.storage.domain.StorageMessage.BUCKET_CREATE_FAIL_CODE;
import static cloud.xcan.angus.core.storage.domain.StorageMessage.BUCKET_CREATE_FAIL_T;
import static cloud.xcan.angus.spec.http.ContentType.TYPE_OCTET_STREAM;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.enums.PlatformStoreType;
import cloud.xcan.angus.core.storage.infra.store.ObjectClient;
import cloud.xcan.angus.core.storage.infra.store.ObjectProperties;
import cloud.xcan.angus.core.storage.infra.store.model.AccessControl;
import cloud.xcan.angus.remote.message.SysException;
import cloud.xcan.angus.spec.annotations.DoInFuture;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.HttpMethod;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("s3ObjectClient")
public class S3ObjectClient extends ObjectClient {

  private AmazonS3 amazonS3;
  private final ObjectProperties objectProperties;

  public S3ObjectClient(ObjectProperties objectProperties) {
    this.objectProperties = objectProperties;
    try {
      this.amazonS3 = buildAmazonS3Client(objectProperties, true);
    } catch (Exception e) {
      log.error("Configuration S3 storage parameter error, cause: {}", e.getMessage());
    }
  }

  @Override
  public void init(List<cloud.xcan.angus.core.storage.domain.bucket.Bucket> buckets)
      throws Exception {
    for (cloud.xcan.angus.core.storage.domain.bucket.Bucket bucket : buckets) {
      if (!isBucketExisted(bucket.getName())) {
        createBucket(new CreateBucketRequest(bucket.getName())
            .withCannedAcl(toCannedAccessControlList(bucket.getAcl())));
        log.info("Create storage bucket: {}", bucket.getName());
      }
    }
  }

  @Override
  public PlatformStoreType getStoreType() {
    return PlatformStoreType.AWS_S3;
  }

  @Override
  public ObjectProperties getProperties() {
    return this.objectProperties;
  }

  @Override
  public boolean isBucketExisted(String bucketName) {
    return this.amazonS3.doesBucketExistV2(bucketName);
  }

  @Override
  public void createBucket(CreateBucketRequest createBucketRequest) {
    try {
      if (!amazonS3.doesBucketExistV2(createBucketRequest.getBucketName())) {
        amazonS3.createBucket((createBucketRequest));
      }
    } catch (Exception e) {
      log.error("Create s3 bucket exception: ", e);
      throw SysException.of(BUCKET_CREATE_FAIL_CODE, BUCKET_CREATE_FAIL_T,
          new Object[]{createBucketRequest.getBucketName()});
    }
  }

  @Override
  public List<Bucket> getAllBuckets() {
    return amazonS3.listBuckets();
  }

  @Override
  public Bucket getBucket(String bucketName) {
    return amazonS3.listBuckets().stream().filter(b -> b.getName().equals(bucketName)).findFirst()
        .orElse(null);
  }

  @Override
  public void removeBucket(String bucketName) {
    amazonS3.deleteBucket(bucketName);
  }

  @Override
  public List<S3ObjectSummary> getAllObjectsByPrefix(String bucketName, String prefix) {
    ObjectListing objectListing = amazonS3.listObjects(bucketName, prefix);
    return new ArrayList<>(objectListing.getObjectSummaries());
  }

  @Override
  public String getObjectUrl(String bucketName, String objectName, Duration expires) {
    return getObjectUrl(bucketName, objectName, expires, HttpMethod.GET);
  }

  @Override
  public String getObjectUrl(String bucketName, String objectName, Duration expires,
      HttpMethod method) {
    // Set the pre-signed URL to expire after `expires`.
    Date expiration = Date.from(Instant.now().plus(expires));

    // Generate the pre-signed URL.
    URL url = amazonS3.generatePresignedUrl(
        new GeneratePresignedUrlRequest(bucketName, objectName).withMethod(method)
            .withExpiration(expiration));
    return url.toString();
  }

  @Override
  public String getObjectUrl(String bucketName, String objectName) {
    URL url = amazonS3.getUrl(bucketName, objectName);
    return url.toString();
  }

  @Override
  public S3Object getObject(String bucketName, String objectName) {
    return amazonS3.getObject(bucketName, objectName);
  }

  @Override
  public S3Object getObject(GetObjectRequest getObjectRequest) {
    return amazonS3.getObject(getObjectRequest);
  }

  @Override
  public boolean doesObjectExist(String bucketName, String objectName) {
    return amazonS3.doesObjectExist(bucketName, objectName);
  }

  @Override
  public boolean renameObject(String bucketName, String objectName, String targetObjectName) {
    return renameFolder(bucketName, objectName, targetObjectName);
  }

  @DoInFuture("Support copy folder")
  @Override
  public boolean copyObject(String bucketName, String objectName, String targetObjectName) {
    // If name doesnot ends with suffix (/) means its a file
    CopyObjectRequest copyObjRequest = new CopyObjectRequest(bucketName,
        objectName, bucketName, targetObjectName);
    try {
      amazonS3.copyObject(copyObjRequest);
      return true;
    } catch (Exception e) {
      log.error("Copy file exception", e);
    }
    return false;
  }

  @Override
  public PutObjectResult putObject(String bucketName, String objectName, InputStream stream,
      AccessControl objectAcl) throws IOException {
    return putObject(bucketName, objectName, stream, stream.available(), TYPE_OCTET_STREAM,
        objectAcl);
  }

  @Override
  public PutObjectResult putObject(String bucketName, String objectName, InputStream stream,
      long size, String contextType, AccessControl objectAcl) {
    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentLength(size);
    objectMetadata.setContentType(contextType);
    PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, stream,
        objectMetadata);
    putObjectRequest.withCannedAcl(toCannedAccessControlList(objectAcl));
    // StorageSetting the read limit value to one byte greater than the size of stream will
    // reliably avoid a ResetException
    putObjectRequest.getRequestClientOptions().setReadLimit(Long.valueOf(size).intValue() + 1);
    return amazonS3.putObject(putObjectRequest);
  }

  public static CannedAccessControlList toCannedAccessControlList(AccessControl objectAcl) {
    if (AccessControl.Private.sameValueAs(objectAcl)) {
      return CannedAccessControlList.Private;
    }
    if (AccessControl.PublicRead.sameValueAs(objectAcl)) {
      return CannedAccessControlList.PublicRead;
    }
    if (AccessControl.PublicReadWrite.sameValueAs(objectAcl)) {
      return CannedAccessControlList.PublicReadWrite;
    }
    return CannedAccessControlList.Private;
  }

  @Override
  public void removeObject(String bucketName, String objectName) {
    amazonS3.deleteObject(bucketName, objectName);
  }

  @Override
  public void removeObjects(String bucketName, String prefix) {
    amazonS3.deleteObject(bucketName, prefix);
  }

  /**
   * Batch delete files
   */
  @Override
  public void removeObjects(String bucketName, Set<String> objectNames) {
    DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName);
    List<KeyVersion> keys = objectNames.stream().map(KeyVersion::new)
        .collect(Collectors.toList());
    deleteObjectsRequest.withKeys(keys);
    amazonS3.deleteObjects(deleteObjectsRequest);
  }

  public boolean renameFolder(String bucketName, String keyName, String newName) {
    boolean result = false;
    try {
      List<S3ObjectSummary> files = amazonS3.listObjects(bucketName, keyName).getObjectSummaries();

      // Some meta data to create empty folders
      ObjectMetadata metadata = new ObjectMetadata();
      metadata.setContentLength(0);
      InputStream emptyContent = new ByteArrayInputStream(new byte[0]);

      // Final location is the locaiton where the child folder contents of the existing folder should go
      String finalLocation = keyName.substring(0, keyName.lastIndexOf('/') + 1) + newName;
      for (S3ObjectSummary file : files) {
        String key = file.getKey();
        // Updating child folder location with the newlocation
        String destinationKeyName = key.replace(keyName, finalLocation);
        if (key.charAt(key.length() - 1) == '/') {
          // If name ends with suffix (/) means its a folders
          PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, destinationKeyName,
              emptyContent, metadata);
          amazonS3.putObject(putObjectRequest);
        } else {
          // If name doesnot ends with suffix (/) means its a file
          CopyObjectRequest copyObjRequest = new CopyObjectRequest(bucketName,
              file.getKey(), bucketName, destinationKeyName);
          amazonS3.copyObject(copyObjRequest);
        }
      }
      return deleteFolder(bucketName, keyName);
    } catch (Exception e) {
      log.error("Rename folder exception:", e);
    }
    return result;
  }

  public boolean deleteFolder(String bucketName, String keyName) {
    boolean result = false;
    try {
      // Deleting folder children
      List<S3ObjectSummary> fileList = amazonS3.listObjects(bucketName, keyName)
          .getObjectSummaries();
      for (S3ObjectSummary file : fileList) {
        amazonS3.deleteObject(bucketName, file.getKey());
      }
      // Deleting actual passed folder
      amazonS3.deleteObject(bucketName, keyName);
      result = true;
    } catch (Exception e) {
      log.error("Delete folder exception:", e);
    }
    return result;
  }

  public AmazonS3 buildAmazonS3Client(ObjectProperties objectProperties, boolean force) {
    if (!force && nonNull(amazonS3)) {
      return amazonS3;
    }
    ClientConfiguration configuration = new ClientConfiguration();
    configuration.setMaxErrorRetry(1);
    configuration.setConnectionTimeout(6 * 1000);
    configuration.setSocketTimeout(2 * 60 * 60 * 1000);
    configuration.setProtocol(Protocol.HTTP);
    AwsClientBuilder.EndpointConfiguration endpointConfig =
        new AwsClientBuilder.EndpointConfiguration(objectProperties.getEndpoint(),
            objectProperties.getRegion());
    AWSCredentials credentials = new BasicAWSCredentials(objectProperties.getAccessKey(),
        objectProperties.getSecretKey());
    AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(
        credentials);
    amazonS3 = AmazonS3Client.builder().withEndpointConfiguration(endpointConfig)
        .withClientConfiguration(configuration).withCredentials(credentialsProvider)
        .disableChunkedEncoding().withPathStyleAccessEnabled(false)
        .build();
    return amazonS3;
  }

  @Override
  public String getObjectNamePrefix(String bucketName, String bizKey) {
    return bizKey;
  }

  @Override
  public String getObjectNamePrefix(String bucketName, String bizKey, Long tenantId, Long spaceId) {
    return bizKey + "/" + tenantId + "/" + spaceId;
  }

  @Override
  public String getObjectName(String prefix) {
    return prefix;
  }

  @Override
  public String getObjectName(String prefix, String fileName) {
    return prefix + "/" + fileName;
  }

  @Override
  public String getObjectName(String bucketName, String bizKey, Long tenantId, Long spaceId,
      String fileName) {
    return getObjectNamePrefix(bucketName, bizKey, tenantId, spaceId) + "/" + fileName;
  }

  @Override
  public String getObjectPath(String objectName) {
    return objectName;
  }

  @Override
  public String getBucketPath(String bucketName) {
    return bucketName;
  }
}
