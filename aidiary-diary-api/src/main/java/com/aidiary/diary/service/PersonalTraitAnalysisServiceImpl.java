package com.aidiary.diary.service;

import com.aidiary.common.enums.DiarySentenceType;
import com.aidiary.common.enums.DiaryStatus;
import com.aidiary.common.enums.DiaryWordType;
import com.aidiary.common.vo.PagingRequest;
import com.aidiary.common.vo.ResponseBundle.UserPrincipal;
import com.aidiary.core.dto.DiaryDatabaseRequestBundle.*;
import com.aidiary.core.entity.UsersEntity;
import com.aidiary.core.service.DiaryDatabaseReadService;
import com.aidiary.diary.model.DiaryRequestBundle.*;
import com.aidiary.diary.model.DiaryResponseBundle.*;
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
    public MonthlyReportResponse getMonthlyReportsOfDiaries(UserPrincipal userPrincipal, DiariesOfMonthGetRequest request) {

        return MonthlyReportResponse.builder()
                .selectedDate(request.getSelectedDate())
                .monthlyDiaryReports(monthlyDiaryReportsOf(userPrincipal, request))
                .build();
    }

    private List<DiaryOutline> monthlyDiaryReportsOf(UserPrincipal userPrincipal, DiariesOfMonthGetRequest request) {

        return diaryDatabaseReadService.findSentencesOfTypeByUserInMonth(
                        DiarySentencesOfTypeInMonthRequest.builder()
                                .usersEntity(UsersEntity.builder().id(userPrincipal.userId()).build())
                                .year(request.year())
                                .month(request.month())
                                .diaryStatus(DiaryStatus.ACTIVE)
                                .diarySentenceType(DiarySentenceType.LITERARY_SUMMARY)
                                .build()
                ).stream()
                .map(DiaryOutline::of)
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

        PagingRequest pagingRequest = PagingRequest.of(0, 7, PagingRequest.Sort.by("DiariesEntity.id"));

        return diaryDatabaseReadService.findSentencesOfTypeByUserWithinPage(
                DiarySentencesOfTypeWithinPageRequest.builder()
                        .usersEntity(user)
                        .diaryStatus(DiaryStatus.ACTIVE)
                        .diarySentenceType(DiarySentenceType.LITERARY_SUMMARY)
                        .pagingRequest(pagingRequest)
                        .build()
        );
    }

    private List<BigDecimal> getRecentSevenAverageEmotionRates(UsersEntity user) {
        PagingRequest pagingRequest = PagingRequest.of(0, 7, PagingRequest.Sort.by("DiariesEntity.id"));

        return diaryDatabaseReadService.findAverageWordsScaleOfTypeByUserWithinPage(
                AverageWordsScaleOfTypeByUserWithinPageRequest.builder()
                        .usersEntity(user)
                        .diaryStatus(DiaryStatus.ACTIVE)
                        .diaryWordType(DiaryWordType.EMOTION)
                        .pagingRequest(pagingRequest)
                        .build()
        );
    }

    private List<String> getMaximumTenRecentRepetitiveCoreValuesAndSelfThoughts(LocalDate today, UsersEntity user) {

        return diaryDatabaseReadService.findTopNRepetitiveWordsOfTypesBetweenDates(
                TopNRepetitiveDiaryWordsOfTypesBetweenDatesRequest.builder()
                        .usersEntity(user)
                        .diaryStatus(DiaryStatus.ACTIVE)
                        .diaryWordTypes(List.of(CORE_VALUE, SELF_THOUGHT))
                        .startDate(today.minusDays(30))
                        .endDate(today)
                        .limit(10)
                        .build()
        );

    }

    private List<String> getLatestDiaryRecommendedActions(UsersEntity user) {
        PagingRequest pagingRequest = PagingRequest.of(0, 1, PagingRequest.Sort.by(PagingRequest.Order.desc("DiariesEntity.id")));
        return diaryDatabaseReadService.findSentencesOfTypeByUserWithinPage(
                DiarySentencesOfTypeWithinPageRequest.builder()
                        .usersEntity(user)
                        .diaryStatus(DiaryStatus.ACTIVE)
                        .diarySentenceType(DiarySentenceType.RECOMMENDED_ACTION)
                        .pagingRequest(pagingRequest)
                        .build()
        );
    }



}
