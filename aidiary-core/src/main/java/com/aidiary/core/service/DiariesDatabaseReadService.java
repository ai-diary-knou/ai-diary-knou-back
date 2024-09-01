package com.aidiary.core.service;

import com.aidiary.common.enums.DiaryStatus;
import com.aidiary.core.entity.DiariesEntity;
import com.aidiary.core.entity.UsersEntity;
import com.aidiary.core.repository.JpaDiariesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiariesDatabaseReadService {

    private final JpaDiariesRepository jpaDiariesRepository;

    public Optional<DiariesEntity> findById(Long diaryId){
        return jpaDiariesRepository.findById(diaryId);
    }

    public Long countAllByUserAndStatus(UsersEntity usersEntity, DiaryStatus diaryStatus) {
        return jpaDiariesRepository.countAllByUserAndStatus(usersEntity, diaryStatus);
    }

    public Optional<DiariesEntity> findByUserAndEntryDateAndStatus(UsersEntity usersEntity, LocalDate entryDate, DiaryStatus diaryStatus){
        return jpaDiariesRepository.findByUserAndEntryDateAndStatus(usersEntity, entryDate, diaryStatus);
    }

}
