package cloud.xcan.angus.core.storage.interfaces.space.facade.vo.auth;

import cloud.xcan.angus.remote.NameJoinField;

public class SpaceAuthUserDetailVo extends SpaceAuthDetailVo {

  @NameJoinField(id = "authObjectId", repository = "commonUserBaseRepo")
  private String name;

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }
}
