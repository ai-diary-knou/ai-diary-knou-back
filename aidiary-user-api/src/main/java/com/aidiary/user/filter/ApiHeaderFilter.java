package com.aidiary.user.filter;

import com.aidiary.common.vo.ResponseBundle.UserPrincipal;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Component
@Slf4j
public class ApiHeaderFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        Long userId = StringUtils.hasText(httpRequest.getHeader("X-User-Id")) ? Long.parseLong(httpRequest.getHeader("X-User-Id")) : null;
        String email = httpRequest.getHeader("X-User-Email");
        String nickname =  Objects.nonNull(httpRequest.getHeader("X-User-Nickname")) ?
            URLDecoder.decode(httpRequest.getHeader("X-User-Nickname"), StandardCharsets.UTF_8) : null;

        log.info("Received Headers in ApiHeaderFilter: X-User-Id: {}, X-User-Email: {}, X-User-Nickname: {}",
                userId, email, nickname);

        UserPrincipal userPrincipal =
                Objects.isNull(userId) || !StringUtils.hasText(email) || !StringUtils.hasText(nickname) ? null :
                        UserPrincipal.builder()
                            .userId(userId)
                            .email(email)
                            .nickname(nickname)
                            .build();

        httpRequest.setAttribute("userPrincipal", userPrincipal);

        chain.doFilter(request, response);
    }
}
