package com.aidiary.auth.service;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.UserException;
import com.aidiary.core.entity.UsersEntity;
import com.aidiary.core.repository.JpaUsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserAuthService {

    private final JpaUsersRepository jpaUsersRepository;

    public UsersEntity loadUserByUsername(String email) {

        return jpaUsersRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_EXIST));
    }

}
