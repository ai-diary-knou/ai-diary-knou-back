package com.aidiary.user.presentation.filter;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.enums.ErrorStatus;
import com.aidiary.common.vo.ResponseBundle;
import com.aidiary.user.application.service.security.JwtTokenProvider;
import com.aidiary.user.application.service.security.JwtTokenProvider.UserClaims;
import com.aidiary.user.domain.entity.UsersEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(UserDetailsService userDetailsService, JwtTokenProvider jwtTokenProvider, ObjectMapper objectMapper) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {

            if (isAllPermittedPath(request) || isPostPermittedPath(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = jwtTokenProvider.getJwtTokenFromCookie(request);
            UserClaims userClaims = StringUtils.hasText(token) ? jwtTokenProvider.extractClaimsFromToken(token) : null;

            if (Objects.isNull(userClaims) || !jwtTokenProvider.isValidToken(token)) {
                throw new AuthenticationException("JWT token is expired or invalid"){};
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (Objects.nonNull(authentication)) {
                filterChain.doFilter(request, response);
                return;
            }

            UsersEntity usersEntity = (UsersEntity) userDetailsService.loadUserByUsername(userClaims.getEmail());
            if (!userClaims.getEmail().equals(usersEntity.getUsername())) {
                filterChain.doFilter(request, response);
                return;
            }

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(usersEntity, null, usersEntity.getAuthorities());
            usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            filterChain.doFilter(request, response);

        } catch (AuthenticationException e) {

            ResponseBundle.ErrorResponse errorResponse = ResponseBundle.ErrorResponse.builder()
                    .status(ErrorStatus.FAIL)
                    .code(ErrorCode.USER_TOKEN_ERROR.name())
                    .message(ErrorCode.USER_TOKEN_ERROR.getMessage())
                    .build();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));

        } catch (Exception e) {

            ResponseBundle.ErrorResponse errorResponse = ResponseBundle.ErrorResponse.builder()
                    .status(ErrorStatus.FAIL)
                    .code(ErrorCode.USER_AUTH_FAIL.name())
                    .message("Unknown Error has occurred while authenticating Jwt Token. Please contact backend developer.")
                    .build();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }

    }

    private static boolean isAllPermittedPath(HttpServletRequest request) {
        String[] permittedAllPaths = {
                "/api/v1/users/duplicate",
                "/api/v1/users/email/auth-code",
                "/api/v1/users/email/auth",
                "/api/v1/users/login",
                "/api/v1/users/password"
        };
        return Arrays.stream(permittedAllPaths).anyMatch(request.getRequestURI()::startsWith);
    }

    private static boolean isPostPermittedPath(HttpServletRequest request) {

        String[] permittedPostPaths = {
                "/api/v1/users"
        };

        return Arrays.stream(permittedPostPaths)
                .anyMatch(path ->
                        HttpMethod.POST.name().equals(request.getMethod()) && path.startsWith(request.getRequestURI())
                );
    }

}
