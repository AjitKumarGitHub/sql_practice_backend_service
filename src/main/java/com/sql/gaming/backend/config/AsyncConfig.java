package com.sql.gaming.backend.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

	@Bean(name = "submissionExecutor")
	public Executor submissionExecutor() {

		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

		// Number of threads that work simultaneously
		executor.setCorePoolSize(10);

		// Maximum concurrent SQL evaluation workers
		executor.setMaxPoolSize(20);

		// Requests waiting for a worker
		executor.setQueueCapacity(200);

		executor.setThreadNamePrefix("sql-submission-");

		// Wait for running tasks during application shutdown
		executor.setWaitForTasksToCompleteOnShutdown(true);

		executor.setAwaitTerminationSeconds(30);

		executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());

		executor.initialize();

		executor.initialize();

		return executor;
	}
}
