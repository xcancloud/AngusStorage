package cloud.xcan.angus.core.storage.application.cmd.space.impl;


import static cloud.xcan.angus.core.storage.domain.StorageMessage.SPACE_FORBID_AUTH_CREATOR;
import static cloud.xcan.angus.core.storage.domain.StorageMessage.SPACE_FORBID_AUTH_CREATOR_CODE;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizAssert;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.storage.application.cmd.space.SpaceAuthCmd;
import cloud.xcan.angus.core.storage.application.cmd.space.SpaceCmd;
import cloud.xcan.angus.core.storage.application.converter.SpaceAuthConverter;
import cloud.xcan.angus.core.storage.application.query.space.SpaceAuthQuery;
import cloud.xcan.angus.core.storage.application.query.space.SpaceQuery;
import cloud.xcan.angus.core.storage.domain.space.Space;
import cloud.xcan.angus.core.storage.domain.space.auth.SpaceAuth;
import cloud.xcan.angus.core.storage.domain.space.auth.SpaceAuthRepo;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Biz
public class SpaceAuthCmdImpl extends CommCmd<SpaceAuth, Long> implements SpaceAuthCmd {

  @Resource
  SpaceQuery spaceQuery;

  @Resource
  SpaceCmd spaceCmd;

  @Resource
  SpaceAuthQuery spaceAuthQuery;

  @Resource
  SpaceAuthRepo spaceAuthRepo;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> add(SpaceAuth spaceAuth) {
    return new BizTemplate<IdKey<Long, Object>>() {
      Space spaceDb;

      @Override
      protected void checkParams() {
        // Check the space existed
        spaceDb = spaceQuery.checkAndFind(spaceAuth.getSpaceId());
        // Do not allow duplicate authorization for the creator
        BizAssert.assertTrue(!spaceDb.getCreatedBy().equals(spaceAuth.getAuthObjectId()),
            SPACE_FORBID_AUTH_CREATOR_CODE, SPACE_FORBID_AUTH_CREATOR);
        // Check user have space authorization permissions
        spaceAuthQuery.checkGrantAuth(getUserId(), spaceAuth.getSpaceId());
        // Check for duplicate authorizations
        spaceAuthQuery.checkRepeatAuth(spaceAuth.getSpaceId(), spaceAuth.getAuthObjectId(),
            spaceAuth.getAuthObjectType());
      }

      @Override
      protected IdKey<Long, Object> process() {
        return insert(spaceAuth, "authObjectId");
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void addCreatorAuth(Set<Long> creatorIds, Long spaceId) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // 1. Check the project existed
        //projectQuery.check(projectId);
        // 2. Check for duplicate authorizations
        //projectAuthQuery.checkRepeatAuth(projectId, getUserId(), AuthObjectType.USER, true);
      }

      @Override
      protected Void process() {
        // Allow modification of new authorization
        spaceAuthRepo.deleteBySpaceIdAndCreator(spaceId, true);

        // Save authorization
        List<SpaceAuth> spaceAuths = creatorIds.stream()
            .map(creatorId -> SpaceAuthConverter.toCreatorAuth(spaceId, creatorId, uidGenerator))
            .collect(Collectors.toList());
        batchInsert(spaceAuths, "authObjectId");
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void replace(SpaceAuth spaceAuth) {
    new BizTemplate<Void>() {
      Space spaceDb;
      SpaceAuth spaceAuthDb;

      @Override
      protected void checkParams() {
        // Check the space auth existed
        spaceAuthDb = spaceAuthQuery.checkAndFind(spaceAuth.getId());
        // Check the space existed
        spaceDb = spaceQuery.checkAndFind(spaceAuthDb.getSpaceId());
        // Do not allow duplicate authorization for the creator
        BizAssert.assertTrue(!spaceDb.getCreatedBy().equals(spaceAuth.getAuthObjectId()),
            SPACE_FORBID_AUTH_CREATOR_CODE, SPACE_FORBID_AUTH_CREATOR);
        // Check if current user have project authorization permissions
        spaceAuthQuery.checkGrantAuth(getUserId(), spaceAuthDb.getSpaceId());
      }

      @Override
      protected Void process() {
        // Replace authorization
        spaceAuthDb.setAuths(spaceAuth.getAuths());
        spaceAuthRepo.save(spaceAuthDb);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(SpaceAuth spaceAuth) {
    new BizTemplate<Void>() {
      Space spaceDb;
      SpaceAuth spaceAuthDb;

      @Override
      protected void checkParams() {
        // Check the space auth existed
        spaceAuthDb = spaceAuthQuery.checkAndFind(spaceAuth.getId());
        // Check if space existed
        spaceDb = spaceQuery.checkAndFind(spaceAuthDb.getSpaceId());
        // Do not allow to delete authorization of creator
        BizAssert.assertTrue(!spaceDb.getCreatedBy().equals(spaceAuthDb.getAuthObjectId()),
            SPACE_FORBID_AUTH_CREATOR_CODE, SPACE_FORBID_AUTH_CREATOR);
        // Check if user have space authorization permissions
        spaceAuthQuery.checkGrantAuth(getUserId(), spaceAuthDb.getSpaceId());
      }

      @Override
      protected Void process() {
        // Delete space auth
        spaceAuthRepo.delete(spaceAuth);
        return null;
      }
    }.execute();
  }

  @Override
  public void enabled(Long spaceId, Boolean enabled) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        spaceCmd.authEnabled(spaceId, enabled);
        // NO Activity
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<SpaceAuth, Long> getRepository() {
    return this.spaceAuthRepo;
  }
}




