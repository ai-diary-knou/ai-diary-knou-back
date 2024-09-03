package com.aidiary.diary.filter;

import com.aidiary.common.vo.ResponseBundle;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Objects;

@Component
@Slf4j
public class ApiHeaderFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        Long userId = StringUtils.hasText(httpRequest.getHeader("X-User-Id")) ? Long.parseLong(httpRequest.getHeader("X-User-Id")) : null;
        String email = httpRequest.getHeader("X-User-Email");
        String nickname = httpRequest.getHeader("X-User-Nickname");

        ResponseBundle.UserPrincipal userPrincipal =
                Objects.isNull(userId) || !StringUtils.hasText(email) || !StringUtils.hasText(nickname) ? null :
                        ResponseBundle.UserPrincipal.builder()
                            .userId(userId)
                            .email(email)
                            .nickname(nickname)
                            .build();

        httpRequest.setAttribute("userPrincipal", userPrincipal);

        chain.doFilter(request, response);
    }
}