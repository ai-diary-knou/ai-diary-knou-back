package com.aidiary.user.domain.repository;

import com.aidiary.common.enums.DiaryStatus;
import com.aidiary.user.domain.entity.DiariesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface JpaDiariesRepository extends JpaRepository<DiariesEntity, Long> {

    Optional<DiariesEntity> findByEntryDateAndStatus(LocalDate entryDate, DiaryStatus status);

}
