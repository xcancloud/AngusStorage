package cloud.xcan.angus.core.storage.domain.bucket;

import cloud.xcan.angus.core.jpa.auditor.AuditingEntity;
import cloud.xcan.angus.core.storage.domain.bucket.config.BucketBizConfig;
import cloud.xcan.angus.core.storage.infra.store.model.AccessControl;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Setter
@Getter
@Accessors(chain = true)
@Table(name = "bucket")
@EntityListeners({AuditingEntityListener.class})
public class Bucket extends AuditingEntity<Bucket, Long> implements Serializable {

  @Id
  private Long id;

  private String name;

  /**
   * Create S3 private bucket by default.
   */
  @Enumerated(EnumType.STRING)
  private AccessControl acl;

  @Column(name = "tenant_created")
  private Boolean tenantCreated;

  @Transient
  private List<BucketBizConfig> configs;

  @Override
  public Long identity() {
    return this.id;
  }
}
