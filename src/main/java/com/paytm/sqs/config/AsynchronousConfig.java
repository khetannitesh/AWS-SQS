package com.paytm.sqs.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;
@Configuration
@EnableAsync
public class AsynchronousConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsynchronousConfig.class);
    @Bean (name = "taskExecutor")
    public Executor taskExecutor() {
        LOGGER.info("Asynchronous Task Executor creation start");
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("MyConsumer-");
        executor.initialize();
        return executor;
    }
}