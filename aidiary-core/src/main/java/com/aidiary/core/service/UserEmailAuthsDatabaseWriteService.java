package com.aidiary.core.service;

import com.aidiary.core.entity.UserEmailAuthsEntity;
import com.aidiary.core.repository.JpaUserEmailAuthsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
@RequiredArgsConstructor
public class UserEmailAuthsDatabaseWriteService {

    private final JpaUserEmailAuthsRepository jpaUserEmailAuthsRepository;

    public UserEmailAuthsEntity save(UserEmailAuthsEntity userEmailAuthsEntity) {
        return jpaUserEmailAuthsRepository.save(userEmailAuthsEntity);
    }

}
