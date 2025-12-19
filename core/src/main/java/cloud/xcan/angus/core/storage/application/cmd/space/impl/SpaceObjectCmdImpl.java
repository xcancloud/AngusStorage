package cloud.xcan.angus.core.storage.application.cmd.space.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.spec.experimental.BizConstant.DEFAULT_ROOT_PID;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.storage.application.cmd.space.SpaceObjectCmd;
import cloud.xcan.angus.core.storage.application.query.space.SpaceAuthQuery;
import cloud.xcan.angus.core.storage.application.query.space.SpaceObjectQuery;
import cloud.xcan.angus.core.storage.application.query.space.SpaceQuery;
import cloud.xcan.angus.core.storage.domain.file.ObjectFileRepo;
import cloud.xcan.angus.core.storage.domain.space.Space;
import cloud.xcan.angus.core.storage.domain.space.auth.SpacePermission;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObject;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObjectRepo;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service
public class SpaceObjectCmdImpl extends CommCmd<SpaceObject, Long> implements SpaceObjectCmd {

  @Resource
  private SpaceObjectRepo spaceObjectRepo;

  @Resource
  private SpaceObjectQuery spaceObjectQuery;

  @Resource
  private ObjectFileRepo objectFileRepo;

  @Resource
  private SpaceQuery spaceQuery;

  @Resource
  private SpaceAuthQuery spaceAuthQuery;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> directoryAdd(SpaceObject directory) {
    return new BizTemplate<IdKey<Long, Object>>() {
      Space spaceDb;
      SpaceObject parentDirectoryDb;

      @Override
      protected void checkParams() {
        // Check the space existed
        spaceDb = spaceQuery.checkAndFind(directory.getSpaceId());
        // Check the write object permission
        spaceAuthQuery.checkObjectWriteAuth(getUserId(), directory.getSpaceId());
        // Check and find parent
        if (directory.hasParent()) {
          parentDirectoryDb = spaceObjectQuery.checkAndDirectory(directory.getParentDirectoryId());
          assertTrue(parentDirectoryDb.getSpaceId().equals(directory.getSpaceId()),
              "The parentDirectoryId and spaceId must be in the same space");
        }
        // Check the directory name existed
        spaceObjectQuery.checkAddDirectoryNameExists(directory.getSpaceId(),
            directory.getParentDirectoryId(), directory.getName());
        // TODO Check directory num and level quota
      }

      @Override
      protected IdKey<Long, Object> process() {
        // Calc level, likeId
        directory.setLevel(nonNull(parentDirectoryDb) ? parentDirectoryDb.getLevel() + 1 : 1);
        // All parent-child folder ID symbols are connected by "-" (up to 10 levels are supported for 200 characters)
        directory.setParentLikeId(nonNull(parentDirectoryDb) ? (
            isNotEmpty(parentDirectoryDb.getParentLikeId())
                ? parentDirectoryDb.getParentLikeId() + "-" + parentDirectoryDb.getId()
                : String.valueOf(parentDirectoryDb.getId())
        ) : "");
        directory.setProjectId(spaceDb.getProjectId());
        return insert(directory);
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void rename(Long id, String name) {
    new BizTemplate<Void>() {
      SpaceObject spaceObjectDb;

      @Override
      protected void checkParams() {
        // Check the space existed
        spaceObjectDb = spaceObjectQuery.checkAndFind(id);
        // Check the write object permission
        spaceAuthQuery.checkObjectWriteAuth(getUserId(), spaceObjectDb.getSpaceId());
        // Check the directory name existed
        if (!name.equals(spaceObjectDb.getName())) {
          // Check the update name exist
          spaceObjectQuery.checkUpdateDirectoryNameExists(spaceObjectDb.getSpaceId(),
              spaceObjectDb.getParentDirectoryId(), id, name);
        }
      }

      @Override
      protected Void process() {
        if (!name.equals(spaceObjectDb.getName())) {
          spaceObjectDb.setName(name);
          spaceObjectRepo.save(spaceObjectDb);
        }
        return null;
      }
    }.execute();
  }

  /**
   * Allow moving different space objects.
   * <p>
   * There may be duplicate names after the move!!!
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void move(Set<Long> objectIds, Long targetSpaceId, Long targetDirId) {
    new BizTemplate<Void>() {
      Space spaceDb;
      SpaceObject targetDirectoryDb;
      List<SpaceObject> movedObjects;
      final boolean hasTargetDir = nonNull(targetDirId) && !targetDirId.equals(DEFAULT_ROOT_PID);
      Set<Long> allTargetDirParentIds;

      @Override
      protected void checkParams() {
        // Check the space existed
        spaceDb = spaceQuery.checkAndFind(targetSpaceId);
        // Check the object existed
        movedObjects = spaceObjectQuery.checkAndFind(objectIds);
        // Check the write target space permission
        spaceAuthQuery.checkObjectWriteAuth(getUserId(), targetSpaceId);
        // Check the moved object write space permission
        spaceAuthQuery.batchCheckPermission(movedObjects.stream().map(SpaceObject::getSpaceId)
            .collect(Collectors.toSet()), SpacePermission.OBJECT_WRITE);
        // Check the target directory existed
        if (hasTargetDir) {
          targetDirectoryDb = spaceObjectQuery.checkAndFindMovedTargetObject(targetSpaceId,
              targetDirId);
        }
        // Check the position has not changed
        checkPositionNotChanged();
        // Check the nested duplicates
        spaceObjectQuery.checkNestedDuplicates(movedObjects);
      }

      @Override
      protected Void process() {
        Map<Long, Map<Long, List<SpaceObject>>> movedSpaceParentMap = movedObjects.stream()
            .collect(Collectors.groupingBy(SpaceObject::getSpaceId,
                Collectors.groupingBy(SpaceObject::getParentDirectoryId)));
        String targetParentLikeId = getTargetParentLikeId();
        if (hasTargetDir) {
          allTargetDirParentIds = getAllTargetDirParentIds();
        }
        for (Long movedSpaceId : movedSpaceParentMap.keySet()) {
          Map<Long, List<SpaceObject>> movedParentMap = movedSpaceParentMap.get(movedSpaceId);
          for (Long movedParentId : movedParentMap.keySet()) {
            List<SpaceObject> movedObjects = movedParentMap.get(movedParentId);
            for (SpaceObject movedObject : movedObjects) {
              if (movedObject.hasSubObject()) {
                // Modify(replace) new parentLikeId of sub object
                String oldSubParentLikeId = movedObject.hasParent() ?
                    movedObject.getParentLikeId() + "-" + movedObject.getId() :
                    String.valueOf(movedObject.getId());
                String newSubParentLikeId = isNotEmpty(targetParentLikeId) ?
                    targetParentLikeId + "-" + movedObject.getId() :
                    String.valueOf(movedObject.getId());
                int newDiffLevel = newSubParentLikeId.split("-").length
                    - oldSubParentLikeId.split("-").length;
                spaceObjectRepo.updateSubParentByOldParentLikeId(targetSpaceId, newDiffLevel,
                    oldSubParentLikeId, newSubParentLikeId);
              }

              // Modify the level,spaceId,parentId, sub object likeId of moved object
              movedObject.setLevel(hasTargetDir ? targetDirectoryDb.getLevel() + 1 : 1);
              movedObject.setSpaceId(targetSpaceId);
              movedObject.setParentDirectoryId(hasTargetDir ? targetDirId : DEFAULT_ROOT_PID);
              movedObject.setParentLikeId(targetParentLikeId);
            }

            // Noteworthy:: If this method is not used, the movedObject will be automatically updated before the next query (findParentLikeIdById)
            spaceObjectRepo.saveAll(movedObjects);
          }
        }
        return null;
      }

      private String getTargetParentLikeId() {
        return hasTargetDir ? (targetDirectoryDb.hasParent() ? targetDirectoryDb.getParentLikeId()
            + "-" + targetDirId : String.valueOf(targetDirId)) : "";
      }

      @NotNull
      private Set<Long> getAllTargetDirParentIds() {
        Set<Long> allParentIds = new HashSet<>();
        allParentIds.add(targetDirId);
        if (targetDirectoryDb.hasParent()) {
          String parentLikeId = spaceObjectRepo.findParentLikeIdById(targetDirId)
              .orElseThrow(() -> ResourceNotFound.of(targetDirId, "movedTargetParent"));
          if (isNotEmpty(parentLikeId)) {
            allParentIds.addAll(Stream.of(parentLikeId.split("-")).map(Long::parseLong).toList());
          }
        }
        return allParentIds;
      }

      private void checkPositionNotChanged() {
        long safeTargetDirId = isNull(targetDirId) ? DEFAULT_ROOT_PID : targetDirId;
        SpaceObject hasNotChange = movedObjects.stream().filter(o -> o.getParentDirectoryId()
                .equals(safeTargetDirId) && o.getSpaceId().equals(targetSpaceId)).findFirst()
            .orElse(null);
        assertTrue(isNull(hasNotChange), String.format("Object %s position has not changed",
            isNull(hasNotChange) ? "" : hasNotChange.getId()));
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(HashSet<Long> ids) {
    new BizTemplate<Void>() {
      List<SpaceObject> objectsDb;

      @Override
      protected void checkParams() {
        objectsDb = spaceObjectRepo.findAllById(ids);
        // Ignore when no objects in db
        if (isEmpty(objectsDb)) {
          return;
        }
        // Check the delete object permission
        spaceAuthQuery.batchCheckPermission(objectsDb.stream().map(SpaceObject::getSpaceId)
            .collect(Collectors.toSet()), SpacePermission.OBJECT_DELETE);
        // Check the nested duplicates
        spaceObjectQuery.checkNestedDuplicates(objectsDb);
      }

      @Override
      protected Void process() {
        // Ignore when no objects in db
        if (isEmpty(objectsDb)) {
          return null;
        }
        Map<Long, Map<Long, List<SpaceObject>>> deletedSpaceParentMap
            = objectsDb.stream().collect(Collectors.groupingBy(SpaceObject::getSpaceId,
            Collectors.groupingBy(SpaceObject::getParentDirectoryId)));
        List<Long> allDeletedObjectIds = new ArrayList<>();
        for (Long deletedSpaceId : deletedSpaceParentMap.keySet()) {
          Map<Long, List<SpaceObject>> deletedParentMap = deletedSpaceParentMap.get(deletedSpaceId);
          for (Long deletedParentId : deletedParentMap.keySet()) {
            List<SpaceObject> deletedObjects = deletedParentMap.get(deletedParentId);
            for (SpaceObject deletedObject : deletedObjects) {
              allDeletedObjectIds.add(deletedObject.getId());
              if (deletedObject.hasSubObject()) {
                // Query sub object ids
                String subParentLikeId = deletedObject.hasParent() ?
                    deletedObject.getParentLikeId() + "-" + deletedObject.getId() :
                    String.valueOf(deletedObject.getId());
                List<Long> subIds = spaceObjectRepo.findIdByParentLikeId(subParentLikeId);
                if (isNotEmpty(subIds)) {
                  allDeletedObjectIds.addAll(subIds);
                }
              }
            }
          }
        }

        // Update file to be deleted(store_deleted = 1)
        objectFileRepo.updateToBeDeleted(allDeletedObjectIds);

        // Delete space object
        if (isNotEmpty(allDeletedObjectIds)) {
          spaceObjectRepo.deleteByIdIn(allDeletedObjectIds);
        }
        return null;
      }
    }.execute();
  }

  @Override
  public void fileAdd0(Long spaceId, SpaceObject parentDirectoryDb, List<SpaceObject> objects) {
    batchInsert(objects);
  }

  @Override
  protected BaseRepository<SpaceObject, Long> getRepository() {
    return spaceObjectRepo;
  }

}
