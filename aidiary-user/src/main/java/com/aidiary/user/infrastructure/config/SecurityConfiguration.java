package com.aidiary.user.infrastructure.config;


import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.enums.ErrorStatus;
import com.aidiary.common.vo.ResponseBundle;
import com.aidiary.user.presentation.filter.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration{

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private String[] permittedAllPaths = {
            "/api/v1/users/duplicate",
            "/api/v1/users/email/auth-code",
            "/api/v1/users/email/auth",
            "/api/v1/users/login",
            "/api/v1/users/password",
            "/api/v1/users/me"
    };

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
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers(request -> StringUtils.hasText(request.getHeader("ai_diary_tmp_user_id")))
                    .permitAll()
                    .requestMatchers(permittedAllPaths)
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/v1/users")
                    .permitAll()
                    .requestMatchers("/api/v1/users/me", "/api/v1/users/nickname", "/api/v1/users/password", "/api/v1/users/logout", "/api/v1/diaries", "/api/v1/diaries/**")
                    .authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(accessDeniedHandler())
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        ;

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
        return (request, response, accessDeniedException) -> {
            ResponseBundle.ErrorResponse errorResponse = ResponseBundle.ErrorResponse.builder()
                    .status(ErrorStatus.FAIL)
                    .code(ErrorCode.USER_AUTH_FAIL.name())
                    .message("You're trying to access to unauthorized path. Please try to login before accessing it.")
                    .build();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
        };
    }

}
