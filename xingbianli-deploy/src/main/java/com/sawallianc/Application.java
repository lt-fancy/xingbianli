package com.sawallianc;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class Application {
    public static void main(String[] args) {
        new SpringApplicationBuilder().sources(Application.class).bannerMode(Banner.Mode.OFF).run(args);
    }
}
