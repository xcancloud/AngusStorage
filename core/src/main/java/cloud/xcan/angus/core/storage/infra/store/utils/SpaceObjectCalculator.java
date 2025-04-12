package cloud.xcan.angus.core.storage.infra.store.utils;

import cloud.xcan.angus.api.enums.FileType;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObject;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SpaceObjectCalculator {

  /**
   * The number of subdirectories and sub-files in a directory is only calculated at one level.
   */
  public static void computeStats(List<SpaceObject> objects) {
    Map<Long, List<SpaceObject>> parentToChildren = objects.stream()
        .collect(Collectors.groupingBy(
            obj -> obj.getParentDirectoryId() != null ? obj.getParentDirectoryId() : 0L
        ));

    Map<Long, Long> sizeCache = new HashMap<>();

    for (SpaceObject obj : objects) {
      if (FileType.FILE.equals(obj.getType())) {
        obj.setSize(obj.getSize());
        obj.setSubDirectoryNum(0).setSubFileNum(0);
      } else {
        List<SpaceObject> children = parentToChildren.getOrDefault(obj.getId(),
            Collections.emptyList());
        int fileCount = (int) children.stream()
            .filter(c -> FileType.FILE.equals(c.getType())).count();
        int dirCount = (int) children.stream()
            .filter(c -> FileType.DIRECTORY.equals(c.getType())).count();

        long totalSize = calcDirectorySize(obj, parentToChildren, sizeCache);
        obj.setSize(totalSize);
        obj.setSubDirectoryNum(dirCount).setSubFileNum(fileCount);
      }
    }
  }

  private static long calcDirectorySize(SpaceObject directory,
      Map<Long, List<SpaceObject>> parentToChildren, Map<Long, Long> sizeCache) {
    Long dirId = directory.getId();
    if (sizeCache.containsKey(dirId)) {
      return sizeCache.get(dirId);
    }

    long totalSize = 0;
    List<SpaceObject> children = parentToChildren.getOrDefault(dirId, Collections.emptyList());
    for (SpaceObject child : children) {
      if (FileType.FILE.equals(child.getType())) {
        totalSize += child.getSize();
      } else {
        totalSize += calcDirectorySize(child, parentToChildren, sizeCache);
      }
    }

    sizeCache.put(dirId, totalSize);
    return totalSize;
  }

  public static void main(String[] args) {
    List<SpaceObject> objects = Arrays.asList(
        new SpaceObject(1L, FileType.DIRECTORY, null, 0L),
        new SpaceObject(2L, FileType.FILE, 1L, 100L),
        new SpaceObject(3L, FileType.DIRECTORY, 1L, 0L),
        new SpaceObject(4L, FileType.FILE, 3L, 200L),
        new SpaceObject(5L, FileType.DIRECTORY, 3L, 0L),
        new SpaceObject(6L, FileType.FILE, 5L, 300L),
        new SpaceObject(7L, FileType.DIRECTORY, 5L, 300L),
        new SpaceObject(8L, FileType.FILE, 5L, 300L),
        new SpaceObject(9L, FileType.FILE, null, 200L)
    );

    SpaceObjectCalculator.computeStats(objects);

    objects.forEach(stats ->
        System.out.printf(
            "ID: %d | Size: %d | Sub Files: %d | Sub Dirs: %d%n",
            stats.getId(),
            stats.getSize(),
            stats.getSubFileNum(),
            stats.getSubDirectoryNum()
        )
    );
  }
}
