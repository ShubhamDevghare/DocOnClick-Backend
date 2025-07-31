////want to run certain tasks in the background, without blocking the main thread
//// eg : When a user signs up or books an appointment, you can send a confirmation email 
//// without making the user wait.
//
////To allow you to use @Async("taskExecutor") in your service methods so  they run in the background,
//// using this custom thread pool.
//
//package com.lakshit.doconclick.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//
//import java.util.concurrent.Executor;
//
//@Configuration
//@EnableAsync
//public class AsyncConfig {
//
//    @Bean(name = "taskExecutor")
//    public Executor taskExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(2);
//        executor.setMaxPoolSize(5);
//        executor.setQueueCapacity(100);
//        executor.setThreadNamePrefix("EmailThread-");
//        executor.initialize();
//        return executor;
//    }
//}
//

// new added just demo
package com.lakshit.doconclick.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("DocOnClick-");
        executor.initialize();
        return executor;
    }
}

