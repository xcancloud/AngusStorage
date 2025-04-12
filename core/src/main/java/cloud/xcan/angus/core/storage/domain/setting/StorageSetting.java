package cloud.xcan.angus.core.storage.domain.setting;

import cloud.xcan.angus.spec.experimental.EntitySupport;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Entity
@Table(name = "storage_setting")
@Setter
@Getter
@Accessors(chain = true)
public class StorageSetting extends EntitySupport<StorageSetting, Long> implements Serializable {

  @Id
  private Long id;

  @Enumerated(EnumType.STRING)
  private StorageSettingKey pkey;

  private String pvalue;

  public SettingData toSetting(ObjectMapper objectMapper) throws JsonProcessingException {
    return Objects.nonNull(pvalue) ? objectMapper.readValue(pvalue, SettingData.class) : null;
  }

  @Override
  public Long identity() {
    return this.id;
  }
}
