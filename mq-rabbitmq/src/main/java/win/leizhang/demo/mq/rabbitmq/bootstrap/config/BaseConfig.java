package win.leizhang.demo.mq.rabbitmq.bootstrap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

@Configuration
public class BaseConfig {

    @Bean
    public Executor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setThreadNamePrefix("threadPool-");
        // 基本参数
        threadPoolTaskExecutor.setCorePoolSize(30);
        threadPoolTaskExecutor.setMaxPoolSize(80);
        threadPoolTaskExecutor.setQueueCapacity(1000);
        threadPoolTaskExecutor.setKeepAliveSeconds(300);
        // 优雅关闭
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        threadPoolTaskExecutor.setAwaitTerminationSeconds(60);
        // 线程池对拒绝任务（无线程可用）的处理策略
        RejectedExecutionHandler rejectedExecutionHandler = new CallerRunsPolicy();
        threadPoolTaskExecutor.setRejectedExecutionHandler(rejectedExecutionHandler);
        return threadPoolTaskExecutor;
    }

}
