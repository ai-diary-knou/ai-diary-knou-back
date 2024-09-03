package com.aidiary.core.repository.jpa;

import com.aidiary.core.entity.UserEmailAuthsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaUserEmailAuthsRepository extends JpaRepository<UserEmailAuthsEntity, Long>,
        QuerydslPredicateExecutor<UserEmailAuthsEntity>
{

    Optional<UserEmailAuthsEntity> findByEmail(String email);

}
