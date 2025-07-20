package cloud.xcan.angus.core.storage.domain.bucket.config;

import static java.util.Objects.nonNull;

import cloud.xcan.angus.spec.experimental.EntitySupport;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Setter
@Getter
@Accessors(chain = true)
@Table(name = "bucket_biz_config")
@EntityListeners({AuditingEntityListener.class})
public class BucketBizConfig extends EntitySupport<BucketBizConfig, Long> implements Serializable {

  @Id
  private Long id;

  @Column(name = "biz_key")
  private String bizKey;

  @Column(name = "bucket_name")
  private String bucketName;

  @Column(name = "remark")
  public String remark;

  /**
   * Whether to public file access: public access file (/pubapi), private access file (/api)
   */
  @Column(name = "public_access")
  private Boolean publicAccess;

  /**
   * Public token access api (private access files also need to use private token authentication)
   */
  @Column(name = "public_token_auth")
  private Boolean publicTokenAuth;

  /**
   * Whether to encrypt files for storage
   */
  private Boolean encrypt;

  /**
   * Whether to enable multi-tenancy control.
   * <p>
   * For downloading only, upload requires multi tenant control.
   */
  @Column(name = "multi_tenant_ctrl")
  private Boolean multiTenantCtrl;

  /**
   * Enable authentication(space) flag.
   */
  @Column(name = "enabled_auth")
  private Boolean enabledAuth;

  @Column(name = "app_code")
  private String appCode;

  @Column(name = "app_admin_code")
  private String appAdminCode;

  /**
   * Browser cache duration, Time unit: second
   */
  @Column(name = "cache_age")
  private int cacheAge;

  /**
   * Flag of tenant defined business.
   *
   * <pre>
   *  User defined business configuration can be deleted, and preset business configuration cannot be deleted.
   * </pre>
   */
  @Column(name = "allow_tenant_created")
  private Boolean allowTenantCreated;

  public boolean isMultiTenantCtrl() {
    return nonNull(multiTenantCtrl) && multiTenantCtrl;
  }

  @Override
  public Long identity() {
    return this.id;
  }
}
