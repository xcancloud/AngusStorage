package cloud.xcan.angus.core.storage.application.query.space.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertNotNull;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceExisted;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.findFirstValue;
import static cloud.xcan.angus.core.storage.application.converter.SpaceObjectConverter.toSpaceObjectSummary;
import static cloud.xcan.angus.core.storage.domain.StorageMessage.OBJECT_DIRECTORY_NAME_EXISTED_T;
import static cloud.xcan.angus.spec.experimental.Assert.assertNotEmpty;
import static cloud.xcan.angus.spec.experimental.BizConstant.DEFAULT_ROOT_PID;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Objects.isNull;

import cloud.xcan.angus.api.enums.FileType;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.storage.application.query.bucket.BucketBizConfigQuery;
import cloud.xcan.angus.core.storage.application.query.file.ObjectFileQuery;
import cloud.xcan.angus.core.storage.application.query.space.SpaceAuthQuery;
import cloud.xcan.angus.core.storage.application.query.space.SpaceObjectQuery;
import cloud.xcan.angus.core.storage.application.query.space.SpaceQuery;
import cloud.xcan.angus.core.storage.domain.bucket.config.BucketBizConfig;
import cloud.xcan.angus.core.storage.domain.file.ObjectFile;
import cloud.xcan.angus.core.storage.domain.space.Space;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObject;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObjectListRepo;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObjectRepo;
import cloud.xcan.angus.core.storage.infra.store.utils.SpaceObjectCalculator;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Biz
public class SpaceObjectQueryImpl implements SpaceObjectQuery {

  @Resource
  private SpaceObjectRepo spaceObjectRepo;

  @Resource
  private SpaceObjectListRepo spaceObjectListRepo;

  @Resource
  private SpaceQuery spaceQuery;

  @Resource
  private SpaceAuthQuery spaceAuthQuery;

  @Resource
  private BucketBizConfigQuery bucketBizConfigQuery;

  @Resource
  private ObjectFileQuery objectFileQuery;

  @Override
  public SpaceObject detail(Long id) {
    return new BizTemplate<SpaceObject>() {
      SpaceObject objectDb;

      @Override
      protected void checkParams() {
        // Check the object existed
        objectDb = checkAndFind(id);
        // Check the read object permission
        spaceAuthQuery.checkObjectReadAuth(getUserId(), objectDb.getSpaceId());
      }

      @Override
      protected SpaceObject process() {
        setObjectStatsAndSummary(List.of(objectDb));
        objectDb.setSummary(toSpaceObjectSummary(objectDb));
        if (objectDb.isFile()) {
          associateFile(objectDb);
        }
        return objectDb;
      }
    }.execute();
  }

  @Override
  public SpaceObject navigation(Long id) {
    return new BizTemplate<SpaceObject>() {
      SpaceObject objectDb;

      @Override
      protected void checkParams() {
        // Check the object existed
        objectDb = checkAndFind(id);
        // Check the read object permission
        spaceAuthQuery.checkObjectReadAuth(getUserId(), objectDb.getSpaceId());
      }

      @Override
      protected SpaceObject process() {
        if (objectDb.hasParent()) {
          assertNotEmpty(objectDb.getParentLikeId(), "Data exception, parentLikeId is empty!");
          Collection<SpaceObject> parent = spaceObjectRepo.findByIdIn(
              Stream.of(objectDb.getParentLikeId().split("-"))
                  .map(Long::parseLong).collect(Collectors.toList()));
          objectDb.setParentChain(parent);
        }
        return objectDb;
      }
    }.execute();
  }

  @Override
  public SpaceObject address(Long id) {
    return new BizTemplate<SpaceObject>() {
      SpaceObject objectDb;

      @Override
      protected void checkParams() {
        // Check the object existed
        objectDb = checkAndFind(id);
        // Check the read object permission
        spaceAuthQuery.checkObjectReadAuth(getUserId(), objectDb.getSpaceId());
      }

      @Override
      protected SpaceObject process() {
        if (objectDb.isFile()) {
          associateFile(objectDb);
        }
        return objectDb;
      }
    }.execute();
  }

  @Override
  public Page<SpaceObject> find(GenericSpecification<SpaceObject> spec, PageRequest pageable) {
    return new BizTemplate<Page<SpaceObject>>() {
      @Override
      protected void checkParams() {
        String spaceId = findFirstValue(spec.getCriteria(), "spaceId");
        assertNotNull(spaceId, "spaceId is required");
        spaceQuery.check(Long.parseLong(spaceId));
        spaceAuthQuery.checkObjectReadAuth(getUserId(), Long.valueOf(spaceId));
      }

      @Override
      protected Page<SpaceObject> process() {
        Page<SpaceObject> page = spaceObjectListRepo.find(spec.getCriteria(),
            pageable, SpaceObject.class, null);
        setObjectStatsAndSummary(page.getContent());
        return page;
      }
    }.execute();
  }

  @Override
  public SpaceObject checkAndFind(Long id) {
    return spaceObjectRepo.findById(id)
        .orElseThrow(() -> ResourceNotFound.of(id, "SpaceObject"));
  }

  @Override
  public SpaceObject checkAndDirectory(Long id) {
    SpaceObject object = spaceObjectRepo.findById(id)
        .orElseThrow(() -> ResourceNotFound.of(id, "SpaceObject"));
    assertTrue(object.isDirectory(), String.format("Object[%s] is not a directory", id));
    return object;
  }

  @Override
  public void checkAddDirectoryNameExists(Long spaceId, Long parentDirectoryId, String name) {
    assertResourceExisted(spaceObjectRepo.countBySpaceIdAndParentDirectoryIdAndNameAndType(spaceId,
            parentDirectoryId, name, FileType.DIRECTORY) < 1, OBJECT_DIRECTORY_NAME_EXISTED_T,
        new Object[]{name});
  }

  @Override
  public void checkUpdateDirectoryNameExists(Long spaceId, Long parentDirectoryId,
      Long id, String name) {
    if (isEmpty(name)) {
      return;
    }
    assertResourceExisted(spaceObjectRepo.countBySpaceIdAndParentDirectoryIdAndNameAndIdNot(spaceId,
        parentDirectoryId, name, id) < 1, OBJECT_DIRECTORY_NAME_EXISTED_T, new Object[]{name});
  }

  @Override
  public SpaceObject checkAndFindMovedTargetObject(Long targetSpaceId, Long targetDirectoryId) {
    SpaceObject targetObject = checkAndFind(targetDirectoryId);
    assertTrue(FileType.DIRECTORY.equals(targetObject.getType()),
        "Error moving to file, can only move to directory");
    assertTrue(targetSpaceId.equals(targetObject.getSpaceId()),
        "Move to directory and space must in the same space");
    return targetObject;
  }

  @Override
  public List<SpaceObject> checkAndFind(Set<Long> objectIds) {
    List<SpaceObject> existedObjects = spaceObjectRepo.findAllById(objectIds);
    checkResourceExists(objectIds, existedObjects);
    return existedObjects;
  }

  @Override
  public List<SpaceObject> checkAndFind(Long spaceId, Set<Long> objectIds) {
    List<SpaceObject> existedObjects = spaceObjectRepo.findAllBySpaceIdAndIdIn(spaceId, objectIds);
    checkResourceExists(objectIds, existedObjects);
    return existedObjects;
  }

  @Override
  public void checkNestedDuplicates(List<SpaceObject> objectsDb) {
    Set<String> parentIds = objectsDb.stream()
        .filter(m -> !m.getParentDirectoryId().equals(DEFAULT_ROOT_PID))
        .map(SpaceObject::getParentLikeId).flatMap(x -> Stream.of(x.split("-")))
        .collect(Collectors.toSet());
    if (isNotEmpty(parentIds)) {
      SpaceObject hasDuplicate = objectsDb.stream()
          .filter(m -> parentIds.contains(String.valueOf(m.getId()))).findFirst().orElse(null);
      assertTrue(isNull(hasDuplicate), String.format("Object %s is nested duplicates",
          isNull(hasDuplicate) ? "" : hasDuplicate.getId()));
    }
  }

  @Override
  public void associateFile(SpaceObject objectDb) {
    ObjectFile file = objectFileQuery.checkAndFind(objectDb.getFid());
    BucketBizConfig bizConfig = bucketBizConfigQuery.findByBizKey(file.getBizKey());
    file.setDownloadUrl(objectFileQuery.assembleDownloadUrl(objectDb.getFid(),
        objectDb.getName(), bizConfig, file.getPublicToken()));
    objectDb.setFile(file);
  }

  @Override
  public void setSpaceStats(List<Space> spaces) {
    if (isNotEmpty(spaces)) {
      List<SpaceObject> spaceObjects = spaceObjectRepo.findAllBySpaceIdIn(
          spaces.stream().map(Space::getId).collect(Collectors.toSet()));
      Map<Long, List<SpaceObject>> spaceObjectsMap = spaceObjects.stream()
          .collect(Collectors.groupingBy(SpaceObject::getSpaceId));
      for (Space space : spaces) {
        List<SpaceObject> objects = spaceObjectsMap.get(space.getId());
        if (isNotEmpty(objects)) {
          space.setSize(objects.stream().filter(SpaceObject::isFile)
              .map(SpaceObject::getSize).reduce(0L, Long::sum));
          space.setSubDirectoryNum(
              objects.stream().filter(SpaceObject::isDirectory).toList().size());
          space.setSubFileNum(objects.stream().filter(SpaceObject::isFile).toList().size());
        }
      }
    }
  }

  @Override
  public void setObjectStats(List<SpaceObject> objects) {
    if (isNotEmpty(objects)) {
      if (objects.size() == 1) {
        SpaceObject object = objects.get(0);
        if (object.isDirectory()) {
          List<SpaceObject> allObjects = new ArrayList<>(objects);
          List<SpaceObject> dirSubObjects = spaceObjectRepo.findByParentLikeId(
              object.getId().toString());
          if (isNotEmpty(dirSubObjects)) {
            allObjects.addAll(dirSubObjects);
          }
          SpaceObjectCalculator.computeStats(allObjects);
        }/*else {
          // isFile -> None calculate count
        }*/
      } else {
        List<SpaceObject> allObjects = spaceObjectRepo.findAllBySpaceIdIn(
            objects.stream().map(SpaceObject::getSpaceId).collect(Collectors.toSet()));
        SpaceObjectCalculator.computeStats(allObjects);
      }
    }
  }

  @Override
  public void setObjectStatsAndSummary(List<SpaceObject> objects) {
    if (isNotEmpty(objects)) {
      setObjectStats(objects);
      for (SpaceObject object : objects) {
        object.setSummary(toSpaceObjectSummary(object));
      }
    }
  }

  private void checkResourceExists(Set<Long> objectIds, List<SpaceObject> existedObjects) {
    if (objectIds.size() != existedObjects.size()) {
      if (isNotEmpty(existedObjects)) {
        objectIds.removeAll(existedObjects.stream().map(SpaceObject::getId)
            .collect(Collectors.toSet()));
      }
      throw ResourceNotFound.of(objectIds.stream().findFirst().get(), "SpaceObject");
    }
  }

}
