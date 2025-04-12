package cloud.xcan.angus.core.storage.domain.space.auth;


import cloud.xcan.angus.api.enums.AuthObjectType;
import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "object_space_auth")
@EntityListeners({AuditingEntityListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class SpaceAuth extends TenantEntity<SpaceAuth, Long> {

  @Id
  private Long id;

  @Column(name = "space_id")
  private Long spaceId;

  @Column(name = "auth_object_type")
  @Enumerated(EnumType.STRING)
  private AuthObjectType authObjectType;

  @Column(name = "auth_object_id")
  private Long authObjectId;

  @Column(name = "auth_data")
  @Type(JsonType.class)
  private List<SpacePermission> auths;

  private Boolean creator;

  @Column(name = "created_by")
  @CreatedBy
  private Long createdBy;

  @Column(name = "created_date")
  @CreatedDate
  private Date createdDate;

  @Override
  public Long identity() {
    return this.id;
  }
}
