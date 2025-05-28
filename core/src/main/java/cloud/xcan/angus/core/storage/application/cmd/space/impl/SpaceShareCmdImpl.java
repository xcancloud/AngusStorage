package cloud.xcan.angus.core.storage.application.cmd.space.impl;

import static cloud.xcan.angus.core.utils.BeanFieldUtils.getNullPropertyNames;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_PUBLIC_TOKEN_LENGTH;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.ProtocolAssert;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.storage.application.cmd.space.SpaceShareCmd;
import cloud.xcan.angus.core.storage.application.query.file.ObjectFileQuery;
import cloud.xcan.angus.core.storage.application.query.space.SpaceAuthQuery;
import cloud.xcan.angus.core.storage.application.query.space.SpaceObjectQuery;
import cloud.xcan.angus.core.storage.application.query.space.SpaceShareQuery;
import cloud.xcan.angus.core.storage.domain.space.auth.SpacePermission;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObject;
import cloud.xcan.angus.core.storage.domain.space.share.SpaceShare;
import cloud.xcan.angus.core.storage.domain.space.share.SpaceShareRepo;
import cloud.xcan.angus.spec.utils.ObjectUtils;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

@Biz
public class SpaceShareCmdImpl extends CommCmd<SpaceShare, Long> implements SpaceShareCmd {

  @Resource
  private SpaceShareRepo spaceShareRepo;

  @Resource
  private SpaceShareQuery spaceShareQuery;

  @Resource
  private SpaceObjectQuery spaceObjectQuery;

  @Resource
  private SpaceAuthQuery spaceAuthQuery;

  @Resource
  private ObjectFileQuery objectFileQuery;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public SpaceShare add(SpaceShare share) {
    return new BizTemplate<SpaceShare>() {
      @Override
      protected void checkParams() {
        // Check expiration duration, required when expired = true
        ProtocolAssert.assertTrue(!share.getExpired()
            || isNotEmpty(share.getExpiredDuration()), "Expiration duration is required");
        // Check access password, required when public = false
        ProtocolAssert.assertTrue(share.getPublic0()
            || isNotEmpty(share.getPassword()), "Access password is required");
        // Check share permission
        spaceAuthQuery.checkShareAuth(getUserId(), share.getSpaceId());
        // Check max share num
        spaceShareQuery.checkMaxShareNum(share);
        if (!share.getAll()) {
          ProtocolAssert.assertTrue(isNotEmpty(share.getObjectIds()),
              "Sharing objectIds is required");
          // Check objects exits
          spaceObjectQuery.checkAndFind(share.getSpaceId(), share.getObjectIds());
          // Check nested duplicates <- Allow cascading
          // spaceObjectQuery.checkNestedDuplicates(objectsDb);
        }
      }

      @Override
      protected SpaceShare process() {
        // Generate public token
        share.setPublicToken(RandomStringUtils.randomAlphanumeric(MAX_PUBLIC_TOKEN_LENGTH));
        // Generate access sharing url(sid(id),spt(publicToken),spf(public))
        share.setId(uidGenerator.getUID());
        share.setUrl(share.getUrl() + String
            .format("?sid=%s&spt=%s&spf=%s", share.getId(), share.getPublicToken(),
                share.getPublic0()));
        // Save sharing
        spaceShareRepo.save(share);
        return share;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public SpaceShare quickAdd(SpaceShare share) {
    return new BizTemplate<SpaceShare>() {
      SpaceObject objectDb;
      SpaceShare shareDb;

      @Override
      protected void checkParams() {
        // Find existed share
        shareDb = spaceShareQuery.findByQuickObjectId(share.getQuickObjectId());

        if (shareDb == null) {
          // Check object exits
          objectDb = spaceObjectQuery.checkAndFind(share.getQuickObjectId());
          // Check share permission
          spaceAuthQuery.checkShareAuth(getUserId(), objectDb.getSpaceId());
        }
      }

      @Override
      protected SpaceShare process() {
        if (nonNull(shareDb)) {
          return shareDb;
        }

        share.setSpaceId(objectDb.getSpaceId());
        spaceObjectQuery.associateFile(objectDb);

        // Generate public token
        share.setPublicToken(RandomStringUtils.randomAlphanumeric(MAX_PUBLIC_TOKEN_LENGTH));
        // Generate access sharing url(sid(id),spt(publicToken),spf(public0))
        share.setId(uidGenerator.getUID());
        String pubDownloadUrl = objectDb.getFile().getDownloadUrl().replace("/api", "/pubapi");
        share.setUrl(pubDownloadUrl + String
            .format("&sid=%s&spt=%s&spf=%s", share.getId(), share.getPublicToken(),
                share.getPublic0()));
        // Save sharing
        spaceShareRepo.save(share);
        return share;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(SpaceShare share) {
    new BizTemplate<Void>() {
      SpaceShare shareDb;

      @Override
      protected void checkParams() {
        // Check share exists
        shareDb = spaceShareQuery.checkAndFind(share.getId());
        // Check share permission
        spaceAuthQuery.checkShareAuth(getUserId(), shareDb.getSpaceId());
        // Check expiration duration, required when expired = true
        ProtocolAssert.assertTrue(Objects.isNull(share.getExpired()) || !share.getExpired()
            || isNotEmpty(share.getExpiredDuration()), "Expiration duration is required");
        // Check access password, required when public0 = false
        // ProtocolAssert.assertTrue(shareDb.getPublic0() -> Update Optional
        //    || isNotEmpty(share.getPassd()), "Access password is required");
        // Check max share num
        spaceShareQuery.checkMaxShareNum(share);
        if (ObjectUtils.isNotEmpty(share.getObjectIds()) && !share.getAll()) {
          ProtocolAssert.assertTrue(isNotEmpty(share.getObjectIds()),
              "Sharing objectIds is required");
          // Check objects exits
          List<SpaceObject> objectsDb = spaceObjectQuery.checkAndFind(
              shareDb.getSpaceId(), share.getObjectIds());
          // Check nested duplicates
          spaceObjectQuery.checkNestedDuplicates(objectsDb);
        }
      }

      @Override
      protected Void process() {
        // Save sharing
        BeanUtils.copyProperties(share, shareDb, getNullPropertyNames(share));
        if (ObjectUtils.isEmpty(share.getObjectIds()) && !shareDb.getAll()) {
          // Share the whole space
          shareDb.setAll(true);
          shareDb.setObjectIds(null);
        }
        spaceShareRepo.save(shareDb);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(HashSet<Long> ids) {
    new BizTemplate<Void>() {
      List<SpaceShare> sharesDb;

      @Override
      protected void checkParams() {
        // Check share exists
        sharesDb = spaceShareQuery.checkAndGetObjects(ids);
        // Check share object space sharing permission
        spaceAuthQuery.batchCheckPermission(sharesDb.stream().map(SpaceShare::getSpaceId)
            .collect(Collectors.toSet()), SpacePermission.SHARE);
      }

      @Override
      protected Void process() {
        // Delete sharing
        spaceShareRepo.deleteByIdIn(ids);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<SpaceShare, Long> getRepository() {
    return spaceShareRepo;
  }
}
