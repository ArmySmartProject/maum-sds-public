package maum.brain.sds.adapter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties()
public class AdapterApplication {
    public static void main(String[] args){
        SpringApplication.run(AdapterApplication.class, args);
    }
}
