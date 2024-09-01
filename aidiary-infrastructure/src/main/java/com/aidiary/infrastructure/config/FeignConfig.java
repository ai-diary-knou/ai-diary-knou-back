package com.aidiary.infrastructure.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.aidiary.infrastructure.transport")
public class FeignConfig {

}
