package cloud.xcan.angus.core.storage.application.query.space.impl;

import static cloud.xcan.angus.core.storage.domain.StorageMessage.SPACE_NO_AUTH;
import static cloud.xcan.angus.core.storage.domain.StorageMessage.SPACE_NO_AUTH_CODE;
import static cloud.xcan.angus.core.storage.domain.StorageMessage.SPACE_NO_TARGET_AUTH;
import static cloud.xcan.angus.core.storage.domain.StorageMessage.SPACE_NO_TARGET_AUTH_CODE;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.hasPolicy;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isTenantSysAdmin;
import static cloud.xcan.angus.remote.message.ProtocolException.M.PARAM_MISSING_KEY;
import static cloud.xcan.angus.remote.message.ProtocolException.M.PARAM_MISSING_T;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.api.enums.AuthObjectType;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.exception.BizException;
import cloud.xcan.angus.core.storage.application.query.space.SpaceAuthQuery;
import cloud.xcan.angus.core.storage.application.query.space.SpaceQuery;
import cloud.xcan.angus.core.storage.domain.bucket.config.BucketBizConfigRepo;
import cloud.xcan.angus.core.storage.domain.space.Space;
import cloud.xcan.angus.core.storage.domain.space.SpaceRepo;
import cloud.xcan.angus.core.storage.domain.space.auth.SpaceAuth;
import cloud.xcan.angus.core.storage.domain.space.auth.SpaceAuthCurrent;
import cloud.xcan.angus.core.storage.domain.space.auth.SpaceAuthRepo;
import cloud.xcan.angus.core.storage.domain.space.auth.SpacePermission;
import cloud.xcan.angus.remote.message.ProtocolException;
import cloud.xcan.angus.remote.message.http.ResourceExisted;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import cloud.xcan.angus.spec.utils.ObjectUtils;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Biz
public class SpaceAuthQueryImpl implements SpaceAuthQuery {

  @Resource
  private SpaceAuthRepo spaceAuthRepo;

  @Resource
  private SpaceQuery spaceQuery;

  @Resource
  private SpaceRepo spaceRepo;

  @Resource
  private BucketBizConfigRepo bucketBizConfigRepo;

  @Resource
  private UserRepo userRepo;

  @Override
  public Boolean status(Long spaceId) {
    return new BizTemplate<Boolean>() {
      Space spaceDb;

      @Override
      protected void checkParams() {
        spaceDb = spaceQuery.checkAndFind(spaceId);
      }

      @Override
      protected Boolean process() {
        return spaceDb.getAuth();
      }
    }.execute();
  }

  @Override
  public List<SpacePermission> userAuth(Long spaceId, Long userId, Boolean admin) {
    return new BizTemplate<List<SpacePermission>>() {
      Space spaceDb;

      @Override
      protected void checkParams() {
        spaceDb = spaceQuery.checkAndFind(spaceId);
      }

      @Override
      protected List<SpacePermission> process() {
        if (Objects.nonNull(admin) && admin && isAdminUser(spaceId)) {
          return SpacePermission.ALL;
        }

        List<SpaceAuth> spaceAuths = findAuth(userId, spaceId);
        if (isCreator(spaceAuths)) {
          return SpacePermission.ALL;
        }

        Set<SpacePermission> permissions = new HashSet<>();
        if (!spaceDb.isEnabledAuth()) {
          permissions.add(SpacePermission.VIEW);
        }

        Set<SpacePermission> authPermissions = spaceAuths.stream().map(SpaceAuth::getAuths)
            .flatMap(Collection::stream).collect(Collectors.toSet());
        authPermissions.addAll(permissions);
        return new ArrayList<>(authPermissions);
      }
    }.execute();
  }

  @Override
  public SpaceAuthCurrent currentUserAuth(Long spaceId, Boolean admin) {
    return new BizTemplate<SpaceAuthCurrent>() {
      Space spaceDb;

      @Override
      protected void checkParams() {
        spaceDb = spaceQuery.checkAndFind(spaceId);
      }

      @Override
      protected SpaceAuthCurrent process() {
        SpaceAuthCurrent authCurrent = new SpaceAuthCurrent();
        authCurrent.setSpaceAuth(spaceDb.getAuth());

        if (Objects.nonNull(admin) && admin && isAdminUser(spaceId)) {
          authCurrent.addPermissions(SpacePermission.ALL);
          return authCurrent;
        }

        List<SpaceAuth> spaceAuths = findAuth(getUserId(), spaceId);
        if (isCreator(spaceAuths)) {
          authCurrent.addPermissions(SpacePermission.ALL);
          return authCurrent;
        }

        Set<SpacePermission> permissions = new HashSet<>();
        if (!spaceDb.isEnabledAuth()) {
          permissions.add(SpacePermission.VIEW);
        }
        Set<SpacePermission> authPermissions = spaceAuths.stream()
            .map(SpaceAuth::getAuths).flatMap(Collection::stream).collect(Collectors.toSet());
        authPermissions.addAll(permissions);
        authCurrent.addPermissions(authPermissions);
        return authCurrent;
      }
    }.execute();
  }

  @Override
  public Map<Long, SpaceAuthCurrent> currentUserAuths(HashSet<Long> spaceIds, Boolean admin) {
    return new BizTemplate<Map<Long, SpaceAuthCurrent>>() {
      List<Space> spacesDb;

      @Override
      protected void checkParams() {
        spacesDb = spaceQuery.checkAndFind(spaceIds);
      }

      @Override
      protected Map<Long, SpaceAuthCurrent> process() {
        Map<Long, SpaceAuthCurrent> authCurrentMap = new HashMap<>();
        if (nonNull(admin) && admin && isAdminUser(spacesDb.get(0).getId())) {
          for (Space space : spacesDb) {
            SpaceAuthCurrent authCurrent = new SpaceAuthCurrent();
            authCurrent.setSpaceAuth(space.getAuth());
            authCurrent.addPermissions(SpacePermission.ALL);
            authCurrentMap.put(space.getId(), authCurrent);
          }
          return authCurrentMap;
        }

        Set<Long> currentCreatorIds = spacesDb.stream()
            .filter(x -> x.getCreatedBy().equals(getUserId())).map(Space::getId)
            .collect(Collectors.toSet());
        if (isNotEmpty(currentCreatorIds)) {
          for (Space space : spacesDb) {
            if (currentCreatorIds.contains(space.getId())) {
              SpaceAuthCurrent authCurrent = new SpaceAuthCurrent();
              authCurrent.setSpaceAuth(space.getAuth());
              authCurrent.addPermissions(SpacePermission.ALL);
              authCurrentMap.put(space.getId(), authCurrent);
            }
          }
        }

        Set<Long> remainIds = new HashSet<>(spaceIds);
        remainIds.removeAll(currentCreatorIds);
        if (isNotEmpty(remainIds)) {
          Map<Long, List<SpaceAuth>> spaceAuthsMap = findAuth(getUserId(), remainIds)
              .stream().collect(Collectors.groupingBy(SpaceAuth::getSpaceId));
          for (Space space : spacesDb) {
            if (remainIds.contains(space.getId())) {
              SpaceAuthCurrent authCurrent = new SpaceAuthCurrent();
              Set<SpacePermission> permissions = new HashSet<>();
              if (!space.isEnabledAuth()) {
                permissions.add(SpacePermission.VIEW);
              }
              List<SpaceAuth> spaceAuths = spaceAuthsMap.get(space.getId());
              if (isNotEmpty(spaceAuths)) {
                Set<SpacePermission> authPermissions = spaceAuths.stream()
                    .map(SpaceAuth::getAuths).flatMap(Collection::stream)
                    .collect(Collectors.toSet());
                permissions.addAll(authPermissions);
              }
              authCurrent.addPermissions(permissions);
              authCurrent.setSpaceAuth(space.getAuth());
              authCurrentMap.put(space.getId(), authCurrent);
            }
          }
        }
        return authCurrentMap;
      }
    }.execute();
  }

  @Override
  public void check(Long spaceId, SpacePermission permission, Long userId) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        checkAuth(userId, spaceId, permission);
        return null;
      }
    }.execute();
  }

  @Override
  public Page<SpaceAuth> find(Specification<SpaceAuth> spec, List<String> spaceIds,
      Pageable pageable) {
    return new BizTemplate<Page<SpaceAuth>>() {
      @Override
      protected void checkParams() {
        if (isEmpty(spaceIds)) {
          throw ProtocolException.of(PARAM_MISSING_T, PARAM_MISSING_KEY,
              new Object[]{"spaceId"});
        }
        batchCheckPermission(spaceIds.stream().map(Long::parseLong).collect(Collectors.toSet()),
            SpacePermission.VIEW);
      }

      @Override
      protected Page<SpaceAuth> process() {
        return spaceAuthRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public SpaceAuth checkAndFind(Long id) {
    return spaceAuthRepo.findById(id)
        .orElseThrow(() -> ResourceNotFound.of(id, "SpaceAuth"));
  }

  @Override
  public void checkViewAuth(Long userId, Long spaceId) {
    checkAuth(userId, spaceId, SpacePermission.VIEW);
  }

  @Override
  public void checkModifyAuth(Long userId, Long spaceId) {
    checkAuth(userId, spaceId, SpacePermission.MODIFY);
  }

  @Override
  public void checkDeleteAuth(Long userId, Long spaceId) {
    checkAuth(userId, spaceId, SpacePermission.DELETE);
  }

  @Override
  public void checkShareAuth(Long userId, Long spaceId) {
    checkAuth(userId, spaceId, SpacePermission.SHARE);
  }

  @Override
  public void checkGrantAuth(Long userId, Long spaceId) {
    checkAuth(userId, spaceId, SpacePermission.GRANT);
  }

  @Override
  public void checkObjectReadAuth(Long userId, Long spaceId) {
    checkAuth(userId, spaceId, SpacePermission.OBJECT_READ);
  }

  @Override
  public void checkObjectWriteAuth(Long userId, Long spaceId) {
    checkAuth(userId, spaceId, SpacePermission.OBJECT_WRITE);
  }

  @Override
  public void checkObjectDeletedAuth(Long userId, Long spaceId) {
    checkAuth(userId, spaceId, SpacePermission.OBJECT_DELETE);
  }

  @Override
  public void checkAuth(Long userId, Long spaceId, SpacePermission permission) {
    if (isAdminUser(spaceId)) {
      return;
    }

    // Fix: When it is not controlled by permissions, it will cause users who do not have authorization permissions to authorize
    if (!permission.equals(SpacePermission.GRANT) && !spaceQuery.isAuthCtrl(spaceId)) {
      return;
    }

    // View as base permissions
    List<SpaceAuth> spaceAuths = findAuth(userId, spaceId);
    if (permission.equals(SpacePermission.VIEW)) {
      if (isNotEmpty(spaceAuths)) {
        return;
      }
      throw BizException.of(SPACE_NO_AUTH_CODE, SPACE_NO_AUTH, new Object[]{permission});
    }

    if (isCreator(spaceAuths)) {
      return;
    }
    if (!findSpaceAction(spaceAuths).contains(permission)) {
      throw BizException.of(SPACE_NO_AUTH_CODE, SPACE_NO_AUTH, new Object[]{permission});
    }
  }

  /**
   * Verify the operation permissions of the space
   * <p>
   *
   * @see SpaceAuthQueryImpl#isAdminUser(Long spaceId) Important: spaceIds must be space IDs under
   * the same application.
   */
  @Override
  public void batchCheckPermission(Collection<Long> spaceIds, SpacePermission permission) {
    if (isEmpty(spaceIds) || isAdminUser(spaceIds.stream().findFirst().get())
        || Objects.isNull(permission)) {
      return;
    }

    Collection<Long> authIds = permission.isGrant()
        ? spaceIds : spaceRepo.findIds0ByIdInAndAuth(spaceIds, true);
    if (isEmpty(authIds)) {
      return;
    }

    List<SpaceAuth> spaceAuths = findAuth(PrincipalContext.getUserId(), spaceIds);
    if (isEmpty(spaceAuths)) {
      long firstId = spaceIds.stream().findFirst().get();
      Space space = spaceRepo.findById(firstId).orElse(null);
      throw BizException.of(SPACE_NO_TARGET_AUTH_CODE, SPACE_NO_TARGET_AUTH,
          new Object[]{permission, Objects.isNull(space) ? firstId : space.getName()});
    }

    Map<Long, List<SpaceAuth>> spaceAuthMap = spaceAuths.stream()
        .filter(o -> nonNull(o.getSpaceId()))
        .collect(Collectors.groupingBy(SpaceAuth::getSpaceId));
    for (Long spaceId : spaceAuthMap.keySet()) {
      List<SpaceAuth> values = spaceAuthMap.get(spaceId);
      if (isNotEmpty(values)) {
        List<SpacePermission> spacePermissions = values.stream()
            .flatMap(o -> o.getAuths().stream()).collect(Collectors.toList());
        if (isNotEmpty(spacePermissions) && spacePermissions.contains(permission)) {
          continue;
        }
      }
      Space space = spaceRepo.findById(spaceId).orElse(null);
      throw BizException.of(SPACE_NO_TARGET_AUTH_CODE, SPACE_NO_TARGET_AUTH,
          new Object[]{permission, Objects.isNull(space) ? spaceId : space.getName()});
    }
  }

  @Override
  public void checkRepeatAuth(Long spaceId, Long authObjectId, AuthObjectType authObjectType) {
    if (spaceAuthRepo.countBySpaceIdAndAuthObjectIdAndAuthObjectType(spaceId, authObjectId,
        authObjectType) > 0) {
      throw ResourceExisted
          .of(String.valueOf(authObjectId), "Authorization:" + authObjectType.name());
    }
  }

  @Override
  public List<Long> findByAuthObjectIdsAndPermission(Long userId, SpacePermission permission) {
    List<Long> orgIds = userRepo.findOrgIdsById(userId);
    orgIds.add(userId);
    return spaceAuthRepo.findAllByAuthObjectIdIn(orgIds).stream()
        .filter(a -> a.getAuths().contains(permission)).map(SpaceAuth::getSpaceId)
        .collect(Collectors.toList());
  }

  @Override
  public List<SpacePermission> getUserAuth(Long spaceId, Long userId) {
    if (isAdminUser(spaceId)) {
      return SpacePermission.ALL;
    }

    List<SpaceAuth> auths = findAuth(userId, spaceId);
    if (isEmpty(auths)) {
      return null;
    }
    if (isCreator(auths)) {
      return SpacePermission.ALL;
    }
    return auths.stream().map(SpaceAuth::getAuths).flatMap(Collection::stream)
        .distinct().collect(Collectors.toList());
  }

  @Override
  public List<SpaceAuth> findAuth(Long userId, Long spaceId) {
    List<Long> orgIds = userRepo.findOrgIdsById(userId);
    orgIds.add(userId);
    return spaceAuthRepo.findAllBySpaceIdAndAuthObjectIdIn(spaceId, orgIds);
  }

  @Override
  public List<SpaceAuth> findAuth(Long userId, Collection<Long> spaceIds) {
    List<Long> orgIds = userRepo.findOrgIdsById(userId);
    orgIds.add(userId);
    return isEmpty(spaceIds) ? spaceAuthRepo.findAllByAuthObjectIdIn(orgIds)
        : spaceAuthRepo.findAllBySpaceIdInAndAuthObjectIdIn(spaceIds, orgIds);
  }

  @Override
  public boolean isCreator(Long userId, Long spaceId) {
    List<SpaceAuth> scenarioAuths = findAuth(userId, spaceId);
    return isCreator(scenarioAuths);
  }

  @Override
  public boolean isAdminUser(Long spaceId) {
    String appAdminCode = bucketBizConfigRepo.findAppAdminCodeBySpaceId(spaceId);
    if (isNotEmpty(appAdminCode)) {
      return hasPolicy(appAdminCode) || isTenantSysAdmin();
    }
    return isTenantSysAdmin();
  }

  @Override
  public boolean isAdminUserByBiz(String bizKey) {
    String appAdminCode = bucketBizConfigRepo.findAppAdminCodeByBizKey(bizKey);
    if (isNotEmpty(appAdminCode)) {
      return hasPolicy(appAdminCode) || isTenantSysAdmin();
    }
    return isTenantSysAdmin();
  }

  @Override
  public boolean isAdminUser(String appAdminCode) {
    return hasPolicy(appAdminCode) || isTenantSysAdmin();
  }

  private boolean isCreator(List<SpaceAuth> scenarioAuths) {
    if (scenarioAuths.isEmpty()) {
      return false;
    }
    for (SpaceAuth scenarioAuth : scenarioAuths) {
      if (scenarioAuth.getCreator()) {
        return true;
      }
    }
    return false;
  }

  private Set<SpacePermission> findSpaceAction(List<SpaceAuth> scenarioAuths) {
    Set<SpacePermission> actions = new HashSet<>();
    for (SpaceAuth spaceAuth : scenarioAuths) {
      actions.addAll(spaceAuth.getAuths());
    }
    return actions;
  }
}




