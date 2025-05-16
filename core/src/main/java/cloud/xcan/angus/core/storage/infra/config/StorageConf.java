package cloud.xcan.angus.core.storage.infra.config;

import cloud.xcan.angus.core.storage.infra.store.ObjectProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ObjectProperties.class})
public class StorageConf {

}
