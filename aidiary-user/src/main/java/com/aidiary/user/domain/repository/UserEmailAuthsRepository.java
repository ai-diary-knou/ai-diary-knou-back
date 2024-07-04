package com.aidiary.user.domain.repository;

import com.aidiary.user.domain.entity.UserEmailAuths;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserEmailAuthsRepository extends JpaRepository<UserEmailAuths, Long> {

    Optional<UserEmailAuths> findByEmailAndCode(String email, int code);
}
