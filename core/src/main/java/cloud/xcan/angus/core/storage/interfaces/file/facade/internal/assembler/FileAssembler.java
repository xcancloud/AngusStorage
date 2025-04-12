package cloud.xcan.angus.core.storage.interfaces.file.facade.internal.assembler;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;

import cloud.xcan.angus.api.storage.file.vo.FileUploadVo;
import cloud.xcan.angus.core.storage.domain.file.ObjectFile;

public class FileAssembler {

  public static FileUploadVo toUploadVo(ObjectFile file) {
    return isNull(file) ? null :
        new FileUploadVo().setId(file.getId())
            .setName(file.getName())
            .setUrl(file.getDownloadUrl())
            .setUniqueName(file.getUniqueName())
            .setStoreAddress(file.getStoreAddress())
            .setStoreType(file.getStoreType());
  }

}

