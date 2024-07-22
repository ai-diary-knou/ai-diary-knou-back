package com.aidiary.user.infrastructure.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.aidiary.user.infrastructure.transport")
public class FeignConfig {

}
