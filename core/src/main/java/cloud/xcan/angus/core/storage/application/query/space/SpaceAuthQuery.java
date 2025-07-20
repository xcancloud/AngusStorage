package cloud.xcan.angus.core.storage.application.query.space;

import cloud.xcan.angus.api.enums.AuthObjectType;
import cloud.xcan.angus.core.storage.domain.space.auth.SpaceAuth;
import cloud.xcan.angus.core.storage.domain.space.auth.SpaceAuthCurrent;
import cloud.xcan.angus.core.storage.domain.space.auth.SpacePermission;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface SpaceAuthQuery {

  Boolean status(Long spaceId);

  List<SpacePermission> userAuth(Long spaceId, Long userId, Boolean admin);

  SpaceAuthCurrent currentUserAuth(Long spaceId, Boolean admin);

  Map<Long, SpaceAuthCurrent> currentUserAuths(HashSet<Long> spaceIds, Boolean admin);

  void check(Long spaceId, SpacePermission authPermission, Long userId);

  Page<SpaceAuth> find(Specification<SpaceAuth> spec, List<String> spaceIds,
      Pageable pageable);

  SpaceAuth checkAndFind(Long id);

  /**
   * Whether the user has permission to view the space.
   *
   * @param userId  grant user id
   * @param spaceId grant space id
   * @throws cloud.xcan.angus.core.biz.exception.BizException If not grant
   *                                                          {@link SpacePermission#VIEW}
   */
  void checkViewAuth(Long userId, Long spaceId);

  /**
   * Whether the user has permission to modify the space.
   *
   * @param userId  grant user id
   * @param spaceId grant space id
   * @throws cloud.xcan.angus.core.biz.exception.BizException If not grant
   *                                                          {@link SpacePermission#MODIFY}
   */
  void checkModifyAuth(Long userId, Long spaceId);

  /**
   * Whether the user has the permission to delete.
   *
   * @param userId  grant user id
   * @param spaceId grant space id
   * @throws cloud.xcan.angus.core.biz.exception.BizException If not grant
   *                                                          {@link SpacePermission#DELETE}
   */
  void checkDeleteAuth(Long userId, Long spaceId);

  /**
   * Whether the user has the permission to grant other user.
   *
   * @param userId  grant user id
   * @param spaceId grant space id
   * @throws cloud.xcan.angus.core.biz.exception.BizException If not grant
   *                                                          {@link SpacePermission#GRANT}
   */
  void checkGrantAuth(Long userId, Long spaceId);

  /**
   * Whether the user has the permission to shared.
   *
   * @param userId  grant user id
   * @param spaceId grant space id
   * @throws cloud.xcan.angus.core.biz.exception.BizException If not grant
   *                                                          {@link SpacePermission#SHARE}
   */
  void checkShareAuth(Long userId, Long spaceId);

  void checkObjectReadAuth(Long userId, Long spaceId);

  void checkObjectWriteAuth(Long userId, Long spaceId);

  void checkObjectDeletedAuth(Long userId, Long spaceId);

  void checkAuth(Long userId, Long spaceId, SpacePermission permission);

  void batchCheckPermission(Collection<Long> spaceIds, SpacePermission permission);

  void checkRepeatAuth(Long spaceId, Long authObjectId, AuthObjectType authObjectType);

  List<Long> findByAuthObjectIdsAndPermission(Long userId, SpacePermission permission);

  List<SpacePermission> getUserAuth(Long spaceId, Long userId);

  List<SpaceAuth> findAuth(Long userId, Long scriptId);

  List<SpaceAuth> findAuth(Long userId, Collection<Long> scriptIds);

  /**
   * Whether the user is the space creator
   *
   * @param userId  grant user id
   * @param spaceId grant space id
   * @return Returns true if the space creator, otherwise returns false
   */
  boolean isCreator(Long userId, Long spaceId);

  boolean isAdminUser(Long spaceId);

  boolean isAdminUserByBiz(String bizKey);

  boolean isAdminUser(String appAdminCode);

}




