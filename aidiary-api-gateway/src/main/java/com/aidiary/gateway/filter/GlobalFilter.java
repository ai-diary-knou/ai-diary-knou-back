package com.aidiary.gateway.filter;

import com.aidiary.gateway.dto.UserClaims;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
@Slf4j
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

    public GlobalFilter(){
        super(Config.class);
    }

    @Data
    public static class Config{
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }

    @Override
    public GatewayFilter apply(Config config) {

        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            // Request Method, Params, Body Logging
            if (config.isPreLogger()) {
                log.info("GlobalFilter - Request ==> ");
                log.info("URI : {}", String.valueOf(request.getPath()));
                log.info("Params : {}", request.getQueryParams());
                log.info("Body : {}", request.getBody());
            }

            return ReactiveSecurityContextHolder.getContext()
                    .map(SecurityContext::getAuthentication)
                    .filter(Objects::nonNull)
                    .filter(Authentication::isAuthenticated)
                    .flatMap(authentication -> {
                        UserClaims userClaims = (UserClaims) authentication.getPrincipal();
                        log.info("Authenticated User: id - {}, email - {}", userClaims.getUserId(), userClaims.getEmail());

                        ServerHttpRequest modifiedRequest = request.mutate()
                                .header("X-User-Id", String.valueOf(userClaims.getUserId()))
                                .header("X-User-Email", userClaims.getEmail())
                                .header("X-User-Nickname", userClaims.getNickname())
                                .build();

                        // 변경된 요청으로 필터 체인 계속
                        return chain.filter(exchange.mutate().request(modifiedRequest).build());
                    })
                    .switchIfEmpty(chain.filter(exchange)) // 인증이 없는 경우 필터 체인 계속
                    .then(Mono.fromRunnable(() -> {
                        if (config.isPostLogger()) {
                            log.info("GlobalFilter - Response ==> {}", response.getStatusCode());
                        }
                    }));
        };
    }
}
