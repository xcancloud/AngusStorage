package cloud.xcan.angus.core.storage.domain.space;

import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.enums.PlatformStoreType;
import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import cloud.xcan.angus.core.storage.domain.bucket.config.BucketBizConfig;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Entity
@Table(name = "object_space")
@Accessors(chain = true)
public class Space extends TenantAuditingEntity<Space, Long> implements Serializable {

  @Id
  private Long id;

  @Column(name = "project_id")
  private Long projectId;

  private String name;

  @Column(name = "biz_key")
  private String bizKey;

  @Column(name = "bucket_name")
  private String bucketName;

  /**
   * No limit when empty or less than 0, Maximum limited by tenant quota
   */
  @Column(name = "quota_size")
  private String quotaSize;

  private Boolean auth;

  private Boolean customized;

  private String remark;

  @Transient
  private long size = 0;
  @Transient
  private int subDirectoryNum = 0;
  @Transient
  private int subFileNum = 0;
  @Transient
  private String appCode;
  @Transient
  private SpaceSummary summary;
  @Transient
  private BucketBizConfig config;
  @Transient
  private PlatformStoreType storeType;

  public boolean hasQuotaLimit() {
    return nonNull(quotaSize);
  }

  public boolean isEnabledAuth() {
    return nonNull(auth) && auth;
  }

  @Override
  public Long identity() {
    return this.id;
  }
}
