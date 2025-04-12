package cloud.xcan.angus.core.storage.infra.job;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.core.job.JobTemplate;
import cloud.xcan.angus.core.storage.domain.file.ObjectFile;
import cloud.xcan.angus.core.storage.domain.file.ObjectFileRepo;
import cloud.xcan.angus.core.storage.infra.store.impl.ObjectClientFactory;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FileStoreDeletedJob {

  private static final String LOCK_3M_KEY = "job:storage:StoreDeletedJob_interval3m";
  private static final String LOCK_2H_KEY = "job:storage:StoreDeletedJob_interval2h";

  private static final Long COUNT = 200L;
  private static final int MAX_RETRY_NUM = 30;

  @Resource
  private JobTemplate jobTemplate;

  @Resource
  private ObjectFileRepo objectFileRepo;

  /**
   * Delete the latest file every 3 minutes.
   */
  // @Transactional(rollbackFor = Exception.class) // Use internal code transactions
  @Scheduled(cron = "0 0/3 * * * ?") // Executed every 5 minutes
  public void deleteLatest() {
    jobTemplate.execute(LOCK_3M_KEY, 10, TimeUnit.MINUTES, () -> {
      List<ObjectFile> objectFilesDb;
      do {
        objectFilesDb = objectFileRepo.findStoreDeletedInFirst(COUNT);
        if (isNotEmpty(objectFilesDb)) {
          batchDeleteAndUpdate(objectFilesDb);
        }
      } while (isNotEmpty(objectFilesDb) && objectFilesDb.size() == COUNT);
    });
  }

  /**
   * Delete the failure deleted file every 2 hours, max retry {@value MAX_RETRY_NUM}.
   */
  // @Transactional(rollbackFor = Exception.class) // Use internal code transactions
  @Scheduled(cron = "0 0 0/2 * * ?")
  public void deleteFailure() {
    jobTemplate.execute(LOCK_2H_KEY, 10, TimeUnit.MINUTES, () -> {
      List<ObjectFile> objectFilesDb;
      do {
        objectFilesDb = objectFileRepo.findStoreDeletedRetryLess(MAX_RETRY_NUM, COUNT);
        if (isNotEmpty(objectFilesDb)) {
          batchDeleteAndUpdate(objectFilesDb);
        }
      } while (isNotEmpty(objectFilesDb) && objectFilesDb.size() == COUNT);
    });
  }

  private void batchDeleteAndUpdate(List<ObjectFile> objectFilesDb) {
    List<ObjectFile> deleteStoreSuccess = new ArrayList<>();
    List<ObjectFile> deleteStoreFailure = new ArrayList<>();
    for (ObjectFile objectFileDb : objectFilesDb) {
      try {
        // No error when storage object is deleted
        ObjectClientFactory.of(objectFileDb.getStoreType())
            .removeObject(objectFileDb.getBucketName(), objectFileDb.getPath());
        deleteStoreSuccess.add(objectFileDb);
      } catch (Exception e) {
        log.error("StoreDeletedJob_interval#inner execute fail, ID:{}, cause:{}",
            objectFileDb.getId(), e.getMessage());
        objectFileDb.setDeletedRetryNum(objectFileDb.getDeletedRetryNum() + 1);
        deleteStoreFailure.add(objectFileDb);
      }
    }
    if (isNotEmpty(deleteStoreSuccess)) {
      // Use internal transactions
      objectFileRepo.deleteInBatch(deleteStoreSuccess);
    }
    if (isNotEmpty(deleteStoreFailure)) {
      // Use internal transactions
      objectFileRepo.batchUpdate(deleteStoreFailure);
    }
  }

}
