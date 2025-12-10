package cloud.xcan.angus.core.storage.application.cmd.space.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.utils.CoreUtils.copyPropertiesIgnoreNull;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.spec.experimental.BizConstant.OWNER_TENANT_ID;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.storage.application.cmd.space.SpaceAuthCmd;
import cloud.xcan.angus.core.storage.application.cmd.space.SpaceCmd;
import cloud.xcan.angus.core.storage.application.converter.SpaceConverter;
import cloud.xcan.angus.core.storage.application.query.bucket.BucketQuery;
import cloud.xcan.angus.core.storage.application.query.space.SpaceAuthQuery;
import cloud.xcan.angus.core.storage.application.query.space.SpaceQuery;
import cloud.xcan.angus.core.storage.domain.bucket.config.BucketBizConfig;
import cloud.xcan.angus.core.storage.domain.file.ObjectFileRepo;
import cloud.xcan.angus.core.storage.domain.space.Space;
import cloud.xcan.angus.core.storage.domain.space.SpaceRepo;
import cloud.xcan.angus.core.storage.domain.space.auth.SpaceAuthRepo;
import cloud.xcan.angus.core.storage.domain.space.auth.SpacePermission;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObjectRepo;
import cloud.xcan.angus.core.storage.infra.store.ObjectClient;
import cloud.xcan.angus.core.storage.infra.store.impl.ObjectClientFactory;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.springframework.transaction.annotation.Transactional;

@Biz
public class SpaceCmdImpl extends CommCmd<Space, Long> implements SpaceCmd {

  @Resource
  private SpaceRepo spaceRepo;

  @Resource
  private SpaceObjectRepo spaceObjectRepo;

  @Resource
  private ObjectFileRepo objectFileRepo;

  @Resource
  private SpaceAuthRepo spaceAuthRepo;

  @Resource
  private SpaceQuery spaceQuery;

  @Resource
  private BucketQuery bucketQuery;

  @Resource
  private SpaceAuthQuery spaceAuthQuery;

  @Resource
  private SpaceAuthCmd spaceAuthCmd;

  /**
   * Add customized space.
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> add(Space space) {
    return new BizTemplate<IdKey<Long, Object>>() {
      BucketBizConfig bizConfig;

      @Override
      protected void checkParams() {
        // Check the space name existed
        spaceQuery.checkAddNameExists(space.getName());
        // Check the bizKey existed
        bizConfig = bucketQuery.checkAndFindByBizKey(space.getBizKey());
        assertTrue(bizConfig.getAllowTenantCreated(), String
            .format("Tenant custom created business [%s] spaces are not supported",
                space.getBizKey()));
        // Check the space num quota
        spaceQuery.checkSpaceNumQuota(1);
        // Check the space size quota: the total allocation cannot exceed the tenant quota limit
        spaceQuery.checkTenantSizeQuota(space);
      }

      @Override
      protected IdKey<Long, Object> process() {
        space.setBucketName(bizConfig.getBucketName());
        space.setCustomized(true);
        IdKey<Long, Object> idKey = insert(space);

        spaceAuthCmd.addCreatorAuth(Collections.singleton(getUserId()), space.getId());
        return idKey;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(Space space) {
    new BizTemplate<Void>() {
      Space spaceDb;

      @Override
      protected void checkParams() {
        // Check the space existed
        spaceDb = spaceQuery.checkAndFind(space.getId());
        // Check the update space permission
        spaceAuthQuery.checkModifyAuth(getUserId(), space.getId());
        // Check the update name existed
        spaceQuery.checkUpdateNameExists(space.getId(), space.getName());
        // Check the space size quota: the total allocation cannot exceed the tenant quota limit
        spaceQuery.checkTenantSizeQuota(space);
      }

      @Override
      protected Void process() {
        spaceRepo.save(copyPropertiesIgnoreNull(space, spaceDb));
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(Set<Long> ids, boolean deleteStore) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // Check the delete space permission
        spaceAuthQuery.batchCheckPermission(ids, SpacePermission.DELETE);
        // Check the non-empty space is not allowed to delete
        spaceQuery.checkSpaceEmpty(ids);
      }

      @Override
      protected Void process() {
        ObjectClient objectClient = ObjectClientFactory.current();
        List<Space> existedSpaces = spaceRepo.findAllById(ids);
        if (deleteStore) {
          existedSpaces.forEach(space -> {
            objectClient.removeObjects(space.getBucketName(),
                objectClient.getObjectNamePrefix(space.getBucketName(), space.getBizKey(),
                    space.getTenantId(), space.getId()));
          });
        }

        spaceRepo.deleteByIdIn(ids);

        // Space related data needs to be deleted manually first, here make sure to delete all
        spaceAuthRepo.deleteBySpaceIdIn(ids);
        spaceObjectRepo.deleteBySpaceIdIn(ids);
        objectFileRepo.deleteBySpaceIdIn(ids);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void authEnabled(Long id, Boolean enabled) {
    new BizTemplate<Void>() {
      Space spaceDb;

      @Override
      protected void checkParams() {
        // Check the space existed
        spaceDb = spaceQuery.checkAndFind(id);
        // Check the user have space authorization permissions
        spaceAuthQuery.checkGrantAuth(getUserId(), id);
      }

      @Override
      protected Void process() {
        spaceRepo.updateAuthById(id, enabled);
        return null;
      }
    }.execute();
  }

  @Override
  public Space findAndInitByBizKey(BucketBizConfig config, String bizKey) {
    Space space = config.isMultiTenantCtrl()
        ? spaceRepo.findByTenantIdAndBizKeyLimit1(getOptTenantId(), bizKey)
        : spaceRepo.findByBizKeyLimit1(bizKey);
    // Tenant business(tenantBiz=true) does not automatically initialize the space
    if (Objects.nonNull(space)) {
      return space;
    }
    assertTrue(config.getAllowTenantCreated()
        || !config.isMultiTenantCtrl() || getOptTenantId().equals(OWNER_TENANT_ID), String
        .format("Tenant custom created business [%s] spaces are not supported", bizKey));
    // NOOP:: spaceAuthCmd.addCreatorAuth();  -> Only the system administrator has space permission
    return addNonCustomized(config);
  }

  /**
   * Add customized space.
   */
  @NotNull
  @Override
  public Space addCustomized(BucketBizConfig config, String spaceName) {
    Space space = config.isMultiTenantCtrl()
        ? spaceRepo.findByTenantIdAndBizKeyLimit1(getOptTenantId(), config.getBizKey())
        : spaceRepo.findByBizKeyLimit1(config.getBizKey());

    String spaceNameFinal = Objects.nonNull(space)
        ? spaceName + System.currentTimeMillis() : spaceName;
    Space initSpace = SpaceConverter.toInitCustomizedByName(config, uidGenerator, spaceNameFinal);
    // Fix:: Value is null when multi tenant control is turned off or /innerapi upload
    initSpace.setTenantId(getOptTenantId());
    initSpace.setCreatedBy(getUserId()).setCreatedDate(LocalDateTime.now())
        .setLastModifiedBy(getUserId()).setLastModifiedDate(LocalDateTime.now());
    spaceRepo.save(initSpace);
    return initSpace;
  }

  /**
   * Add non customized space.
   */
  @NotNull
  @Override
  public Space addNonCustomized(BucketBizConfig config) {
    Space initSpace = SpaceConverter.toInitNonCustomizedByBizKey(config, uidGenerator);
    // Fix:: Value is null when multi tenant control is turned off or /innerapi upload
    initSpace.setTenantId(getOptTenantId());
    initSpace.setCreatedBy(getUserId()).setCreatedDate(LocalDateTime.now())
        .setLastModifiedBy(getUserId()).setLastModifiedDate(LocalDateTime.now());
    spaceRepo.save(initSpace);
    return initSpace;
  }

  @Override
  protected BaseRepository<Space, Long> getRepository() {
    return spaceRepo;
  }
}
