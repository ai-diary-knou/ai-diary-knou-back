package com.aidiary.gateway.filter;

import com.aidiary.auth.dto.JwtTokenInfo;
import com.aidiary.auth.service.JwtTokenProvider;
import com.aidiary.gateway.dto.UserClaims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import java.util.Arrays;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter implements WebFilter {

    @Value("${spring.cloud.gateway.allowed-uri.server.permitted-all.any}")
    private String[] permittedAllPaths;

    @Value("${spring.cloud.gateway.allowed-uri.server.permitted-all.post}")
    private String[] permittedPostPaths;

    private final UserDetailsService userAuthDetailsService;

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();

        if (isAllPermittedPath(request) || isPostPermittedPath(request)) {
            return chain.filter(exchange);
        }

        String authorization = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        return Mono.justOrEmpty(authorization)
                .filter(authHeader -> authHeader.startsWith("Bearer "))
                .switchIfEmpty(chain.filter(exchange)
                        .then(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization header is missing or invalid"))))
                .flatMap(authHeader -> {
                    String token = authHeader.substring(7); // "Bearer " 이후의 토큰 부분
                    JwtTokenInfo jwtTokenInfo = jwtTokenProvider.extractClaimsFromToken(token);

                    if (Objects.isNull(jwtTokenInfo) || !jwtTokenProvider.isValidToken(token)) {
                        return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT token is expired or invalid"));
                    }

                    return authenticateUser(exchange, chain, jwtTokenInfo);
                })
                .onErrorResume(e -> unauthorized(exchange, e.getMessage()));

    }

    private Mono<Void> authenticateUser(ServerWebExchange exchange, WebFilterChain chain, JwtTokenInfo jwtTokenInfo) {
        return Mono.defer(() -> {
            UserClaims realDatabaseUserClaim = (UserClaims) userAuthDetailsService.loadUserByUsername(jwtTokenInfo.email());

            if (!jwtTokenInfo.email().equals(realDatabaseUserClaim.getEmail()) ||
                    !jwtTokenInfo.nickname().equals(realDatabaseUserClaim.getNickname())) {
                return unauthorized(exchange, "User details do not match");
            }

            Authentication authentication = new UsernamePasswordAuthenticationToken(realDatabaseUserClaim, null, realDatabaseUserClaim.getAuthorities());
            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
        });
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        log.error("Unauthorized access: {}", message);
        return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, message));
    }

    private boolean isAllPermittedPath(ServerHttpRequest request) {
        return Arrays.stream(permittedAllPaths).anyMatch(request.getURI().getPath()::startsWith);
    }

    private boolean isPostPermittedPath(ServerHttpRequest request) {
        return Arrays.stream(permittedPostPaths)
                .anyMatch(path ->
                        HttpMethod.POST.equals(request.getMethod()) && path.startsWith(request.getURI().getPath())
                );
    }

}
