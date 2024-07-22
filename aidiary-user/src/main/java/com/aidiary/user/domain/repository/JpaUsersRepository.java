package com.aidiary.user.domain.repository;

import com.aidiary.user.domain.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaUsersRepository extends JpaRepository<UsersEntity, Long> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<UsersEntity> findByEmail(String email);

}
