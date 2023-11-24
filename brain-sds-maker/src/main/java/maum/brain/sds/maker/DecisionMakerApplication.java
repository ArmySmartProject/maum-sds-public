package maum.brain.sds.maker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties()
public class DecisionMakerApplication {

    public static void main(String[] args){
        SpringApplication.run(DecisionMakerApplication.class, args);
    }
}
