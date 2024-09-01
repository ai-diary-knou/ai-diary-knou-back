package com.aidiary.core.service;

import com.aidiary.core.entity.UsersEntity;
import com.aidiary.core.repository.JpaUsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
@RequiredArgsConstructor
public class UsersDatabaseWriteService {

    private final JpaUsersRepository jpaUsersRepository;

    public UsersEntity save(UsersEntity usersEntity){
        return jpaUsersRepository.save(usersEntity);
    }

}
