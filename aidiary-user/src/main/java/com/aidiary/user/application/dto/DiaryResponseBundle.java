package com.aidiary.user.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate entryDate,
        String content
    ){}

}
