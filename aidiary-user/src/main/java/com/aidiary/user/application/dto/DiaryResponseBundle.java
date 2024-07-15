package com.aidiary.user.application.dto;

import com.aidiary.user.infrastructure.transport.response.OpenAiResponseBundle.OpenAiCoreValues;
import com.aidiary.user.infrastructure.transport.response.OpenAiResponseBundle.OpenAiEmotions;
import com.aidiary.user.infrastructure.transport.response.OpenAiResponseBundle.OpenAiSelfThoughts;
import com.aidiary.user.infrastructure.transport.response.OpenAiResponseBundle.OpenAiWord;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public abstract class DiaryResponseBundle {

    @Builder
    public record MainReportResponse(
         List<String> recentSevenLiterarySummaries,
         List<BigDecimal> recentSevenAverageEmotionScales,
         List<String> recentTenRepetitiveKeywords, // within 30 days
         List<String> recentRecommendedActions
    ){}

    @Builder
    public record MonthlyReportResponse(
          LocalDate selectedDate,
          List<DiaryOutline> monthlyDiaryReports
    ){}

    @Builder
    public record DiaryOutline(
         @JsonFormat(pattern = "yyyy-MM-dd")
         LocalDate entryDate,
         String literarySummary
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

    public record DiaryEmotions(
            String content,
            List<DiaryWord> words
    ) {
        public static DiaryEmotions fromOpenAiEmotions(OpenAiEmotions openAiEmotions) {
            return new DiaryEmotions(
                    openAiEmotions.content(),
                    openAiEmotions.words().stream().map(DiaryWord::fromOpenAiWord).collect(Collectors.toList())
            );
        }
    }

    public record DiarySelfThoughts(
            String content,
            List<DiaryWord> words
    ) {
        public static DiarySelfThoughts fromOpenAiSelfThoughts(OpenAiSelfThoughts openAiSelfThoughts) {
            return new DiarySelfThoughts(
                    openAiSelfThoughts.content(),
                    openAiSelfThoughts.words().stream().map(DiaryWord::fromOpenAiWord).collect(Collectors.toList())
            );
        }
    }

    public record DiaryCoreValues(
            String content,
            List<DiaryWord> words
    ) {
        public static DiaryCoreValues fromOpenAiCoreValues(OpenAiCoreValues openAiCoreValues) {
            return new DiaryCoreValues(
                    openAiCoreValues.content(),
                    openAiCoreValues.words().stream().map(DiaryWord::fromOpenAiWord).collect(Collectors.toList())
            );
        }
    }

    public record DiaryWord(
            String text,
            int scale
    ) {
        public static DiaryWord fromOpenAiWord(OpenAiWord openAiWord) {
            return new DiaryWord(
                    openAiWord.text(),
                    openAiWord.scale()
            );
        }
    }

}
