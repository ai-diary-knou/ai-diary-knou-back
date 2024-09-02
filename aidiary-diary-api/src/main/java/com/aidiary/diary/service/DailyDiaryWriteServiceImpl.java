package com.aidiary.diary.service;

import com.aidiary.common.enums.DiaryStatus;
import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.DiaryException;
import com.aidiary.common.utils.HybridEncryptor;
import com.aidiary.common.vo.ResponseBundle.UserPrincipal;
import com.aidiary.core.entity.DiariesEntity;
import com.aidiary.core.entity.UsersEntity;
import com.aidiary.core.service.DiaryDatabaseReadService;
import com.aidiary.core.service.DiaryDatabaseWriteService;
import com.aidiary.diary.model.DiaryRequestBundle.DiaryCreateRequest;
import com.aidiary.diary.model.DiaryRequestBundle.DiaryUpdateRequest;
import com.aidiary.diary.model.DiaryResponseBundle.DiarySaveRes;
import com.aidiary.diary.service.event.DailyDiaryCreateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DailyDiaryWriteServiceImpl implements DailyDiaryWriteService {

    private final DiaryDatabaseReadService diaryDatabaseReadService;
    private final DiaryDatabaseWriteService diaryDatabaseWriteService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final HybridEncryptor hybridEncryptor;

    @Override
    public DiarySaveRes saveDiaryAfterOpenAiAnalysis(UserPrincipal userPrincipal, DiaryCreateRequest request) throws Exception {

        UsersEntity user = UsersEntity.builder().id(userPrincipal.userId()).build();
        Optional<DiariesEntity> sameEntryDateDiary = diaryDatabaseReadService.findActiveDiaryByUserAndEntryDate(user, request.entryDate(), DiaryStatus.ACTIVE);

        if (sameEntryDateDiary.isPresent()) {
            throw new DiaryException(ErrorCode.DIARY_ALREADY_EXIST);
        }

        DiariesEntity diary = diaryDatabaseWriteService.saveDailyDiary(
                DiariesEntity.builder()
                        .user(user)
                        .content(hybridEncryptor.encrypt(request.content()))
                        .entryDate(request.entryDate())
                        .status(DiaryStatus.ACTIVE)
                        .build()
        );

        applicationEventPublisher.publishEvent(new DailyDiaryCreateEvent(this, diary, user, request.content()));

        return DiarySaveRes.builder().diaryId(diary.getId()).build();
    }

    @Override
    public DiarySaveRes updateDiaryAfterOpenAiAnalysis(UserPrincipal userPrincipal, Long diaryId, DiaryUpdateRequest request) throws Exception {

        UsersEntity user = UsersEntity.builder().id(userPrincipal.userId()).build();
        DiariesEntity originalDiary = diaryDatabaseReadService.findDiaryById(diaryId)
                .orElseThrow(() -> new DiaryException(ErrorCode.DIARY_NOT_FOUND));

        if (!originalDiary.getUser().isSameUser(user)) {
            throw new DiaryException(ErrorCode.DIARY_NOT_FOUND);
        }

        DiariesEntity newDiary = diaryDatabaseWriteService.updateDailyDiary(originalDiary,
                DiariesEntity.builder()
                .user(user)
                .content(hybridEncryptor.encrypt(request.content()))
                .entryDate(originalDiary.getEntryDate())
                .status(DiaryStatus.ACTIVE)
                .build()
        );

        applicationEventPublisher.publishEvent(new DailyDiaryCreateEvent(this, newDiary, user, request.content()));

        return DiarySaveRes.builder().diaryId(newDiary.getId()).build();
    }

}
