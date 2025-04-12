package cloud.xcan.angus.core.storage.infra.store.operation;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import java.util.List;


/**
 * @see AmazonS3
 */
public interface BucketOperation {

  boolean isBucketExisted(String bucketName);

  void createBucket(CreateBucketRequest createBucketRequest);

  List<Bucket> getAllBuckets();

  Bucket getBucket(String bucketName);

  void removeBucket(String bucketName);

}
