package cloud.xcan.angus.api.commonlink;

import cloud.xcan.angus.spec.locale.EnumValueMessage;

public enum CompressFormat implements EnumValueMessage<String> {
  bzip2, gzip, jar, tar, zip, dump, _7z;

  @Override
  public String getValue() {
    return this.name();
  }

}
