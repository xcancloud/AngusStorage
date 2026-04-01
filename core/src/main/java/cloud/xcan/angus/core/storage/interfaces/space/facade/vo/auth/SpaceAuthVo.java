package cloud.xcan.angus.core.storage.interfaces.space.facade.vo.auth;


import cloud.xcan.angus.spec.ValueObject;
import java.io.Serializable;
import java.util.List;

public interface SpaceAuthVo extends Serializable {

  Long getId();

  String getAuthObjectType();

  Long getAuthObjectId();

  String getName();

  List<? extends ValueObject<?>> getPermissions();

  void setId(Long id);

  void setAuthObjectType(String authObjectType);

  void setAuthObjectId(Long authObjectId);

  void setName(String name);

  void setPermissions(List<? extends ValueObject<?>> permissions);

  Boolean getCreator();

  void setCreator(Boolean creator);

  Long getSpaceId();

  void setSpaceId(Long spaceId);
}



