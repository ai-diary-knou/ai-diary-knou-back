package com.aidiary.core.service;

import com.aidiary.core.entity.UserEmailAuthsEntity;
import com.aidiary.core.entity.UserLoginHistoriesEntity;
import com.aidiary.core.entity.UsersEntity;
import com.aidiary.core.repository.jpa.JpaUserEmailAuthsRepository;
import com.aidiary.core.repository.jpa.JpaUserLoginHistoriesRepository;
import com.aidiary.core.repository.jpa.JpaUsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
@RequiredArgsConstructor
public class UserDatabaseWriteService {

    private final JpaUsersRepository jpaUsersRepository;
    private final JpaUserEmailAuthsRepository jpaUserEmailAuthsRepository;
    private final JpaUserLoginHistoriesRepository jpaUserLoginHistoriesRepository;

    public UsersEntity save(UsersEntity usersEntity) {
        return jpaUsersRepository.save(usersEntity);
    }

    public UserEmailAuthsEntity save(UserEmailAuthsEntity userEmailAuthsEntity) {
        return jpaUserEmailAuthsRepository.save(userEmailAuthsEntity);
    }

    public UserLoginHistoriesEntity save(UserLoginHistoriesEntity userLoginHistoriesEntity) {
        return jpaUserLoginHistoriesRepository.save(userLoginHistoriesEntity);
    }

    public void createOrUpdateUserLoginHistory(UsersEntity usersEntity, String ipAddress, String device, LocalDateTime lastLoggedAt) {

        Optional<UserLoginHistoriesEntity> optionalUserLoginHistoriesEntity =
                jpaUserLoginHistoriesRepository.findByUser(usersEntity);

        if (optionalUserLoginHistoriesEntity.isPresent()) {
            UserLoginHistoriesEntity userLoginHistoriesEntity = optionalUserLoginHistoriesEntity.get();
            userLoginHistoriesEntity.updateIpAddressAndDevice(ipAddress, device);
            userLoginHistoriesEntity.updateLastLoggedAt(lastLoggedAt);
            return;
        }

        jpaUserLoginHistoriesRepository.save(
                UserLoginHistoriesEntity.builder()
                        .user(usersEntity)
                        .ipAddress(ipAddress)
                        .device(device)
                        .lastLoggedAt(LocalDateTime.now())
                        .build()
        );

    }

}
