package com.aidiary.diary.service;

import com.aidiary.common.enums.DiarySentenceType;
import com.aidiary.common.enums.DiaryStatus;
import com.aidiary.common.enums.DiaryWordType;
import com.aidiary.common.vo.PagingRequest;
import com.aidiary.common.vo.ResponseBundle.UserPrincipal;
import com.aidiary.core.dto.DiaryDatabaseRequestBundle.*;
import com.aidiary.core.entity.DailyAnalysisSentencesEntity;
import com.aidiary.core.entity.DiariesEntity;
import com.aidiary.core.entity.UsersEntity;
import com.aidiary.core.service.DiaryDatabaseReadService;
import com.aidiary.diary.model.DiaryRequestBundle.*;
import com.aidiary.diary.model.DiaryResponseBundle.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonalTraitAnalysisServiceImplTest {

    @Mock
    private DiaryDatabaseReadService diaryDatabaseReadService;

    @InjectMocks
    private PersonalTraitAnalysisServiceImpl personalTraitAnalysisService;

    private UserPrincipal userPrincipal;
    private UsersEntity user;

    @BeforeEach
    public void setUp() {
        userPrincipal = UserPrincipal.builder().userId(1L).build();
        user = UsersEntity.builder().id(userPrincipal.userId()).build();
    }

    @Test
    public void 월간_다이어리_목록_조회_성공() {

        // given
        DiariesOfMonthGetRequest request = new DiariesOfMonthGetRequest(2024,9,1);

        DiariesEntity diary1 = DiariesEntity.builder().id(1L).entryDate(LocalDate.of(2024, 9, 1)).user(user).status(DiaryStatus.ACTIVE).build();
        DiariesEntity diary2 = DiariesEntity.builder().id(2L).entryDate(LocalDate.of(2024, 9, 2)).user(user).status(DiaryStatus.ACTIVE).build();
        List<DailyAnalysisSentencesEntity> diaryLiterarySummaries = List.of(
                DailyAnalysisSentencesEntity.builder().user(user).diary(diary1).type(DiarySentenceType.LITERARY_SUMMARY).content("맑고 따뜻한 하루의 산책").build(),
                DailyAnalysisSentencesEntity.builder().user(user).diary(diary2).type(DiarySentenceType.LITERARY_SUMMARY).content("행복함과 즐거움이 가득한 하루").build()
        );

        when(diaryDatabaseReadService.findSentencesOfTypeByUserInMonth(any(DiarySentencesOfTypeInMonthRequest.class)))
                .thenReturn(diaryLiterarySummaries);

        // when
        MonthlyReportResponse result = personalTraitAnalysisService.getMonthlyReportsOfDiaries(userPrincipal, request);

        // then
        assertNotNull(result);
        assertEquals(request.getSelectedDate(), result.selectedDate());
        assertEquals(2, result.monthlyDiaryReports().size());
        verify(diaryDatabaseReadService, times(1)).findSentencesOfTypeByUserInMonth(any(DiarySentencesOfTypeInMonthRequest.class));
    }

    @Test
    public void 최근_일기_통해_경향성_분석_성공() {
        // given
        List<String> literarySummaries = List.of("맑고 따뜻한 하루의 산책", "행복함과 즐거움이 가득한 하루", "정신없이 지나간 하루");
        List<BigDecimal> averageEmotionScales = List.of(new BigDecimal("4.5"), new BigDecimal("3.8"));
        List<String> repetitiveKeywords = List.of("성실성", "책임감", "결단력");
        List<String> recommendedActions = List.of("더 자주 운동하기");

        when(diaryDatabaseReadService.findSentencesOfTypeByUserWithinPage(any(DiarySentencesOfTypeWithinPageRequest.class)))
                .thenReturn(literarySummaries)
                .thenReturn(recommendedActions);

        when(diaryDatabaseReadService.findAverageWordsScaleOfTypeByUserWithinPage(any(AverageWordsScaleOfTypeByUserWithinPageRequest.class)))
                .thenReturn(averageEmotionScales);

        when(diaryDatabaseReadService.findTopNRepetitiveWordsOfTypesBetweenDates(any(TopNRepetitiveDiaryWordsOfTypesBetweenDatesRequest.class)))
                .thenReturn(repetitiveKeywords);

        // when
        MainReportResponse result = personalTraitAnalysisService.getPersonalTraitAnalysisService(userPrincipal);

        // then
        assertNotNull(result);
        assertEquals(3, result.recentLiterarySummaries().size());
        assertEquals(2, result.recentAverageEmotionScales().size());
        assertEquals(3, result.recentRepetitiveKeywords().size());
        assertEquals(1, result.recentRecommendedActions().size());
        verify(diaryDatabaseReadService, times(2)).findSentencesOfTypeByUserWithinPage(any(DiarySentencesOfTypeWithinPageRequest.class));
        verify(diaryDatabaseReadService, times(1)).findAverageWordsScaleOfTypeByUserWithinPage(any(AverageWordsScaleOfTypeByUserWithinPageRequest.class));
        verify(diaryDatabaseReadService, times(1)).findTopNRepetitiveWordsOfTypesBetweenDates(any(TopNRepetitiveDiaryWordsOfTypesBetweenDatesRequest.class));
    }

}