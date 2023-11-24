package maum.brain.sds.logdb;

import maum.brain.sds.logdb.components.SessionLogMaxIDChecker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties()
public class DatabaseLoggerApplication {
    public static void main(String[] args){
        SpringApplication.run(DatabaseLoggerApplication.class, args);
        SessionLogMaxIDChecker.resetMaxID();
    }
}
