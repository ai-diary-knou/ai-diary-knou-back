package com.aidiary.user.domain.repository;

import com.aidiary.user.domain.entity.UserEmailAuthsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaUserEmailAuthsRepository extends JpaRepository<UserEmailAuthsEntity, Long> {

    Optional<UserEmailAuthsEntity> findByEmailAndCode(String email, int code);
}
