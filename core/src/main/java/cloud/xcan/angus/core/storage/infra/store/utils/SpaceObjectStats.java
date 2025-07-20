package cloud.xcan.angus.core.storage.infra.store.utils;

public interface SpaceObjectStats<T extends SpaceObjectStats> {

  long getSize();

  T setSize(long totalSize);

  int getSubFileNum();

  T setSubFileNum(int subFileNum);

  int getSubDirectoryNum();

  T setSubDirectoryNum(int subDirectoryNum);

}
