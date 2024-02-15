package com.capella.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.apache.http.client.methods.HttpGet;
import java.util.Arrays;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Value("#{'${ui.cors.origin.urls}'.split(',')}")
    private String[] corsOriginUrls;

    @Bean
    public WebMvcConfigurer corsConfigurer(){
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods(HttpGet.METHOD_NAME, HttpPost.METHOD_NAME, HttpPut.METHOD_NAME, HttpDelete.METHOD_NAME)
                        .allowedHeaders("*")
                        .allowedOrigins(Arrays.asList(corsOriginUrls).stream().map(StringUtils::trim).toArray(String[]::new));
            }
        };
    }

}
