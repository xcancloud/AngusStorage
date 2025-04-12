package cloud.xcan.angus.core.storage.infra.store.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

@Component
public class FileBytesCache {

  private static final int CACHE_MAX_FILE_SIZE = 200 * 1024 * 1024;

  private final Cache<Long, byte[]> eventTemplatesCache;

  public FileBytesCache() {
    this.eventTemplatesCache = CacheBuilder.newBuilder()
        .expireAfterWrite(60, TimeUnit.MINUTES)
        .initialCapacity(100).maximumSize(500)
        .build();
  }

  public void cacheFileBytes(Long fid, byte[] bytes) {
    if (canCacheFileBytes(bytes.length)) {
      this.eventTemplatesCache.put(fid, bytes);
    }
  }

  public byte[] getFileBytes(Long fid) {
    return this.eventTemplatesCache.getIfPresent(fid);
  }

  public void clearFileBytes(Long fid) {
    this.eventTemplatesCache.invalidate(fid);
  }

  public boolean canCacheFileBytes(int size) {
    if (size >= CACHE_MAX_FILE_SIZE) {
      return false;
    }
    Runtime runtime = Runtime.getRuntime();
    long freeMemory = runtime.freeMemory();
    long totalMemory = runtime.totalMemory();
    return (freeMemory + size) / (double) totalMemory > 0.6d;
  }
}
