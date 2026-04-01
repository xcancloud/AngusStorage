package cloud.xcan.angus.core.storage.application.query.space;

import cloud.xcan.angus.api.commonlink.space.StorageResourcesCount;
import cloud.xcan.angus.api.commonlink.space.StorageResourcesCreationCount;
import cloud.xcan.angus.core.storage.domain.space.Space;
import cloud.xcan.angus.persistence.jpa.criteria.GenericSpecification;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface SpaceQuery {

  Space detail(Long id);

  Page<Space> list(GenericSpecification<Space> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  StorageResourcesCount countStatistics(Long projectId, String creatorObjectType,
      Long creatorObjectId, LocalDateTime createdDateStart, LocalDateTime createdDateEnd);

  StorageResourcesCreationCount resourcesCreationStatistics(Long projectId,
      String creatorObjectType, Long creatorObjectId, LocalDateTime createdDateStart,
      LocalDateTime createdDateEnd);

  Space checkAndFind(Long id);

  List<Space> checkAndFind(Collection<Long> reqIds);

  void check(Long id);

  void checkTenantSizeQuota(Space targetSpaceDb);

  void checkSpaceSizeQuota(Space targetSpaceDb);

  void checkSpaceEmpty(Set<Long> spaceIds);

  Space findNotEmptyOneOf(Collection<Long> spaceIds);

  Boolean isAuthCtrl(Long id);

  void checkAddNameExists(String name);

  void checkUpdateNameExists(Long spaceId, String name);

  void setObjectStats(List<Space> spaces);

}
