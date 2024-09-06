package com.aidiary.core.service;

import com.aidiary.core.entity.QUserEmailAuthsEntity;
import com.aidiary.core.entity.UserEmailAuthsEntity;
import com.aidiary.core.entity.UserLoginHistoriesEntity;
import com.aidiary.core.entity.UsersEntity;
import com.aidiary.core.repository.jpa.JpaUserEmailAuthsRepository;
import com.aidiary.core.repository.jpa.JpaUserLoginHistoriesRepository;
import com.aidiary.core.repository.jpa.JpaUsersRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDatabaseReadService {

    private final JpaUsersRepository jpaUsersRepository;
    private final JpaUserEmailAuthsRepository jpaUserEmailAuthsRepository;
    private final JpaUserLoginHistoriesRepository jpaUserLoginHistoriesRepository;
    private final JPAQueryFactory jpaQueryFactory;

    public boolean isUserExistsByEmail(String email) {
        return jpaUsersRepository.existsByEmail(email);
    }

    public boolean isUserExistsByNickname(String nickname) {
        return jpaUsersRepository.existsByNickname(nickname);
    }

    public Optional<UsersEntity> findUserById(Long userId) {
        return jpaUsersRepository.findById(userId);
    }

    public Optional<UsersEntity> findUserByEmail(String email) {
        return jpaUsersRepository.findByEmail(email);
    }

    public boolean isUserEmailAuthConfirmedByEmail(String email){

        QUserEmailAuthsEntity qUserEmailAuthsEntity = QUserEmailAuthsEntity.userEmailAuthsEntity;

        return Objects.nonNull(
                jpaQueryFactory.selectOne()
                        .from(qUserEmailAuthsEntity)
                        .where(
                                qUserEmailAuthsEntity.email.eq(email),
                                qUserEmailAuthsEntity.confirmedAt.isNotNull()
                        ).fetch()
        );
    }

    public Optional<UserEmailAuthsEntity> findUserEmailAuthByEmail(String email){
        return jpaUserEmailAuthsRepository.findByEmail(email);
    }

    public Optional<UserLoginHistoriesEntity> findUserLoginHistoryByUser(UsersEntity user) {
        return jpaUserLoginHistoriesRepository.findByUser(user);
    }

}
