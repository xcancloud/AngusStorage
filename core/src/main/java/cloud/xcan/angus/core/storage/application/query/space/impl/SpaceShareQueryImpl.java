package cloud.xcan.angus.core.storage.application.query.space.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertNotNull;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertUnauthorized;
import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.findFirstValue;
import static cloud.xcan.angus.core.storage.application.converter.SpaceObjectConverter.formatShareDownloadUrl;
import static cloud.xcan.angus.core.storage.application.converter.SpaceObjectConverter.toSpaceObjectSummary;
import static cloud.xcan.angus.core.storage.domain.StorageMessage.SHARE_OBJECT_OVER_LIMIT_CODE;
import static cloud.xcan.angus.core.storage.domain.StorageMessage.SHARE_OBJECT_OVER_LIMIT_T;
import static cloud.xcan.angus.remote.CommonMessage.SHARE_PASSWORD_ERROR_T;
import static cloud.xcan.angus.remote.CommonMessage.SHARE_TOKEN_ERROR_T;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_SHARE_OBJECT_NUM;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;

import cloud.xcan.angus.api.manager.UserManager;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.exception.QuotaException;
import cloud.xcan.angus.core.jpa.criteria.CriteriaUtils;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.storage.application.query.space.SpaceAuthQuery;
import cloud.xcan.angus.core.storage.application.query.space.SpaceObjectQuery;
import cloud.xcan.angus.core.storage.application.query.space.SpaceShareQuery;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObject;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObjectRepo;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObjectSearchRepo;
import cloud.xcan.angus.core.storage.domain.space.share.SpaceShare;
import cloud.xcan.angus.core.storage.domain.space.share.SpaceShareListRepo;
import cloud.xcan.angus.core.storage.domain.space.share.SpaceShareRepo;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import cloud.xcan.angus.spec.utils.ObjectUtils;
import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@org.springframework.stereotype.Service
public class SpaceShareQueryImpl implements SpaceShareQuery {

  @Resource
  private SpaceShareRepo spaceShareRepo;

  @Resource
  private SpaceShareListRepo spaceShareListRepo;

  @Resource
  private SpaceObjectRepo spaceObjectRepo;

  @Resource
  private SpaceObjectSearchRepo spaceObjectSearchRepo;

  @Resource
  private SpaceObjectQuery spaceObjectQuery;

  @Resource
  private SpaceAuthQuery spaceAuthQuery;

  @Resource
  private UserManager userManager;

  @Override
  public SpaceShare detail(Long id) {
    return new BizTemplate<SpaceShare>() {
      SpaceShare spaceShareDb;

      @Override
      protected void checkParams() {
        // Check the share exited
        spaceShareDb = checkAndFind(id);
        // Check the share permission
        spaceAuthQuery.checkShareAuth(getUserId(), spaceShareDb.getSpaceId());
      }

      @Override
      protected SpaceShare process() {
        return spaceShareDb;
      }
    }.execute();
  }

  @Override
  public Page<SpaceShare> list(GenericSpecification<SpaceShare> spec, PageRequest pageable) {
    return new BizTemplate<Page<SpaceShare>>() {
      String spaceId;

      @Override
      protected void checkParams() {
        spaceId = findFirstValue(spec.getCriteria(), "spaceId");
        assertNotNull(spaceId, "spaceId is required");
        spaceAuthQuery.checkShareAuth(getUserId(), Long.valueOf(spaceId));
      }

      @Override
      protected Page<SpaceShare> process() {
        // Set authorization conditions when you are not an administrator or only query yourself
        return spaceShareListRepo.find(spec.getCriteria(), pageable, SpaceShare.class,
            new String[]{"remark"});
      }
    }.execute();
  }

  @Override
  public SpaceShare shareDetailPub(Long sid, String spt, String password) {
    return new BizTemplate<SpaceShare>(false) {
      SpaceShare spaceShareDb;

      @Override
      protected void checkParams() {
        // Check the share exited
        spaceShareDb = checkAndFind(sid);
        // Check the spt(public token) authorization
        assertUnauthorized(spt.equals(spaceShareDb.getPublicToken()), SHARE_TOKEN_ERROR_T);
        // Check the password where public0 = false
        assertUnauthorized(spaceShareDb.getPublic0() ||
            spaceShareDb.getPassword().equals(password), SHARE_PASSWORD_ERROR_T);
      }

      @Override
      protected SpaceShare process() {
        // Set user name and avatar
        userManager.setUserNameAndAvatar(Collections.singleton(spaceShareDb), "createdBy");
        return spaceShareDb;
      }
    }.execute();
  }

  @Override
  public SpaceObject objectDetailPub(Long oid, Long sid, String spt, String password) {
    return new BizTemplate<SpaceObject>(false) {
      SpaceShare spaceShareDb;
      SpaceObject spaceObjectDb;

      @Override
      protected void checkParams() {
        // Check the object exited
        spaceObjectDb = spaceObjectQuery.checkAndFind(oid);
        // Check the share exited
        spaceShareDb = checkAndFind(sid);
        // Check the sharing id is consistent
        assertTrue(spaceShareDb.getSpaceId().equals(spaceObjectDb.getSpaceId()),
            "Sharing space is inconsistent");
        // Check the share oid exited
        assertResourceNotFound(spaceShareDb.getObjectIds().contains(oid), oid);
        // Check the spt(public token) authorization
        assertUnauthorized(spt.equals(spaceShareDb.getPublicToken()),
            SHARE_TOKEN_ERROR_T);
        // Check the password where public = false
        assertUnauthorized(spaceShareDb.getPublic0() ||
            spaceShareDb.getPassword().equals(password), SHARE_PASSWORD_ERROR_T);
      }

      @Override
      protected SpaceObject process() {
        spaceObjectDb.setSummary(toSpaceObjectSummary(spaceObjectDb));
        if (spaceObjectDb.isFile()) {
          spaceObjectQuery.associateFile(spaceObjectDb);
          spaceObjectDb.getFile().setDownloadUrl(
              formatShareDownloadUrl(spaceObjectDb.getFile().getDownloadUrl(), sid, spt, password));
        }
        return spaceObjectDb;
      }
    }.execute();
  }

  @Override
  public Page<SpaceObject> objectListPub(Set<SearchCriteria> criteria, PageRequest pageable) {
    return new BizTemplate<Page<SpaceObject>>(false) {
      SpaceShare spaceShareDb;
      final String spaceId = findFirstValue(criteria, "spaceId");

      @Override
      protected void checkParams() {
        assertNotNull(spaceId, "spaceId is required");
        String sid = findFirstValue(criteria, "sid");
        assertNotNull(sid, "sid is required");
        String spt = findFirstValue(criteria, "spt");
        assertNotNull(spt, "spt is required");
        String password = findFirstValue(criteria, "password");

        // Check share exits
        spaceShareDb = checkAndFind(Long.parseLong(sid));
        // Check spt(public token) authorization
        assertUnauthorized(spt.equals(spaceShareDb.getPublicToken()), SHARE_TOKEN_ERROR_T);
        // Check password where public0 = false
        assertUnauthorized(spaceShareDb.getPublic0() ||
            spaceShareDb.getPassword().equals(password), SHARE_PASSWORD_ERROR_T);
      }

      @Override
      protected Page<SpaceObject> process() {
        // Set filter tenant
        PrincipalContext.get().setTenantId(spaceShareDb.getTenantId());

        if (!spaceShareDb.getAll()) {
          int minLevel = spaceObjectRepo.findMinLevelByIdIn(Long.parseLong(spaceId),
              spaceShareDb.getObjectIds());
          // Return to the first level of sharing
          if (minLevel > 0) {
            CriteriaUtils.containsAndRemove(criteria, "parentDirectoryId");
            criteria.add(SearchCriteria.equal("level", minLevel));
          }
          // Filter sharing objectIds
          criteria.add(SearchCriteria.in("id", spaceShareDb.getObjectIds()));
        }

        Page<SpaceObject> page = spaceObjectSearchRepo
            .find(criteria, pageable, SpaceObject.class, null);
        spaceObjectQuery.setObjectStatsAndSummary(page.getContent());
        return page;
      }
    }.execute();
  }

  @Override
  public SpaceShare findByQuickObjectId(Long quickObjectId) {
    return spaceShareRepo.findByQuickObjectId(quickObjectId);
  }

  @Override
  public SpaceShare checkAndFind(Long id) {
    return spaceShareRepo.findById(id).orElseThrow(() -> ResourceNotFound.of(id, "SpaceShare"));
  }

  @Override
  public List<SpaceShare> checkAndGetObjects(HashSet<Long> ids) {
    List<SpaceShare> existedObjects = spaceShareRepo.findAllById(ids);
    if (ids.size() != existedObjects.size()) {
      if (ObjectUtils.isNotEmpty(existedObjects)) {
        ids.removeAll(existedObjects.stream().map(SpaceShare::getId).collect(Collectors.toSet()));
      }
      throw ResourceNotFound.of(ids.stream().findFirst().get(), "SpaceShare");
    }
    return existedObjects;
  }

  @Override
  public void checkMaxShareNum(SpaceShare share) {
    if (!share.getAll() && share.getObjectIds().size() > MAX_SHARE_OBJECT_NUM) {
      throw QuotaException.of(SHARE_OBJECT_OVER_LIMIT_CODE, SHARE_OBJECT_OVER_LIMIT_T,
          new Object[]{MAX_SHARE_OBJECT_NUM});
    }
  }
}
