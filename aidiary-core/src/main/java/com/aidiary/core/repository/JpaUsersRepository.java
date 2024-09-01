package com.aidiary.core.repository;


import com.aidiary.core.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaUsersRepository extends JpaRepository<UsersEntity, Long> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<UsersEntity> findByEmail(String email);

}
