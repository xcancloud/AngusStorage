package cloud.xcan.angus.core.storage.application.converter;

import cloud.xcan.angus.core.storage.domain.bucket.config.BucketBizConfig;
import cloud.xcan.angus.core.storage.domain.space.Space;
import cloud.xcan.angus.idgen.UidGenerator;

public class SpaceConverter {

  public static Space toInitCustomizedByName(BucketBizConfig config,
      UidGenerator uidGenerator, String name) {
    return new Space().setId(uidGenerator.getUID())
        .setName(name)
        .setBizKey(config.getBizKey())
        .setBucketName(config.getBucketName())
        .setQuotaSize(null)
        .setAuth(true)
        .setCustomized(true);
  }

  public static Space toInitNonCustomizedByBizKey(BucketBizConfig config,
      UidGenerator uidGenerator) {
    return new Space().setId(uidGenerator.getUID())
        .setName(config.getBizKey())
        .setBizKey(config.getBizKey())
        .setBucketName(config.getBucketName())
        .setQuotaSize(null)
        .setAuth(true)
        .setCustomized(false);
  }

}
