package cloud.xcan.angus.core.storage.application.cmd.bucket.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceExisted;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.storage.domain.StorageMessage.BUCKET_BIZ_DELETED_NOT_EMPTY;
import static cloud.xcan.angus.core.storage.domain.StorageMessage.BUCKET_BIZ_DELETED_NOT_EMPTY_CODE;
import static cloud.xcan.angus.core.storage.domain.StorageMessage.BUCKET_NON_CUS_BIZ_DELETED_ERROR;
import static cloud.xcan.angus.core.storage.domain.StorageMessage.BUCKET_NON_CUS_BIZ_DELETED_ERROR_CODE;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.storage.application.cmd.bucket.BucketBizConfigCmd;
import cloud.xcan.angus.core.storage.application.query.bucket.BucketQuery;
import cloud.xcan.angus.core.storage.domain.bucket.Bucket;
import cloud.xcan.angus.core.storage.domain.bucket.config.BucketBizConfig;
import cloud.xcan.angus.core.storage.domain.bucket.config.BucketBizConfigRepo;
import cloud.xcan.angus.core.storage.domain.file.ObjectFileRepo;
import cloud.xcan.angus.core.storage.infra.store.impl.ObjectClientFactory;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;

@Biz
public class BucketBizConfigCmdImpl extends CommCmd<BucketBizConfig, Long>
    implements BucketBizConfigCmd {

  @Resource
  private BucketQuery bucketQuery;

  @Resource
  private ObjectFileRepo objectFileRepo;

  @Resource
  public BucketBizConfigRepo bucketBizConfigRepo;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void config(BucketBizConfig bizConfig) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // Only allow system administrators to maintain buckets
        bucketQuery.checkOperateBucketPermission();
        // Check the BucketBizConfig existed
        BucketBizConfig grantDb = bucketBizConfigRepo.findByBizKey(bizConfig.getBizKey());
        assertResourceExisted(grantDb, bizConfig.getBizKey(), "BucketBizConfig");
      }

      @Override
      protected Void process() {
        insert0(bizConfig);
        return null;
      }
    }.execute();
  }

  @Override
  public void configDelete(String bizKey) {
    new BizTemplate<Void>() {
      Bucket bucketDb;
      BucketBizConfig bizConfigDb;

      @Override
      protected void checkParams() {
        // Only allow system administrators to maintain buckets
        bucketQuery.checkOperateBucketPermission();
        // Check the business deletion is allowed and biz config existed
        bizConfigDb = bucketQuery.checkAndFindByBizKey(bizKey);
        // User defined business configuration is not allowed to delete
        assertTrue(bizConfigDb.getAllowTenantCreated(),
            BUCKET_NON_CUS_BIZ_DELETED_ERROR_CODE, BUCKET_NON_CUS_BIZ_DELETED_ERROR);
        // Check the bucket existed
        bucketDb = bucketQuery.checkAndFind(bizConfigDb.getBucketName());
        // If there is an object under the bucket, the bucket cannot be deleted
        assertTrue(bucketQuery.isBucketBizEmpty(bucketDb.getName(), bizKey),
            BUCKET_BIZ_DELETED_NOT_EMPTY_CODE, BUCKET_BIZ_DELETED_NOT_EMPTY);
      }

      @Override
      protected Void process() {
        ObjectClientFactory.current().removeObject(bucketDb.getName(),
            ObjectClientFactory.current().getObjectNamePrefix(bucketDb.getName(), bizKey));
        bucketBizConfigRepo.deleteByBucketNameAndBizKey(bucketDb.getName(), bizKey);
        objectFileRepo.deleteByBucketNameAndBizKey(bucketDb.getName(), bizKey);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<BucketBizConfig, Long> getRepository() {
    return bucketBizConfigRepo;
  }
}
