package com.aidiary.core.repository.jpa;

import com.aidiary.common.enums.DiaryStatus;
import com.aidiary.core.entity.DiariesEntity;
import com.aidiary.core.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface JpaDiariesRepository extends JpaRepository<DiariesEntity, Long> {

    Optional<DiariesEntity> findByUserAndEntryDateAndStatus(UsersEntity usersEntity, LocalDate entryDate, DiaryStatus status);

    Long countAllByUserAndStatus(UsersEntity usersEntity, DiaryStatus status);

}
