package cloud.xcan.angus.config;

import cloud.xcan.angus.core.utils.SpringAppDirUtils;
import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

  @Bean
  Logger.Level feignLoggerLevel() {
    return Logger.Level.FULL;
  }

  @Bean
  public SpringAppDirUtils springAppDirUtils() {
    return new SpringAppDirUtils();
  }
}
