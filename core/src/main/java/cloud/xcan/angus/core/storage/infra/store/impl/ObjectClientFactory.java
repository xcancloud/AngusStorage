package cloud.xcan.angus.core.storage.infra.store.impl;

import cloud.xcan.angus.api.enums.PlatformStoreType;
import cloud.xcan.angus.core.spring.SpringContextHolder;
import cloud.xcan.angus.core.storage.infra.store.ObjectClient;
import cloud.xcan.angus.core.storage.infra.store.ObjectProperties;
import java.util.Map;

public class ObjectClientFactory {

  public static ObjectClient current() {
    Map<String, ObjectClient> objectClientMap = SpringContextHolder.getCtx()
        .getBeansOfType(ObjectClient.class);
    ObjectProperties properties = SpringContextHolder.getBean(ObjectProperties.class);
    return objectClientMap.values().stream()
        .filter(x -> properties.getStoreType().equals(x.getStoreType()))
        .findFirst().orElseThrow(() -> new IllegalStateException(properties.getStoreType()
            + " client not found"));
  }

  public static ObjectClient of(PlatformStoreType storeType) {
    Map<String, ObjectClient> objectClientMap = SpringContextHolder.getCtx()
        .getBeansOfType(ObjectClient.class);
    return objectClientMap.values().stream()
        .filter(x -> storeType.equals(x.getStoreType()))
        .findFirst().orElseThrow(() -> new IllegalStateException(storeType.getValue()
            + " client not found"));
  }

  public static Map<String, ObjectClient> all() {
    return SpringContextHolder.getCtx().getBeansOfType(ObjectClient.class);
  }

}
