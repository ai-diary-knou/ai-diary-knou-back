package com.aidiary.diary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = "com.aidiary")
public class AiDiaryDiaryApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiDiaryDiaryApplication.class, args);
    }

}
