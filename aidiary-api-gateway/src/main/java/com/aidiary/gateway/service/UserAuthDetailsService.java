package com.aidiary.gateway.service;


import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.UserException;
import com.aidiary.core.entity.UsersEntity;
import com.aidiary.core.repository.jpa.JpaUsersRepository;
import com.aidiary.gateway.dto.UserClaims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAuthDetailsService implements UserDetailsService {

    private final JpaUsersRepository jpaUsersRepository;

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UsersEntity usersEntity = jpaUsersRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_EXIST));
        log.info("loadUserByUserName : id = {}, email = {}, nickname - {}", usersEntity.getId(), usersEntity.getEmail(), usersEntity.getNickname());
        return UserClaims.builder()
                .userId(usersEntity.getId())
                .email(usersEntity.getEmail())
                .nickname(usersEntity.getNickname())
                .status(usersEntity.getStatus())
                .loginAttemptCnt(usersEntity.getLoginAttemptCnt())
                .build();
    }

}
