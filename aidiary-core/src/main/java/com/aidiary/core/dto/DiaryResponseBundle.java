package com.aidiary.core.dto;

import com.aidiary.core.entity.DailyAnalysisSentencesEntity;
import com.aidiary.core.entity.DailyAnalysisWordsEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public abstract class DiaryResponseBundle {

    @Builder
    public record MainReportResponse(
            List<String> recentLiterarySummaries,
            List<BigDecimal> recentAverageEmotionScales,
            List<String> recentRepetitiveKeywords, // within 30 days
            List<String> recentRecommendedActions
    ){}

    @Builder
    public record MonthlyReportResponse(
          LocalDate selectedDate,
          List<DiaryOutline> monthlyDiaryReports
    ){}

    @Builder
    public record DiaryOutline(
         Long diaryId,
         @JsonFormat(pattern = "yyyy-MM-dd")
         LocalDate entryDate,
         String literarySummary
    ){
        public static DiaryOutline of(DailyAnalysisSentencesEntity dailyAnalysisSentencesEntity) {
            return DiaryOutline.builder()
                    .diaryId(dailyAnalysisSentencesEntity.getDiary().getId())
                    .entryDate(dailyAnalysisSentencesEntity.getDiary().getEntryDate())
                    .literarySummary(dailyAnalysisSentencesEntity.getContent())
                    .build();
        }
    }

    @Builder
    public record DiarySaveRes(
            Long diaryId
    ){}

    @Builder
    public record DiaryDetail(
            @JsonFormat(pattern = "yyyy-MM-dd E", locale = "ko_KR")
            LocalDate entryDate,
            DiaryEmotions emotions,
            DiarySelfThoughts selfThoughts,
            DiaryCoreValues coreValues,
            List<String> recommendedActions,
            List<String> additionals,
            String literarySummary,
            String diaryContent
    ) {

    }

    @Builder
    public record DiaryEmotions(
            String content,
            List<DiaryWord> words
    ) {

    }

    @Builder
    public record DiarySelfThoughts(
            String content,
            List<DiaryWord> words
    ) {

    }

    @Builder
    public record DiaryCoreValues(
            String content,
            List<DiaryWord> words
    ) {

    }

    @Builder
    public record DiaryWord(
            String text,
            int scale
    ) {
        public static DiaryWord of(DailyAnalysisWordsEntity dailyAnalysisWordsEntity) {
            return new DiaryWord(
                    dailyAnalysisWordsEntity.getText(),
                    dailyAnalysisWordsEntity.getScale()
            );
        }
    }

}
