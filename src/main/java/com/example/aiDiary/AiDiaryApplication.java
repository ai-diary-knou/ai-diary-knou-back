package com.example.aiDiary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class AiDiaryApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiDiaryApplication.class, args);
    }

}
