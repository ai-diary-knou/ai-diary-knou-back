package com.aidiary.diary.service.processor;

import com.aidiary.common.enums.DiarySentenceType;
import com.aidiary.common.enums.DiaryWordType;
import com.aidiary.common.utils.HybridEncryptor;
import com.aidiary.core.entity.DailyAnalysisSentencesEntity;
import com.aidiary.core.entity.DailyAnalysisWordsEntity;
import com.aidiary.core.entity.DiariesEntity;
import com.aidiary.diary.model.DiaryResponseBundle.*;
import com.aidiary.diary.model.DiaryResponseBundle.DiaryDetail.DiaryDetailBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

@Component
@RequiredArgsConstructor
public class DailyDiaryContentProcessor extends AbstractDiaryContentProcessor{

    private final HybridEncryptor hybridEncryptor;
    private final List<DiaryContentProcessor> contentProcessors = Arrays.asList(
            new EmotionContentProcessor(),
            new SelfThoughtContentProcessor(),
            new CoreValueContentProcessor(),
            new RecommendedActionContentProcessor(),
            new AdditionalContentProcessor(),
            new LiterarySummaryContentProcessor()
    );

    public DiaryDetail process(
            DiariesEntity diariesEntity,
            List<DailyAnalysisWordsEntity> words,
            List<DailyAnalysisSentencesEntity> sentences
    ) throws Exception {

        DiaryDetail.DiaryDetailBuilder builder = DiaryDetail.builder()
                .entryDate(diariesEntity.getEntryDate())
                .diaryContent(hybridEncryptor.decrypt(diariesEntity.getContent()));

        process(builder, diariesEntity, diaryWordsByType(words), diarySentencesByType(sentences));

        return builder.build();
    }

    @Override
    public void process(DiaryDetailBuilder builder,
                        DiariesEntity diariesEntity,
                        Map<DiaryWordType, List<DiaryWord>> wordsByType,
                        Map<DiarySentenceType, List<String>> sentencesByType
    ) {

        for (DiaryContentProcessor processor : contentProcessors) {
            processor.process(builder, diariesEntity, wordsByType, sentencesByType);
        }
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

}
