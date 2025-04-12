package cloud.xcan.angus.core.storage.infra.store.operation;

import cloud.xcan.angus.core.storage.infra.store.model.AccessControl;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * @see AmazonS3
 */
public interface ObjectOperation {

  /**
   * @param prefix An optional parameter restricting the response to keys beginning with the
   *               specified prefix. Use prefixes to separate a bucket into different sets of keys,
   *               similar to how a file system organizes files into directories.
   */
  List<S3ObjectSummary> getAllObjectsByPrefix(String bucketName, String prefix);

  String getObjectUrl(String bucketName, String objectName, Duration expires);

  String getObjectUrl(String bucketName, String objectName);

  String getObjectUrl(String bucketName, String objectName, Duration expires,
      HttpMethod method);

  S3Object getObject(String bucketName, String objectName);

  S3Object getObject(GetObjectRequest getObjectRequest) throws IOException;

  PutObjectResult putObject(String bucketName, String objectName, InputStream stream,
      AccessControl objectAcl) throws IOException;

  PutObjectResult putObject(String bucketName, String objectName, InputStream stream,
      long size, String contextType, AccessControl objectAcl) throws IOException;

  void removeObject(String bucketName, String objectName);

  void removeObjects(String bucketName, String prefix);

  void removeObjects(String bucketName, Set<String> objectNames);

  boolean doesObjectExist(String bucketName, String objectName);

  boolean renameObject(String bucketName, String objectName, String targetObjectName);

  boolean copyObject(String bucketName, String objectName, String targetObjectName);

  String getObjectNamePrefix(String bucketName, String bizKey);

  String getObjectNamePrefix(String bucketName, String bizKey, Long tenantId, Long spaceId);

  String getObjectName(String prefix);

  String getObjectName(String prefix, String fileName);

  String getObjectName(String bucketName, String bizKey, Long tenantId, Long spaceId,
      String fileName);

  String getObjectPath(String objectName);

  String getBucketPath(String bucketName);
}
