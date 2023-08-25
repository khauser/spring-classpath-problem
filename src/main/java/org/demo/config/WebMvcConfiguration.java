package org.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableAsync
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    @Qualifier("rest-thread-pool")
    private ThreadPoolTaskScheduler threadPool;

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
          configurer.setTaskExecutor(threadPool);
          configurer.setDefaultTimeout(200_000);
    }
}
