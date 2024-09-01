package com.aidiary.gateway.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class UserLoggingFilter extends AbstractGatewayFilterFactory<UserLoggingFilter.Config> {

    public UserLoggingFilter(){
        super(Config.class);
    }

    @Data
    public static class Config{

    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();



           // Post Filter
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {

            }));
        };
    }



}
