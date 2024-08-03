package com.aidiary.user.domain.repository;

import com.aidiary.common.enums.DiaryStatus;
import com.aidiary.user.domain.entity.DiariesEntity;
import com.aidiary.user.domain.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface JpaDiariesRepository extends JpaRepository<DiariesEntity, Long> {

    Optional<DiariesEntity> findByUserAndEntryDateAndStatus(UsersEntity usersEntity, LocalDate entryDate, DiaryStatus status);

    Long countAllByUserAndStatus(UsersEntity usersEntity, DiaryStatus status);

}
