package cloud.xcan.angus;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@EnableDiscoveryClient
@SpringBootApplication
public class XCanAngusStorageApplication {

  public static void main(String[] args) {
    SpringApplication.run(XCanAngusStorageApplication.class, args);
  }

}
