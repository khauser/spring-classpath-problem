package org.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Configuration
public class ThreadPoolConfiguration
{
    @Value("${threads.number-rest-threads}")
    private int numberRestThreads;

    @Value("${threads.shutdown-timeout-millis}")
    private int shutdownTimeoutMillis;

    @Bean(name = "rest-thread-pool")
    public ThreadPoolTaskScheduler createRESTThreadPool()
    {
        return createThreadPool("REST-Pool", numberRestThreads);
    }

    private ThreadPoolTaskScheduler createThreadPool(String name, int numberThreads)
    {
        ThreadPoolTaskScheduler threadPool = new ThreadPoolTaskScheduler();
        threadPool.setThreadNamePrefix(name);
        threadPool.setErrorHandler(exception -> log.error("Exception in thread.", exception));
        threadPool.setPoolSize(numberThreads);
        threadPool.setWaitForTasksToCompleteOnShutdown(true);
        threadPool.setAwaitTerminationSeconds(shutdownTimeoutMillis);
        return threadPool;
    }
}
