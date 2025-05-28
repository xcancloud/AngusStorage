package cloud.xcan.angus.core.storage.application.cmd.bucket.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceExisted;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.storage.domain.StorageMessage.BUCKET_DELETED_NOT_EMPTY;
import static cloud.xcan.angus.core.storage.domain.StorageMessage.BUCKET_DELETED_NOT_EMPTY_CODE;
import static cloud.xcan.angus.core.storage.domain.StorageMessage.BUCKET_NAME_FORMAT_ERROR;
import static cloud.xcan.angus.core.storage.domain.StorageMessage.BUCKET_NON_CUS_DELETED_ERROR;
import static cloud.xcan.angus.core.storage.domain.StorageMessage.BUCKET_NON_CUS_DELETED_ERROR_CODE;
import static cloud.xcan.angus.core.storage.infra.store.impl.S3ObjectClient.toCannedAccessControlList;
import static com.amazonaws.services.s3.internal.BucketNameUtils.isValidV2BucketName;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.storage.application.cmd.bucket.BucketCmd;
import cloud.xcan.angus.core.storage.application.cmd.space.SpaceCmd;
import cloud.xcan.angus.core.storage.application.query.bucket.BucketQuery;
import cloud.xcan.angus.core.storage.domain.bucket.Bucket;
import cloud.xcan.angus.core.storage.domain.bucket.BucketRepo;
import cloud.xcan.angus.core.storage.domain.space.SpaceRepo;
import cloud.xcan.angus.core.storage.infra.store.impl.ObjectClientFactory;
import cloud.xcan.angus.spec.experimental.IdKey;
import cloud.xcan.angus.spec.utils.ObjectUtils;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import jakarta.annotation.Resource;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;


@Biz
@Slf4j
public class BucketCmdImpl extends CommCmd<Bucket, Long> implements BucketCmd {

  @Resource
  private BucketRepo bucketRepo;

  @Resource
  private BucketQuery bucketQuery;

  @Resource
  private SpaceRepo spaceRepo;

  @Resource
  private SpaceCmd spaceCmd;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public IdKey<Long, Object> add(Bucket bucket) {
    return new BizTemplate<IdKey<Long, Object>>() {
      @Override
      protected void checkParams() {
        // Only allow system administrators to maintain buckets
        bucketQuery.checkOperateBucketPermission();
        // Check the bucket name is legal
        assertTrue(isValidV2BucketName(bucket.getName()), BUCKET_NAME_FORMAT_ERROR);
        // Check the bucket name is globally unique
        assertResourceExisted(bucketQuery.find0(bucket.getName()), bucket.getName(), "Bucket");
      }

      @Override
      protected IdKey<Long, Object> process() {
        // Save the original information of the bucket to the database
        IdKey<Long, Object> idKey = insert(bucket);

        // Create bucket
        CreateBucketRequest bucketRequest = new CreateBucketRequest(bucket.getName())
            .withCannedAcl(toCannedAccessControlList(bucket.getAcl()));
        ObjectClientFactory.current().createBucket(bucketRequest);
        return idKey;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(String name) {
    new BizTemplate<Void>() {
      Bucket bucketDb;

      @Override
      protected void checkParams() {
        // Only allow system administrators to maintain buckets
        bucketQuery.checkOperateBucketPermission();
        // Check if the bucket existed
        bucketDb = bucketQuery.checkAndFind(name);
        // If there is an object under the bucket, the bucket cannot be deleted
        assertTrue(bucketDb.getTenantCreated(), BUCKET_NON_CUS_DELETED_ERROR_CODE,
            BUCKET_NON_CUS_DELETED_ERROR);
        // If there is an object under the bucket, the bucket cannot be deleted
        assertTrue(bucketQuery.isBucketEmpty(name), BUCKET_DELETED_NOT_EMPTY_CODE,
            BUCKET_DELETED_NOT_EMPTY);
      }

      @Override
      protected Void process() {
        ObjectClientFactory.current().removeBucket(bucketDb.getName());
        bucketRepo.delete(bucketDb);
        Set<Long> bucketSpaceIds = spaceRepo.findIdByBucketName(name);
        if (ObjectUtils.isNotEmpty(bucketSpaceIds)) {
          spaceCmd.delete(bucketSpaceIds, false);
        }
        // Retain:: bucketBizConfigRepo.deleteByBucketName(name);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<Bucket, Long> getRepository() {
    return this.bucketRepo;
  }
}
