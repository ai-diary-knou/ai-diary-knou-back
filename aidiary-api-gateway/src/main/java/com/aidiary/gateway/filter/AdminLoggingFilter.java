package com.aidiary.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AdminLoggingFilter extends AbstractGatewayFilterFactory<AdminLoggingFilter.Config> {

    public AdminLoggingFilter(){
        super(Config.class);
    }

    public static class Config{
        // configuration properties
    }

    @Override
    public GatewayFilter apply(Config config) {

        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("DiaryLoggingFilter Before : request id -> {}", request.getId());

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {  // Mono : 비동기 방식에서의 단일 응답값 의미
                log.info("DiaryLoggingFilter After : response code -> {}", response.getStatusCode());
            }));
        };
    }
}
