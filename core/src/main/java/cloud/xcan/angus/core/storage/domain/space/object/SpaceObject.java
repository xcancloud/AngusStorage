package cloud.xcan.angus.core.storage.domain.space.object;

import static cloud.xcan.angus.spec.experimental.BizConstant.DEFAULT_ROOT_PID;

import cloud.xcan.angus.api.commonlink.FileProxyConstant;
import cloud.xcan.angus.api.enums.FileType;
import cloud.xcan.angus.api.enums.PlatformStoreType;
import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import cloud.xcan.angus.core.storage.domain.file.ObjectFile;
import cloud.xcan.angus.core.storage.infra.store.utils.SpaceObjectStats;
import com.google.common.collect.Sets;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Entity
@Table(name = "object_space_object")
@Accessors(chain = true)
public class SpaceObject extends TenantAuditingEntity<SpaceObject, Long> implements
    SpaceObjectStats<SpaceObject> {

  @Id
  private Long id;

  @Column(name = "project_id")
  private Long projectId;

  private String name;

  @Enumerated(EnumType.STRING)
  private FileType type;

  @Enumerated(EnumType.STRING)
  @Column(name = "store_type")
  private PlatformStoreType storeType;

  /**
   * File id corresponding to the Object
   */
  private Long fid;

  /**
   * Folder or file hierarchy: 1+5+1, Folder up to 5 levels.
   *
   * @see FileProxyConstant#MAX_FILE_DIR_LEVEL
   */
  private int level;

  private long size = 0L;

  @Column(name = "space_id")
  private Long spaceId;

  @Column(name = "parent_directory_id")
  private Long parentDirectoryId;

  /**
   * All parent directory ID symbols are connected by "-" (up to 10 levels are supported for 200
   * characters)
   */
  @Column(name = "parent_like_id")
  private String parentLikeId;

  @Transient
  private int subDirectoryNum = 0;
  @Transient
  private int subFileNum = 0;
  @Transient
  private String parentName;
  @Transient
  private ObjectFile file;
  @Transient
  private SpaceObjectSummary summary;
  @Transient
  private Collection<SpaceObject> parentChain;

  public SpaceObject() {
  }

  public SpaceObject(Long id, FileType type, Long parentDirectoryId, Long size) {
    this.id = id;
    this.type = type;
    this.parentDirectoryId = parentDirectoryId;
    this.size = size;
  }

  public boolean hasParent() {
    return Objects.nonNull(parentDirectoryId) && !parentDirectoryId.equals(DEFAULT_ROOT_PID);
  }

  public boolean hasSubObject() {
    return subDirectoryNum > 0 || subFileNum > 0;
  }

  public boolean isDirectory() {
    return FileType.DIRECTORY.equals(type);
  }

  public boolean isFile() {
    return FileType.FILE.equals(type) && Objects.nonNull(fid);
  }

  public Set<Long> getParentIds() {
    return hasParent() ? Stream.of(parentLikeId.split("-")).map(Long::parseLong)
        .collect(Collectors.toSet()) : Sets.newHashSet();
  }

  @Override
  public Long identity() {
    return this.id;
  }
}
