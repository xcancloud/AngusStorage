package cloud.xcan.angus.core.storage.application.converter;

import cloud.xcan.angus.api.enums.AuthObjectType;
import cloud.xcan.angus.core.storage.domain.space.auth.SpaceAuth;
import cloud.xcan.angus.core.storage.domain.space.auth.SpacePermission;
import cloud.xcan.angus.idgen.UidGenerator;

public class SpaceAuthConverter {

  public static SpaceAuth toCreatorAuth(Long spaceId, Long creatorId, UidGenerator uidGenerator) {
    return new SpaceAuth().setId(uidGenerator.getUID())
        .setSpaceId(spaceId)
        .setAuthObjectType(AuthObjectType.USER)
        .setAuthObjectId(creatorId)
        .setAuths(SpacePermission.ALL)
        .setCreator(true);
  }

}
