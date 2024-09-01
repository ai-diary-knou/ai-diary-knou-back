package com.aidiary.gateway.config;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.enums.ErrorStatus;
import com.aidiary.common.vo.ResponseBundle;
import com.aidiary.gateway.dto.SecurityAllowedUriProperties;
import com.aidiary.gateway.filter.JwtAuthenticationFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;
import java.util.Arrays;
import java.util.List;
import static org.springframework.security.config.web.server.SecurityWebFiltersOrder.REACTOR_CONTEXT;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "https://ai-diary-knou-front.vercel.app"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.cloud.gateway.allowed-uri.server")
    public SecurityAllowedUriProperties securityAllowedUriProperties(){
        return new SecurityAllowedUriProperties();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeExchange(authorize -> authorize
                        .pathMatchers(securityAllowedUriProperties().getPermittedAll().getAny())
                        .permitAll()
                        .pathMatchers(HttpMethod.POST, securityAllowedUriProperties().getPermittedAll().getPost())
                        .permitAll()
                        .pathMatchers(securityAllowedUriProperties().getAuthenticated())
                        .authenticated()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(accessDeniedHandler())
                        .authenticationEntryPoint(new HttpStatusServerEntryPoint(org.springframework.http.HttpStatus.UNAUTHORIZED))
                )
                .addFilterAt(jwtAuthenticationFilter, REACTOR_CONTEXT)
        ;

        return http.build();
    }

    @Bean
    public ServerAccessDeniedHandler accessDeniedHandler() {
        return (exchange, denied) -> {
            ResponseBundle.ErrorResponse errorResponse = ResponseBundle.ErrorResponse.builder()
                    .status(ErrorStatus.FAIL)
                    .code(ErrorCode.USER_AUTH_FAIL.name())
                    .message("You're trying to access an unauthorized path. Please log in before accessing it.")
                    .build();
            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
            exchange.getResponse().getHeaders().setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            try {
                return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                        .bufferFactory().wrap(new ObjectMapper().writeValueAsBytes(errorResponse))));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        };
    }

}
