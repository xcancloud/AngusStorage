package cloud.xcan.angus.core.storage.application.query.space;

import cloud.xcan.angus.api.commonlink.space.StorageResourcesCount;
import cloud.xcan.angus.api.commonlink.space.StorageResourcesCreationCount;
import cloud.xcan.angus.api.enums.AuthObjectType;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.storage.domain.space.Space;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface SpaceQuery {

  Space detail(Long id);

  Page<Space> find(GenericSpecification<Space> spec, PageRequest pageable);

  StorageResourcesCount countStatistics(Long projectId, AuthObjectType creatorObjectType,
      Long creatorObjectId, LocalDateTime createdDateStart, LocalDateTime createdDateEnd);

  StorageResourcesCreationCount resourcesCreationStatistics(Long projectId,
      AuthObjectType creatorObjectType, Long creatorObjectId, LocalDateTime createdDateStart,
      LocalDateTime createdDateEnd);

  Space checkAndFind(Long id);

  List<Space> checkAndFind(Collection<Long> reqIds);

  void check(Long id);

  void checkTenantSizeQuota(Space targetSpaceDb);

  void checkSpaceSizeQuota(Space targetSpaceDb);

  void checkSpaceNumQuota(long incr);

  void checkSpaceEmpty(Set<Long> spaceIds);

  Space findNotEmptyOneOf(Collection<Long> spaceIds);

  Boolean isAuthCtrl(Long id);

  void checkAddNameExists(String name);

  void checkUpdateNameExists(Long spaceId, String name);

  void setObjectStats(List<Space> spaces);
}
