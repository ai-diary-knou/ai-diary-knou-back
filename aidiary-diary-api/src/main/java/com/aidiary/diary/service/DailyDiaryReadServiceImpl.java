package com.aidiary.diary.service;

import com.aidiary.common.enums.DiarySentenceType;
import com.aidiary.common.enums.DiaryWordType;
import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.DiaryException;
import com.aidiary.common.utils.HybridEncryptor;
import com.aidiary.common.vo.ResponseBundle.UserPrincipal;
import com.aidiary.core.entity.DailyAnalysisSentencesEntity;
import com.aidiary.core.entity.DailyAnalysisWordsEntity;
import com.aidiary.core.entity.DiariesEntity;
import com.aidiary.core.entity.UsersEntity;
import com.aidiary.core.service.DiaryDatabaseReadService;
import com.aidiary.diary.model.DiaryResponseBundle.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class DailyDiaryReadServiceImpl implements DailyDiaryReadService {

    private final DiaryDatabaseReadService diaryDatabaseReadService;
    private final HybridEncryptor hybridEncryptor;

    @Override
    public DiaryDetail getDiaryDetail(Long userId, Long diaryId) throws Exception {

        DiariesEntity diariesEntity = diaryDatabaseReadService.findDiaryById(diaryId)
                .orElseThrow(() -> new DiaryException(ErrorCode.DIARY_NOT_FOUND));

        if (!diariesEntity.getUser().isSameUser(userId)) {
            throw new DiaryException(ErrorCode.DIARY_OWNER_MISMATCH);
        }

        if (!diariesEntity.isActivated()) {
            throw new DiaryException(ErrorCode.DIARY_NOT_FOUND);
        }

        String diaryContent = hybridEncryptor.decrypt(diariesEntity.getContent());

        Map<DiaryWordType, List<DiaryWord>> wordsByType = diaryWordsByType(diaryDatabaseReadService.findWordsFromDiary(diariesEntity));
        Map<DiarySentenceType, List<String>> sentencesByType = diarySentencesByType(diaryDatabaseReadService.findSentencesFromDiary(diariesEntity));

        return DiaryDetail.builder()
                .entryDate(diariesEntity.getEntryDate())
                .emotions(
                        DiaryEmotions.builder()
                                .content(sentencesByType.get(DiarySentenceType.EMOTION).get(0))
                                .words(wordsByType.get(DiaryWordType.EMOTION))
                                .build()
                )
                .selfThoughts(
                        DiarySelfThoughts.builder()
                                .content(sentencesByType.get(DiarySentenceType.SELF_THOUGHT).get(0))
                                .words(wordsByType.get(DiaryWordType.SELF_THOUGHT))
                                .build()
                )
                .coreValues(
                        DiaryCoreValues.builder()
                                .content(sentencesByType.get(DiarySentenceType.CORE_VALUE).get(0))
                                .words(wordsByType.get(DiaryWordType.CORE_VALUE))
                                .build()
                )
                .recommendedActions(sentencesByType.get(DiarySentenceType.RECOMMENDED_ACTION))
                .additionals(sentencesByType.get(DiarySentenceType.ADDITIONAL))
                .literarySummary(sentencesByType.get(DiarySentenceType.LITERARY_SUMMARY).get(0))
                .diaryContent(diaryContent)
                .build();
    }

    private Map<DiaryWordType, List<DiaryWord>> diaryWordsByType(List<DailyAnalysisWordsEntity> dailyAnalysisWordsEntities){

        Map<DiaryWordType, List<DiaryWord>> wordsByType = new HashMap<>();

        for (DailyAnalysisWordsEntity dailyAnalysisWordsEntity : dailyAnalysisWordsEntities) {
            List<DiaryWord> words = wordsByType.getOrDefault(dailyAnalysisWordsEntity.getType(), new ArrayList<>());
            words.add(DiaryWord.of(dailyAnalysisWordsEntity));
            wordsByType.put(dailyAnalysisWordsEntity.getType(), words);
        }

        return wordsByType;
    }

    private Map<DiarySentenceType, List<String>> diarySentencesByType(List<DailyAnalysisSentencesEntity> dailyAnalysisSentencesEntities) {

        Map<DiarySentenceType, List<String>> sentencesByType = new HashMap<>();

        for (DailyAnalysisSentencesEntity dailyAnalysisSentencesEntity : dailyAnalysisSentencesEntities) {
            if (Objects.isNull(dailyAnalysisSentencesEntity) || !StringUtils.hasText(dailyAnalysisSentencesEntity.getContent())) continue;
            List<String> sentences = sentencesByType.getOrDefault(dailyAnalysisSentencesEntity.getType(), new ArrayList<>());
            sentences.add(dailyAnalysisSentencesEntity.getContent());
            sentencesByType.put(dailyAnalysisSentencesEntity.getType(), sentences);
        }

        return sentencesByType;
    }

    @Override
    public Long getUserDiaryCount(UserPrincipal userPrincipal) {

        UsersEntity user = UsersEntity.builder().id(userPrincipal.userId()).build();

        return diaryDatabaseReadService.countAllActiveDiariesByUser(user);

    }
}
