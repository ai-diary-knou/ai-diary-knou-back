package com.aidiary.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.aidiary.auth","com.aidiary.common"})
public class AidiaryUserAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AidiaryUserAuthApplication.class, args);
    }

}
