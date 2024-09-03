package com.aidiary.diary.service;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.DiaryException;
import com.aidiary.common.vo.ResponseBundle.UserPrincipal;
import com.aidiary.core.entity.DailyAnalysisSentencesEntity;
import com.aidiary.core.entity.DailyAnalysisWordsEntity;
import com.aidiary.core.entity.DiariesEntity;
import com.aidiary.core.entity.UsersEntity;
import com.aidiary.core.service.DiaryDatabaseReadService;
import com.aidiary.diary.model.DiaryResponseBundle.DiaryDetail;
import com.aidiary.diary.service.processor.DailyDiaryContentProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DailyDiaryReadServiceImpl implements DailyDiaryReadService {

    private final DiaryDatabaseReadService diaryDatabaseReadService;
    private final DailyDiaryContentProcessor dailyDiaryContentProcessor;

    @Override
    public DiaryDetail getDiaryDetail(UserPrincipal userPrincipal, Long diaryId) throws Exception {

        DiariesEntity diary = diaryDatabaseReadService.findDiaryById(diaryId)
                .orElseThrow(() -> new DiaryException(ErrorCode.DIARY_NOT_FOUND));

        validateIfDiaryIsWrittenByUser(userPrincipal, diary);
        validateIfDiaryStatusActive(diary);

        List<DailyAnalysisWordsEntity> words = diaryDatabaseReadService.findWordsFromDiary(diary);
        List<DailyAnalysisSentencesEntity> sentences = diaryDatabaseReadService.findSentencesFromDiary(diary);

        return dailyDiaryContentProcessor.process(diary, words, sentences);
    }

    private void validateIfDiaryStatusActive(DiariesEntity diariesEntity) {
        if (!diariesEntity.isActivated()) {
            throw new DiaryException(ErrorCode.DIARY_NOT_FOUND);
        }
    }

    private void validateIfDiaryIsWrittenByUser(UserPrincipal userPrincipal, DiariesEntity diariesEntity) {
        if (!diariesEntity.getUser().getId().equals(userPrincipal.userId())) {
            throw new DiaryException(ErrorCode.DIARY_OWNER_MISMATCH);
        }
    }



    @Override
    public Long getUserDiaryCount(UserPrincipal userPrincipal) {

        UsersEntity user = UsersEntity.builder().id(userPrincipal.userId()).build();

        return diaryDatabaseReadService.countAllActiveDiariesByUser(user);

    }
}
