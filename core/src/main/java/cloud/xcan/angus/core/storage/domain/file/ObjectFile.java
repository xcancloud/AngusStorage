package cloud.xcan.angus.core.storage.domain.file;

import static cloud.xcan.angus.spec.experimental.BizConstant.DEFAULT_ROOT_PID;

import cloud.xcan.angus.api.enums.PlatformStoreType;
import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import cloud.xcan.angus.core.storage.infra.store.model.UploadType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.http.MediaType;

@Entity
@Setter
@Getter
@Accessors(chain = true)@Table(name = "object_file")
@EntityListeners({AuditingEntityListener.class})
public class ObjectFile extends TenantAuditingEntity<ObjectFile, Long> implements Serializable {

  @Id
  private Long id;

  @Column(name = "project_id")
  private Long projectId;

  /**
   * Real file name when uploading
   */
  @Column(name = "name")
  private String name;

  /**
   * File name during actual storage, unique. Format：name + .id + .extension
   */
  @Column(name = "unique_name")
  private String uniqueName;

  /**
   * Object id corresponding to the file
   */
  private Long oid;

  /**
   * File or folder relative path = dir + name
   */
  private String path;

  /**
   * The actual upload size, which may be inconsistent with the physical disk storage size.
   */
  private long size;

  @Column(name = "content_type")
  private String contentType;

  /**
   * Local storage full path / URL address of S3 object.
   */
  @Column(name = "store_address")
  private String storeAddress;

  @Enumerated(EnumType.STRING)
  @Column(name = "store_type")
  private PlatformStoreType storeType;

  @Column(name = "space_id")
  private Long spaceId;

  @Column(name = "parent_directory_id")
  private Long parentDirectoryId;

  @Column(name = "instance_id")
  private String instanceId;

  @Column(name = "biz_key")
  private String bizKey;

  @Column(name = "bucket_name")
  private String bucketName;

  @Column(name = "upload_id")
  private String uploadId;

  @Enumerated(EnumType.STRING)
  @Column(name = "upload_type")
  private UploadType uploadType;

  private Boolean completed;

  @Column(name = "store_deleted")
  private Boolean storeDeleted;

  @Column(name = "deleted_retry_num")
  private Integer deletedRetryNum;

  @Column(name = "public_token")
  private String publicToken;

  @Transient
  private String downloadUrl;
  @Transient
  private int cacheAge;
  @Transient
  private MediaType mediaType;
  @Transient
  private String forwardUrl;

  public boolean isRoot() {
    return Objects.nonNull(parentDirectoryId) && parentDirectoryId.equals(DEFAULT_ROOT_PID);
  }

  public boolean isS3Platform() {
    return Objects.nonNull(storeType) && storeType.equals(PlatformStoreType.AWS_S3);
  }

  public boolean isLocalPlatform() {
    return Objects.nonNull(storeType) && storeType.equals(PlatformStoreType.LOCAL);
  }

  @Override
  public Long identity() {
    return this.id;
  }
}
