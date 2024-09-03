package com.aidiary.core.repository.jpa;


import com.aidiary.core.entity.UserLoginHistoriesEntity;
import com.aidiary.core.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaUserLoginHistoriesRepository extends JpaRepository<UserLoginHistoriesEntity, Long> {

    Optional<UserLoginHistoriesEntity> findByUser(UsersEntity user);

}
