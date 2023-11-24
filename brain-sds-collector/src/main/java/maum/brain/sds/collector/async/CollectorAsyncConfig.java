package maum.brain.sds.collector.async;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class CollectorAsyncConfig {
  @Bean(name = "threadPoolTaskExecutor")
  public Executor threadPoolTaskExecutor() {
    System.out.println("Started : threadPoolTaskExecutor");
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setCorePoolSize(10);
    taskExecutor.setMaxPoolSize(30);
    taskExecutor.setQueueCapacity(250);
    taskExecutor.setThreadNamePrefix("Executor-");
    taskExecutor.initialize();
    return taskExecutor;
  }
}
