package com.aidiary.user.domain.repository;

import com.aidiary.user.domain.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUsersRepository extends JpaRepository<UsersEntity, Long> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

}
