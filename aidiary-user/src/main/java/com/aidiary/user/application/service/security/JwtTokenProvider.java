package com.aidiary.user.application.service.security;

import com.aidiary.user.application.dto.UserClaims;
import com.aidiary.user.infrastructure.encryptor.HybridEncryptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${spring.jwt.secret-key}")
    private String secretKey;

    @Value("${spring.jwt.expire-time}")
    private long tokenExpirationMilliSeconds;

    @Value("${client.domain}")
    private String clientDomain;

    private final HybridEncryptor hybridEncryptor;

    // 토큰 생성
    public String createToken(UserClaims userClaims) throws Exception {

        Claims claims = Jwts.claims();
        claims.put("token", hybridEncryptor.encrypt(new ObjectMapper().writeValueAsString(UserClaims.builder()
                .userId(userClaims.getUserId())
                .email(userClaims.getEmail())
                .nickname(userClaims.getNickname())
                .build())));

        return "Bearer " + Jwts.builder()
                .setClaims(claims)
                .setSubject(userClaims.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpirationMilliSeconds))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // 토큰에서 회원 정보 추출
    public UserClaims extractClaimsFromToken(String token) throws Exception {
        String tokenWithoutBearer = token.substring("Bearer ".length());
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(tokenWithoutBearer).getBody();
        String authorization = Objects.requireNonNull(claims.get("token")).toString();
        String decryptedToken = hybridEncryptor.decrypt(authorization);
        return new ObjectMapper().readValue(decryptedToken, UserClaims.class);
    }

    // 토큰 만료 여부 확인
    public boolean isValidToken(String token){
        try {
            String tokenWithoutBearer = token.substring("Bearer ".length());
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(tokenWithoutBearer);
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // 쿠키 설정
    public void setCookieByJwtToken(HttpServletResponse response, String token){

        Cookie cookie = new Cookie("Authentication", token);
        cookie.setDomain("ai-diary-knou-front.vercel.app");
        //cookie.setAttribute("SameSite", "None");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge((int)(tokenExpirationMilliSeconds / 1000));
        cookie.setPath("/");
        response.addCookie(cookie);

    }

    // 헤더에서 토큰 추출
    public String getJwtTokenFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (Objects.isNull(authHeader) || authHeader.isEmpty()) {
            return null;
        }

        return authHeader;
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
