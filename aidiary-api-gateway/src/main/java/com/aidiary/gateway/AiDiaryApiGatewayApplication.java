package com.aidiary.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.aidiary")
public class AiDiaryApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiDiaryApiGatewayApplication.class, args);
	}

}
