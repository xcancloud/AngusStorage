package cloud.xcan.angus.core.storage.application.query.bucket.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.checkOpSysAdmin;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.checkSysAdmin;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.spring.boot.ApplicationInfo;
import cloud.xcan.angus.core.storage.application.query.bucket.BucketQuery;
import cloud.xcan.angus.core.storage.domain.bucket.Bucket;
import cloud.xcan.angus.core.storage.domain.bucket.BucketRepo;
import cloud.xcan.angus.core.storage.domain.bucket.config.BucketBizConfig;
import cloud.xcan.angus.core.storage.domain.bucket.config.BucketBizConfigRepo;
import cloud.xcan.angus.core.storage.domain.file.ObjectFileRepo;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


@Biz
@Slf4j
public class BucketQueryImpl implements BucketQuery {

  @Resource
  private BucketRepo bucketRepo;

  @Resource
  private BucketBizConfigRepo bucketBizConfigRepo;

  @Resource
  private ObjectFileRepo objectFileRepo;

  @Resource
  private ApplicationInfo applicationInfo;

  @Override
  public Bucket detail(String name) {
    return new BizTemplate<Bucket>() {
      @Override
      protected Bucket process() {
        Bucket bucket = find0(name);
        if (Objects.isNull(bucket)) {
          return null;
        }
        bucket.setConfigs(bucketBizConfigRepo.findByBucketName(name));
        return bucket;
      }
    }.execute();
  }

  @Override
  public Page<Bucket> list(Specification<Bucket> spec, Pageable pageable) {
    return new BizTemplate<Page<Bucket>>() {
      @Override
      protected Page<Bucket> process() {
        Page<Bucket> buckets = bucketRepo.findAll(spec, pageable);
        if (buckets.isEmpty()) {
          return buckets;
        }
        Map<String, List<BucketBizConfig>> assignMap = bucketBizConfigRepo
            .findByBucketNameIn(buckets.stream().map(Bucket::getName).collect(Collectors.toSet()))
            .stream().collect(Collectors.groupingBy(BucketBizConfig::getBucketName));
        buckets.getContent().forEach(x -> {
          x.setConfigs(assignMap.get(x.getName()));
        });
        return buckets;
      }
    }.execute();
  }

  @Override
  public Bucket checkAndFind(String name) {
    return bucketRepo.findByName(name)
        .orElseThrow(() -> ResourceNotFound.of(name, "Bucket"));
  }

  @Override
  public Bucket find0(String name) {
    return bucketRepo.findByName(name).orElse(null);
  }

  @Override
  public boolean isBucketEmpty(String bucketName) {
    return !objectFileRepo.findByBucketNameLimit1(bucketName);
  }

  @Override
  public boolean isBucketBizEmpty(String bucketName, String bizKey) {
    return !objectFileRepo.findByBucketNameLimit1(bucketName, bizKey);
  }

  @Override
  public BucketBizConfig checkAndFindByBizKey(String bizKey) {
    BucketBizConfig config = bucketBizConfigRepo.findByBizKey(bizKey);
    assertResourceNotFound(config, bizKey, "BucketBizConfig");
    return config;
  }

  @Override
  public void checkOperateBucketPermission() {
    // Only allow system administrators to operate
    if (applicationInfo.isCloudServiceEdition()) {
      checkOpSysAdmin();
    } else {
      checkSysAdmin();
    }
  }
}
