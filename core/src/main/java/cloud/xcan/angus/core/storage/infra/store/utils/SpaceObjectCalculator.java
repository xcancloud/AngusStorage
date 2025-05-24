package cloud.xcan.angus.core.storage.infra.store.utils;

import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.enums.FileType;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpaceObjectCalculator {

  /**
   * The number of subdirectories and sub-files in a directory is only calculated at one level.
   */
  public static void computeStats(List<SpaceObject> objects) {
    List<SpaceObject> allDir = objects.stream().filter(SpaceObject::isDirectory).toList();
    Map<Long, List<SpaceObject>> dirAllSubs = new HashMap<>();
    for (SpaceObject dir : allDir) {
      dirAllSubs.put(dir.getId(), objects.stream()
          .filter(x -> nonNull(x.getParentLikeId()) && x.getParentLikeId()
              .contains(x.getId().toString()))
          .toList());
    }

    for (SpaceObject obj : objects) {
      if (FileType.FILE.equals(obj.getType())) {
        obj.setSize(obj.getSize());
        obj.setSubDirectoryNum(0).setSubFileNum(0);
      } else {
        List<SpaceObject> children = dirAllSubs.getOrDefault(obj.getId(), Collections.emptyList());
        int fileCount = (int) children.stream().filter(SpaceObject::isFile).count();
        int dirCount = (int) children.stream().filter(SpaceObject::isDirectory).count();
        long totalSize = children.stream().filter(SpaceObject::isFile).map(SpaceObject::getSize)
            .reduce(Long::sum).orElse(0L);
        obj.setSize(totalSize);
        obj.setSubDirectoryNum(dirCount).setSubFileNum(fileCount);
      }
    }
  }

}
