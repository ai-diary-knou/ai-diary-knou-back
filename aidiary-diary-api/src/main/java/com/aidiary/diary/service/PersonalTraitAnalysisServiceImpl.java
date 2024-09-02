package com.aidiary.diary.service;

import com.aidiary.common.enums.DiarySentenceType;
import com.aidiary.common.enums.DiaryStatus;
import com.aidiary.common.enums.DiaryWordType;
import com.aidiary.common.vo.PagingRequest;
import com.aidiary.common.vo.ResponseBundle.UserPrincipal;
import com.aidiary.core.dto.DiaryDatabaseRequestBundle;
import com.aidiary.core.entity.UsersEntity;
import com.aidiary.core.service.DiaryDatabaseReadService;
import com.aidiary.diary.model.DiaryRequestBundle.*;
import com.aidiary.diary.model.DiaryResponseBundle;
import com.aidiary.diary.model.DiaryResponseBundle.MainReportResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.aidiary.common.enums.DiaryWordType.CORE_VALUE;
import static com.aidiary.common.enums.DiaryWordType.SELF_THOUGHT;

@Service
@Slf4j
@RequiredArgsConstructor
public class PersonalTraitAnalysisServiceImpl implements PersonalTraitAnalysisService {

    private final DiaryDatabaseReadService diaryDatabaseReadService;

    @Override
    public DiaryResponseBundle.MonthlyReportResponse getMonthlyReportsOfDiaries(UserPrincipal userPrincipal, DiariesOfMonthGetRequest request) {

        return DiaryResponseBundle.MonthlyReportResponse.builder()
                .selectedDate(request.getSelectedDate())
                .monthlyDiaryReports(monthlyDiaryReportsOf(userPrincipal, request))
                .build();
    }

    private List<DiaryResponseBundle.DiaryOutline> monthlyDiaryReportsOf(UserPrincipal userPrincipal, DiariesOfMonthGetRequest request) {

        return diaryDatabaseReadService.findByUserAndMonthAndStatusAndType(
                        DiaryDatabaseRequestBundle.CurrentMonthWrittenDiariesRequest.builder()
                                .usersEntity(UsersEntity.builder().id(userPrincipal.userId()).build())
                                .month(request.month())
                                .diaryStatus(DiaryStatus.ACTIVE)
                                .diarySentenceType(DiarySentenceType.LITERARY_SUMMARY)
                                .build()
                ).stream()
                .map(DiaryResponseBundle.DiaryOutline::of)
                .collect(Collectors.toList());
    }

    @Override
    public MainReportResponse getPersonalTraitAnalysisService(UserPrincipal userPrincipal) {

        LocalDate today = LocalDate.now();
        UsersEntity user = UsersEntity.builder().id(userPrincipal.userId()).build();

        return MainReportResponse.builder()
                .recentLiterarySummaries(getRecentDiariesSevenLiterarySummaries(user))
                .recentAverageEmotionScales(getRecentSevenAverageEmotionRates(user))
                .recentRepetitiveKeywords(getMaximumTenRecentRepetitiveCoreValuesAndSelfThoughts(today, user))
                .recentRecommendedActions(getLatestDiaryRecommendedActions(user))
                .build();
    }

    private List<String> getRecentDiariesSevenLiterarySummaries(UsersEntity user) {
        PagingRequest pagingRequest = PagingRequest.of(0, 7, PagingRequest.Sort.by("id"));
        return diaryDatabaseReadService.findSentenceByUserAndTypeAndPage(
                user, DiaryStatus.ACTIVE, DiarySentenceType.LITERARY_SUMMARY, pagingRequest
        );
    }

    private List<BigDecimal> getRecentSevenAverageEmotionRates(UsersEntity user) {
        PagingRequest pagingRequest = PagingRequest.of(0, 7, PagingRequest.Sort.by("id"));
        return diaryDatabaseReadService.findAverageEmotionScalesByUserAndDatesBetween(
                user, DiaryStatus.ACTIVE, DiaryWordType.EMOTION, pagingRequest
        );
    }

    private List<String> getMaximumTenRecentRepetitiveCoreValuesAndSelfThoughts(LocalDate today, UsersEntity user) {
        return diaryDatabaseReadService.findTenRecentRepetitiveKeywordsByUserAndBetween(
                user, DiaryStatus.ACTIVE, List.of(CORE_VALUE, SELF_THOUGHT), today.minusDays(30), today
        );
    }

    private List<String> getLatestDiaryRecommendedActions(UsersEntity user) {
        PagingRequest pagingRequest = PagingRequest.of(0, 1, PagingRequest.Sort.by(PagingRequest.Order.desc("id")));
        return diaryDatabaseReadService.findRecentRecommendedActions(user, DiaryStatus.ACTIVE, DiarySentenceType.RECOMMENDED_ACTION, pagingRequest);
    }



}
