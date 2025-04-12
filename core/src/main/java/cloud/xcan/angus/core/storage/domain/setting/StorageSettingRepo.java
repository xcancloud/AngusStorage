package cloud.xcan.angus.core.storage.domain.setting;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface StorageSettingRepo extends BaseRepository<StorageSetting, Long> {

  StorageSetting findByPkey(StorageSettingKey pkey);

  @Modifying
  @Query("update StorageSetting t set t.pvalue = ?2 where t.pkey =?1")
  int updateValueByKey(StorageSettingKey key, String value);

}
