package com.aidiary.user.application.service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Base64Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${spring.jwt.secret-key}")
    private String secretKey;

    @Value("${spring.jwt.expire-time}")
    private long tokenExpirationMilliSeconds;

    @Value("${client.domain}")
    private String clientDomain;

    @Builder
    @Getter
    public static class UserClaims{
        private Long userId;
        private String email;
        private String nickname;
    }

    // 토큰 생성
    public String createToken(UserClaims userClaims) {

        Claims claims = Jwts.claims().setSubject(userClaims.getEmail());
        claims.put("userId", userClaims.getUserId());
        claims.put("email", userClaims.getEmail());
        claims.put("nickname", userClaims.getNickname());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userClaims.email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpirationMilliSeconds))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // 토큰에서 회원 정보 추출
    public UserClaims extractClaimsFromToken(String token){
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        return UserClaims.builder()
                .userId(Long.parseLong(claims.get("userId").toString()))
                .email(claims.get("email").toString())
                .nickname(claims.get("nickname").toString())
                .build();
    }

    // 토큰 만료 여부 확인
    public boolean isValidToken(String token){
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // 쿠키 설정
    public void setCookieByJwtToken(HttpServletResponse response, String token){

        Cookie cookie = new Cookie("Authentication", token);
        cookie.setDomain(clientDomain);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // todo 차후 true로 변경 필요
        cookie.setMaxAge((int)(tokenExpirationMilliSeconds / 1000));
        cookie.setPath("/");
        response.addCookie(cookie);

    }

    // 쿠키에서 토큰 추출
    public String getJwtTokenFromCookie(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        if (Objects.isNull(cookies)) {
            return null;
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> Objects.nonNull(cookie) && "Authentication".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst().orElse(null);
    }

    // 쿠키에서 토큰 삭제
    public void revokeJwtTokenFromCookie(HttpServletResponse response){
        Cookie cookie = new Cookie("Authentication", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

}
