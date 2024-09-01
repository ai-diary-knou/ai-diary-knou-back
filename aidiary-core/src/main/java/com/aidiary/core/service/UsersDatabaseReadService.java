package com.aidiary.core.service;

import com.aidiary.core.entity.UsersEntity;
import com.aidiary.core.repository.JpaUsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UsersDatabaseReadService {

    private final JpaUsersRepository jpaUsersRepository;

    public Optional<UsersEntity> findById(Long userId){
        return jpaUsersRepository.findById(userId);
    }

    public Optional<UsersEntity> findByEmail(String email){
        return jpaUsersRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email){
        return jpaUsersRepository.existsByEmail(email);
    }

    public boolean existsByNickname(String nickname){
        return jpaUsersRepository.existsByNickname(nickname);
    }

}
