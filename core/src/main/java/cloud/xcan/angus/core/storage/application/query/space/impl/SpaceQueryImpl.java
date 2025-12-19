package cloud.xcan.angus.core.storage.application.query.space.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertNotNull;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceExisted;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.findFirstAndRemove;
import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.findFirstValueAndRemove;
import static cloud.xcan.angus.core.storage.application.converter.SpaceObjectConverter.toSpaceSummary;
import static cloud.xcan.angus.core.storage.domain.StorageMessage.SPACE_DELETED_NOT_EMPTY;
import static cloud.xcan.angus.core.storage.domain.StorageMessage.SPACE_DELETED_NOT_EMPTY_CODE;
import static cloud.xcan.angus.core.storage.domain.StorageMessage.SPACE_NAME_EXISTED_T;
import static cloud.xcan.angus.core.storage.domain.StorageMessage.SPACE_SIZE_OVER_LIMIT_CODE;
import static cloud.xcan.angus.core.storage.domain.StorageMessage.SPACE_SIZE_OVER_LIMIT_T;
import static cloud.xcan.angus.core.utils.CoreUtils.getCommonResourcesStatsFilter;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.remote.search.SearchCriteria.equal;
import static cloud.xcan.angus.remote.search.SearchCriteria.merge;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.setting.quota.QuotaResource;
import cloud.xcan.angus.api.commonlink.setting.tenant.quota.SettingTenantQuota;
import cloud.xcan.angus.api.commonlink.space.StorageResourcesCount;
import cloud.xcan.angus.api.commonlink.space.StorageResourcesCreationCount;
import cloud.xcan.angus.api.enums.AuthObjectType;
import cloud.xcan.angus.api.enums.FileResourceType;
import cloud.xcan.angus.api.enums.FileType;
import cloud.xcan.angus.api.manager.SettingTenantQuotaManager;
import cloud.xcan.angus.api.manager.UserManager;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.exception.BizException;
import cloud.xcan.angus.core.biz.exception.QuotaException;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.storage.application.query.bucket.BucketBizConfigQuery;
import cloud.xcan.angus.core.storage.application.query.space.SpaceAuthQuery;
import cloud.xcan.angus.core.storage.application.query.space.SpaceObjectQuery;
import cloud.xcan.angus.core.storage.application.query.space.SpaceQuery;
import cloud.xcan.angus.core.storage.domain.FileSummary;
import cloud.xcan.angus.core.storage.domain.bucket.config.BucketBizConfigRepo;
import cloud.xcan.angus.core.storage.domain.space.Space;
import cloud.xcan.angus.core.storage.domain.space.SpaceListRepo;
import cloud.xcan.angus.core.storage.domain.space.SpaceRepo;
import cloud.xcan.angus.core.storage.domain.space.SpaceSearchRepo;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObject;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObjectRepo;
import cloud.xcan.angus.core.storage.infra.store.ObjectProperties;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.spec.experimental.Assert;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import cloud.xcan.angus.spec.unit.DataSize;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@org.springframework.stereotype.Service
public class SpaceQueryImpl implements SpaceQuery {

  @Resource
  private SpaceRepo spaceRepo;

  @Resource
  private SpaceListRepo spaceListRepo;

  @Resource
  private SpaceSearchRepo scenarioSearchRepo;

  @Resource
  private BucketBizConfigRepo bucketBizConfigRepo;

  @Resource
  private BucketBizConfigQuery bucketBizConfigQuery;

  @Resource
  private SpaceAuthQuery spaceAuthQuery;

  @Resource
  private SpaceObjectRepo spaceObjectRepo;

  @Resource
  private SpaceObjectQuery spaceObjectQuery;

  @Resource
  private UserManager userManager;

  @Resource
  private SettingTenantQuotaManager settingTenantQuotaManager;

  @Resource
  private ObjectProperties objectProperties;

  @Override
  public Space detail(Long id) {
    return new BizTemplate<Space>() {
      Space spaceDb;

      @Override
      protected void checkParams() {
        // Check that you or others create and authorize me before you can query
        spaceAuthQuery.checkViewAuth(PrincipalContext.getUserId(), id);
        // Check the space existed
        spaceDb = checkAndFind(id);
      }

      @Override
      protected Space process() {

        spaceDb.setStoreType(objectProperties.getStoreType());

        setObjectStats(List.of(spaceDb));

        SettingTenantQuota tenantQuota = settingTenantQuotaManager.findTenantQuota(
            getOptTenantId(), QuotaResource.FileStore);
        Assert.assertNotNull(tenantQuota, "Tenant quota setting not found");
        long tenantQuotaSize = tenantQuota.getQuota();
        spaceDb.setSummary(toSpaceSummary(spaceDb, tenantQuotaSize));

        spaceDb.setConfig(bucketBizConfigQuery.findByBizKey(spaceDb.getBizKey()));
        return spaceDb;
      }
    }.execute();
  }

  @Override
  public Page<Space> list(GenericSpecification<Space> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Space>>() {
      String appCode;

      @Override
      protected void checkParams() {
        appCode = findFirstValueAndRemove(spec.getCriteria(), "appCode");
        assertNotNull(appCode, "appCode is required");
      }

      @Override
      protected Page<Space> process() {
        assembleFilterParam(spec.getCriteria(), spaceAuthQuery, userManager,
            bucketBizConfigRepo, appCode);

        Page<Space> page = fullTextSearch
            ? scenarioSearchRepo.find(spec.getCriteria(), pageable, Space.class, match)
            : spaceListRepo.find(spec.getCriteria(), pageable, Space.class, null);
        setObjectStats(page.getContent());
        return page;
      }
    }.execute();
  }

  @Override
  public StorageResourcesCount countStatistics(Long projectId, AuthObjectType creatorObjectType,
      Long creatorObjectId, LocalDateTime createdDateStart, LocalDateTime createdDateEnd) {
    return new BizTemplate<StorageResourcesCount>() {

      @Override
      protected StorageResourcesCount process() {
        final StorageResourcesCount result = new StorageResourcesCount();
        Set<Long> createdBys = null;

        // Find all when condition is null, else find by condition
        if (nonNull(creatorObjectType)) {
          createdBys = userManager.getUserIdByOrgType0(creatorObjectType, creatorObjectId);
        }

        Set<SearchCriteria> allFilters = getCommonResourcesStatsFilter(projectId, createdDateStart,
            createdDateEnd, createdBys);

        // Number of statistical data
        countSpace(result, allFilters);
        countSpaceFileAndDirectory(result, allFilters);
        return result;
      }
    }.execute();
  }

  @Override
  public StorageResourcesCreationCount resourcesCreationStatistics(Long projectId,
      AuthObjectType creatorObjectType, Long creatorObjectId, LocalDateTime createdDateStart,
      LocalDateTime createdDateEnd) {
    return new BizTemplate<StorageResourcesCreationCount>() {

      final LocalDateTime now = LocalDateTime.now();
      final LocalDateTime last7DayBefore = now.minusDays(7);
      final LocalDateTime last30DayBefore = now.minusDays(30);

      final StorageResourcesCreationCount result = new StorageResourcesCreationCount();

      Set<Long> createdBys = null;


      @Override
      protected StorageResourcesCreationCount process() {
        // Find all when condition is null, else find by condition
        if (nonNull(creatorObjectType)) {
          createdBys = userManager.getUserIdByOrgType0(creatorObjectType, creatorObjectId);
        }

        Set<SearchCriteria> commonFilters = getCommonResourcesStatsFilter(projectId,
            createdDateStart, createdDateEnd, createdBys);
        Set<SearchCriteria> last7DayFilters = getCommonResourcesStatsFilter(
            projectId, last7DayBefore, now, createdBys);
        Set<SearchCriteria> last30DayFilters = getCommonResourcesStatsFilter(
            projectId, last30DayBefore, now, createdBys);

        countSpaceFile(result, commonFilters, last7DayFilters, last30DayFilters);

        StorageResourcesCount countStatistics = countStatistics(projectId, creatorObjectType,
            creatorObjectId, createdDateStart, createdDateEnd);
        Map<FileResourceType, Long> fileByResourceType = new HashMap<>();
        fileByResourceType.put(FileResourceType.SPACE, countStatistics.getAllSpaces());
        fileByResourceType.put(FileResourceType.DIRECTORY,
            countStatistics.getAllSpaceDirectories());
        fileByResourceType.put(FileResourceType.FILE, countStatistics.getAllSpaceFiles());
        result.setSpaceFileByResourceType(fileByResourceType);
        return result;
      }
    }.execute();
  }

  @Override
  public Space checkAndFind(Long id) {
    return spaceRepo.findById(id).orElseThrow(() -> ResourceNotFound.of(id, "Space"));
  }

  @Override
  public List<Space> checkAndFind(Collection<Long> reqIds) {
    List<Space> spacesDb = spaceRepo.findAllById(reqIds);
    Set<Long> spaceDbIds = spacesDb.stream().map(Space::getId).collect(Collectors.toSet());
    Set<Long> retainsIds = new HashSet<>(reqIds);
    retainsIds.removeAll(spaceDbIds);
    assertResourceNotFound(isEmpty(retainsIds), retainsIds, "Space");
    return spacesDb;
  }

  @Override
  public void check(Long id) {
    assertResourceExisted(spaceRepo.existsById(id), id, "Space");
  }

  @Override
  public void checkTenantSizeQuota(Space space) {
    // Check the tenant quota
    if (space.hasQuotaLimit()) {
      Long tenantSize = spaceObjectRepo.sumSizeByTenantId(getOptTenantId());
      if (nonNull(tenantSize)) {
        settingTenantQuotaManager.checkTenantQuota(QuotaResource.FileStore, null, tenantSize);
      }
    }
  }

  @Override
  public void checkSpaceSizeQuota(Space space) {
    if (space.hasQuotaLimit()) {
      DataSize spaceQuota = DataSize.parse(space.getQuotaSize());
      Long spaceSize = spaceObjectRepo.sumSizeBySpaceId(space.getId());
      if (nonNull(spaceSize) && spaceSize >= spaceQuota.toBytes()) {
        throw QuotaException.of(SPACE_SIZE_OVER_LIMIT_CODE, SPACE_SIZE_OVER_LIMIT_T,
            new Object[]{spaceQuota.toHumanString()});
      }
    }
  }

  @Override
  public void checkSpaceNumQuota(long incr) {
    long spaceSize = spaceRepo.countByTenantId(getOptTenantId());
    settingTenantQuotaManager.checkTenantQuota(QuotaResource.DataSpace, null, spaceSize + incr);
  }

  @Override
  public void checkSpaceEmpty(Set<Long> spaceIds) {
    Space notEmptySpace = findNotEmptyOneOf(spaceIds);
    if (nonNull(notEmptySpace)) {
      throw BizException.of(SPACE_DELETED_NOT_EMPTY_CODE, SPACE_DELETED_NOT_EMPTY,
          new Object[]{notEmptySpace.getName()});
    }
  }

  @Override
  public Space findNotEmptyOneOf(Collection<Long> spaceIds) {
    return spaceRepo.findNotEmptyBySpaceIdInLimit1(spaceIds);
  }

  @Override
  public Boolean isAuthCtrl(Long id) {
    Optional<Space> scriptOptional = spaceRepo.findById(id);
    return scriptOptional.isEmpty() || scriptOptional.get().isEnabledAuth();
  }

  @Override
  public void checkAddNameExists(String name) {
    assertResourceExisted(spaceRepo.countByName(name) <= 0,
        SPACE_NAME_EXISTED_T, new Object[]{name});
  }

  @Override
  public void checkUpdateNameExists(Long spaceId, String name) {
    if (isNotEmpty(name)) {
      List<Space> exitedSpaceDb = spaceRepo.findByNameAndIdNot(name, spaceId);
      assertResourceExisted(exitedSpaceDb, SPACE_NAME_EXISTED_T, new Object[]{name});
    }
  }

  @Override
  public void setObjectStats(List<Space> spaces) {
    spaceObjectQuery.setSpaceStats(spaces);
  }

  private void countSpace(StorageResourcesCount result, Set<SearchCriteria> allFilters) {
    result.setAllSpaces(spaceRepo.countAllByFilters(allFilters));
  }

  private void countSpaceFileAndDirectory(StorageResourcesCount result,
      Set<SearchCriteria> allFilters) {
    Map<FileType, FileSummary> summaries = spaceObjectRepo.countByFiltersAndGroup(
            SpaceObject.class, FileSummary.class, allFilters, "type", "id")
        .stream().collect(Collectors.toMap(FileSummary::getKey, x -> x));
    result.setAllSpaceFiles(summaries.getOrDefault(FileType.FILE, new FileSummary()).getTotal());
    result.setAllSpaceDirectories(
        summaries.getOrDefault(FileType.DIRECTORY, new FileSummary()).getTotal());
  }

  private void countSpaceFile(StorageResourcesCreationCount result, Set<SearchCriteria> allFilters,
      Set<SearchCriteria> last7DayFilter, Set<SearchCriteria> last30DayFilter) {
    result.setAllSpaceFile(
            spaceObjectRepo.countAllByFilters(merge(allFilters, equal("type", "FILE"))))
        .setSpaceFileByLast7Day(
            spaceObjectRepo.countAllByFilters(merge(last7DayFilter, equal("type", "FILE"))))
        .setSpaceFileByLast30Day(
            spaceObjectRepo.countAllByFilters(merge(last30DayFilter, equal("type", "FILE"))));
  }

  public static void assembleFilterParam(Set<SearchCriteria> criteria,
      SpaceAuthQuery spaceAuthQuery, UserManager userManager,
      BucketBizConfigRepo bucketBizConfigRepo, String appCode) {
    String appAdminCode = bucketBizConfigRepo.findAppAdminCodeByAppCode(appCode);
    assertNotNull(appCode, "appCode is required");

    criteria.add(SearchCriteria.equal("customized", true));

    // Set authorization conditions when you are not an administrator or only query yourself
    SearchCriteria adminCriteria = findFirstAndRemove(criteria, "admin");
    boolean admin = Objects.nonNull(adminCriteria) && Boolean.parseBoolean(
        adminCriteria.getValue().toString().replaceAll("\"", ""));
    if (!admin || !spaceAuthQuery.isAdminUser(appAdminCode)) {
      criteria.add(SearchCriteria.in("authObjectId", userManager.getValidOrgAndUserIds()));
    }
  }

}
