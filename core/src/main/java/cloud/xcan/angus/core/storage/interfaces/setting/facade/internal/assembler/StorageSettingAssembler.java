package cloud.xcan.angus.core.storage.interfaces.setting.facade.internal.assembler;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.core.storage.domain.setting.SettingData;
import cloud.xcan.angus.core.storage.interfaces.setting.facade.dto.StorageSettingReplaceDto;
import cloud.xcan.angus.core.storage.interfaces.setting.facade.vo.StorageSettingDetailVo;

public class StorageSettingAssembler {

  public static SettingData toSettingData(StorageSettingReplaceDto dto) {
    return new SettingData()
        .setStoreType(dto.getStoreType())
        .setProxyAddress(dto.getProxyAddress())
        .setAesKey(dto.getAesKey())
        .setLocalDir(dto.getLocalDir())
        .setEndpoint(dto.getEndpoint())
        .setRegion(dto.getRegion())
        .setAccessKey(dto.getAccessKey())
        .setSecretKey(dto.getSecretKey())
        .setForce(dto.getForce());
  }

  public static StorageSettingDetailVo toSettingDetailTo(SettingData setting) {
    StorageSettingDetailVo detailVo = new StorageSettingDetailVo()
        .setStoreType(setting.getStoreType())
        .setProxyAddress(setting.getProxyAddress())
        .setAesKey(setting.getAesKey())
        .setLocalDir(setting.getLocalDir())
        .setEndpoint(setting.getEndpoint())
        .setRegion(setting.getRegion())
        .setAccessKey(setting.getAccessKey())
        .setSecretKey(setting.getSecretKey());
    // Set default proxy address
    if (isEmpty(detailVo.getProxyAddress())) {
      detailVo.setProxyAddress(setting.getDefaultProxyAddress());
    }
    return detailVo;
  }

}
