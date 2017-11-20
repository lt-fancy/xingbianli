package com.sawallianc.config;

import com.sawallianc.zuulfilter.PreRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StartConfig {

    @Bean
    public PreRequestFilter preRequestFilter(){
        return new PreRequestFilter();
    }
}
