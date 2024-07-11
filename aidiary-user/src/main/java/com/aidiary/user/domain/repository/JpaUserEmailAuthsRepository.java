package com.aidiary.user.domain.repository;

import com.aidiary.user.domain.entity.UserEmailAuthsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserEmailAuthsRepository extends JpaRepository<UserEmailAuthsEntity, Long> {


}
