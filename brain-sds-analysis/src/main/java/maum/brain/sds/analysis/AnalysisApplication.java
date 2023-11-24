package maum.brain.sds.analysis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class AnalysisApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnalysisApplication.class, args);
    }

}
