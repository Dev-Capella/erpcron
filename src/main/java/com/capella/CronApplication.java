package com.capella;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableCaching
@EnableScheduling
@EnableAsync
public class CronApplication {
    public static void main(String[] args){
        SpringApplication.run(CronApplication.class,args);
    }
}
