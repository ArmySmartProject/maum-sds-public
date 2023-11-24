package maum.brain.sds.collector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties()
public class InfoCollectorApplication {
    public static void main(String[] args){
        SpringApplication.run(InfoCollectorApplication.class, args);
    }
}
