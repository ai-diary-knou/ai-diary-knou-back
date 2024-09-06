package com.aidiary.core.service;

import com.aidiary.core.entity.UserEmailAuthsEntity;
import com.aidiary.core.entity.UserLoginHistoriesEntity;
import com.aidiary.core.entity.UsersEntity;
import com.aidiary.core.repository.jpa.JpaUserEmailAuthsRepository;
import com.aidiary.core.repository.jpa.JpaUserLoginHistoriesRepository;
import com.aidiary.core.repository.jpa.JpaUsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.aidiary.common.enums.UserStatus.BLOCKED;

@Service
@RequiredArgsConstructor
public class UserDatabaseWriteService {

    private final JpaUsersRepository jpaUsersRepository;
    private final JpaUserEmailAuthsRepository jpaUserEmailAuthsRepository;
    private final JpaUserLoginHistoriesRepository jpaUserLoginHistoriesRepository;

    public UsersEntity save(UsersEntity usersEntity) {
        return jpaUsersRepository.save(usersEntity);
    }

    public void updateUserPassword(UsersEntity usersEntity, String newPasswordHash) {
        usersEntity.updatePassword(newPasswordHash);
    }

    public void updateUserNickname(UsersEntity usersEntity, String newNickname) {
        usersEntity.updateNickname(newNickname);
    }

    public void increaseLoginAttemptCntAndLockIfApproachMaxAttempt(UsersEntity usersEntity){
        usersEntity.updateLoginAttemptCnt(usersEntity.getLoginAttemptCnt() + 1);
        if (usersEntity.getLoginAttemptCnt() == 5) {
            usersEntity.updateStatus(BLOCKED);
        }
    }

    public void resetUserLoginAttemptCnt(UsersEntity usersEntity) {
        usersEntity.updateLoginAttemptCnt(0);
    }

    public UserEmailAuthsEntity save(UserEmailAuthsEntity userEmailAuthsEntity) {
        return jpaUserEmailAuthsRepository.save(userEmailAuthsEntity);
    }

    public void updateUserEmailAuthCodeAndResetConfirmedDate(UserEmailAuthsEntity originalUserEmailAuthEntity, String authCode) {
        originalUserEmailAuthEntity.updateCreatedAt(LocalDateTime.now());
        originalUserEmailAuthEntity.updateCodeAndResetConfirmedAt(authCode);
    }

    public void confirmUserEmailAuth(UserEmailAuthsEntity userEmailAuthsEntity){
        userEmailAuthsEntity.updateConfirmedAt(LocalDateTime.now());
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
