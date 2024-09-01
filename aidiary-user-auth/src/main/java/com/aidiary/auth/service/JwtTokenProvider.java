package com.aidiary.auth.service;

import com.aidiary.auth.dto.JwtTokenInfo;
import com.aidiary.common.utils.HybridEncryptor;
import com.aidiary.core.entity.UsersEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${spring.jwt.secret-key}")
    private String secretKey;

    @Value("${spring.jwt.expire-time}")
    private long tokenExpirationMilliSeconds;

    private final HybridEncryptor hybridEncryptor;

    // 토큰 생성
    public String createToken(UsersEntity usersEntity) throws Exception {

        Claims claims = Jwts.claims();
        claims.put("token", hybridEncryptor.encrypt(new ObjectMapper().writeValueAsString(JwtTokenInfo.builder()
                .userId(usersEntity.getId())
                .email(usersEntity.getEmail())
                .nickname(usersEntity.getNickname())
                .build())));

        return "Bearer " + Jwts.builder()
                .setClaims(claims)
                .setSubject(usersEntity.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpirationMilliSeconds))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // 토큰에서 회원 정보 추출
    public JwtTokenInfo extractClaimsFromToken(String token) {

        try {
            Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
            String authorization = Objects.requireNonNull(claims.get("token")).toString();
            String decryptedToken = hybridEncryptor.decrypt(authorization);
            return new ObjectMapper().readValue(decryptedToken, JwtTokenInfo.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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

}
