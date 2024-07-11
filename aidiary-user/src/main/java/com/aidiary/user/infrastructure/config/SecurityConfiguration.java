package com.aidiary.user.infrastructure.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration{

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()); /* AbstractHttpConfigurer::disable */
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/user/**")
                .permitAll()
        );
        http.formLogin(form -> form.loginPage("/login"));
        return http.build();
    }

}
