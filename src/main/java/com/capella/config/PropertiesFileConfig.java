package com.capella.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({
        @PropertySource("classpath:cron-${spring.profiles.active}.properties")
})
public class PropertiesFileConfig {
}
