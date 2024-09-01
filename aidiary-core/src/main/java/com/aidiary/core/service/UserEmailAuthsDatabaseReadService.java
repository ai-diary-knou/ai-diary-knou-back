package com.aidiary.core.service;

import com.aidiary.core.entity.UserEmailAuthsEntity;
import com.aidiary.core.repository.JpaUserEmailAuthsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserEmailAuthsDatabaseReadService {

    private final JpaUserEmailAuthsRepository jpaUserEmailAuthsRepository;

    public Optional<UserEmailAuthsEntity> findByEmail(String email) {
        return jpaUserEmailAuthsRepository.findByEmail(email);
    }

}
