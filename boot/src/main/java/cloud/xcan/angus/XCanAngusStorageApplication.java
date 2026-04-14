package cloud.xcan.angus;

import cloud.xcan.angus.persistence.jpa.annotation.AngusSpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableFeignClients(basePackages = {
    "cloud.xcan.angus.api.storage",
    "cloud.xcan.angus.api.gm",
    "cloud.xcan.angus.security"
})
@AngusSpringBootApplication
public class XCanAngusStorageApplication {

  public static void main(String[] args) {
    SpringApplication.run(XCanAngusStorageApplication.class, args);
  }

}
