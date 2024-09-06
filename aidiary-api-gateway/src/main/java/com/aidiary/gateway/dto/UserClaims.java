package com.aidiary.gateway.dto;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.enums.UserStatus;
import com.aidiary.common.exception.UserException;
import com.aidiary.core.entity.UsersEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

import static com.aidiary.common.enums.UserStatus.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class UserClaims implements UserDetails {

    private Long userId;

    private String email;

    private String nickname;

    private String password;

    private UserStatus status;

    private Integer loginAttemptCnt;

    public UserClaims(UsersEntity usersEntity) {
        this.userId = usersEntity.getId();
        this.email = usersEntity.getEmail();
        this.nickname = usersEntity.getNickname();
        this.password = usersEntity.getPassword();
        this.status = usersEntity.getStatus();
        this.loginAttemptCnt = usersEntity.getLoginAttemptCnt();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !this.status.equals(INACTIVE);
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.status.equals(BLOCKED);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !this.status.equals(BLOCKED);
    }

    @Override
    public boolean isEnabled() {
        return this.status.equals(ACTIVE);
    }

    public String toJsonString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public UserClaims parse(String jsonString) throws UserException {
        try {
            return new ObjectMapper().readValue(jsonString, UserClaims.class);
        } catch (JsonProcessingException e) {
            log.info("UserClaims Parse Fail :: ", e);
            throw new UserException(ErrorCode.USER_TOKEN_ERROR);
        }
    }

}