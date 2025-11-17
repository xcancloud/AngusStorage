package cloud.xcan.angus.core.storage.domain.space.share;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;

@Setter
@Getter
@Entity
@Table(name = "object_space_share")
@Accessors(chain = true)
public class SpaceShare extends TenantAuditingEntity<SpaceShare, Long> implements Serializable {

  @Id
  private Long id;

  @Column(name = "space_id")
  private Long spaceId;

  @Column(name = "share_type")
  @Enumerated(EnumType.STRING)
  private SpaceShareType shareType;

  /**
   * Whether to share the whole space flag.
   * <p>
   * When objectIds is null, it will be set to true.
   */
  @Column(name = "`all`")
  private Boolean all;

  @Type(JsonType.class)
  @Column(columnDefinition = "json", name = "object_ids")
  private Set<Long> objectIds;

  @Column(name = "quick_object_id")
  private Long quickObjectId;

  @Column(name = "url")
  private String url;

  private Boolean expired;

  @Column(name = "expired_duration")
  private String expiredDuration;

  @Column(name = "expired_date")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime expiredDate;

  private Boolean public0;

  @Column(name = "public_token")
  private String publicToken;

  private String password;

  private String remark;

  @Transient
  private String createdByName;
  @Transient
  private String avatar;

  public Set<Long> getWideObjectIds() {
    Set<Long> wideObjectIds = new HashSet<>();
    if (isNotEmpty(objectIds)) {
      wideObjectIds.addAll(objectIds);
    }
    if (nonNull(quickObjectId)) {
      wideObjectIds.add(quickObjectId);
    }
    return wideObjectIds;
  }

  @Override
  public Long identity() {
    return this.id;
  }
}
