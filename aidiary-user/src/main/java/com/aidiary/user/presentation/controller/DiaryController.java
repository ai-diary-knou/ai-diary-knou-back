package com.aidiary.user.presentation.controller;

import com.aidiary.common.vo.ResponseBundle.ResponseResult;
import com.aidiary.user.application.dto.DiaryRequestBundle.*;
import com.aidiary.user.application.dto.DiaryResponseBundle.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/diaries")
@Slf4j
public class DiaryController {

    @GetMapping("/main-reports")
    public ResponseResult getMainReportsOfDiaries(){

        List<String> recentSevenLiterarySummaries = List.of(
                "가족의 소중함과 친구와의 소중한 대화, 창조적인 활동으로 채워진 하루는 마치 '소년이 온다'의 따뜻한 감성을 떠올리게 한다.",
                "맑은 하늘 아래, 새로운 취미를 통해 자아실현의 즐거움을 맛본다.",
                "가족이 함께하는 이 순간은 마치 헤르만 헤세의 '데미안' 속에서처럼 영원할 것 같다.",
                "하늘에는 구름이 떠다니며, 마음은 자아실현의 길을 향해 걸어가고 있다.",
                "오늘의 하루는 마치 '백설공주'의 악마의 마법 거울과 같았다. 자신의 가치를 발견하고 성장하기 위한 여정이 시작되었다."
        );

        List<BigDecimal> recentSevenAverageEmotionScales = List.of(
                new BigDecimal(7.12).setScale(2, RoundingMode.HALF_UP),
                new BigDecimal(5.5).setScale(2, RoundingMode.HALF_UP),
                new BigDecimal(6.78).setScale(2, RoundingMode.HALF_UP),
                new BigDecimal(6).setScale(2, RoundingMode.HALF_UP),
                new BigDecimal(4.2).setScale(2, RoundingMode.HALF_UP)
        );

        List<String> recentTenRepetitiveKeywords = List.of(
                "가족", "친구", "창조", "평온함", "만족감",
                "피곤함", "커피", "생일", "케이크", "취미"
        );

        List<String> recentRecommendedActions = List.of(
                "친구들과 자주 만나 소중한 대화를 나누기",
                "새로운 취미를 발견하고 창조적인 활동에 시간을 투자하기",
                "가족과 함께 시간 보내며 소중함을 깨달음"
        );

        return ResponseResult.success(
                MainReportResponse.builder()
                        .recentSevenLiterarySummaries(recentSevenLiterarySummaries)
                        .recentSevenAverageEmotionScales(recentSevenAverageEmotionScales)
                        .recentTenRepetitiveKeywords(recentTenRepetitiveKeywords)
                        .recentRecommendedActions(recentRecommendedActions)
                .build()
        );
    }

    @GetMapping("/monthly-reports")
    public ResponseResult getMonthlyReportsOfDiaries(DiariesOfMonthGetRequest request){

        LocalDate selectedDate = LocalDate.now();

        List<String> recentSevenLiterarySummaries = List.of(
                "가족의 소중함과 친구와의 소중한 대화, 창조적인 활동으로 채워진 하루는 마치 '소년이 온다'의 따뜻한 감성을 떠올리게 한다.",
                "맑은 하늘 아래, 새로운 취미를 통해 자아실현의 즐거움을 맛본다.",
                "가족이 함께하는 이 순간은 마치 헤르만 헤세의 '데미안' 속에서처럼 영원할 것 같다.",
                "하늘에는 구름이 떠다니며, 마음은 자아실현의 길을 향해 걸어가고 있다.",
                "오늘의 하루는 마치 '백설공주'의 악마의 마법 거울과 같았다. 자신의 가치를 발견하고 성장하기 위한 여정이 시작되었다."
        );

        List<DiaryOutline> monthlyDiaryReports = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            monthlyDiaryReports.add(
                    DiaryOutline.builder()
                            .entryDate(LocalDate.of(2024, 7, i))
                            .literarySummary(recentSevenLiterarySummaries.get(i))
                            .build()
            );
        }

        return ResponseResult.success(
                MonthlyReportResponse.builder()
                        .selectedDate(selectedDate)
                        .monthlyDiaryReports(monthlyDiaryReports)
                        .build()
        );
    }

    @PostMapping
    public ResponseResult saveDiary(@RequestBody DiaryCreateRequest request){



        return ResponseResult.success();
    }

    @PutMapping("/{diaryId}")
    public ResponseResult updateDiary(@PathVariable Long diaryId, @RequestBody DiaryUpdateRequest request){


        return ResponseResult.success();
    }

    @GetMapping("/{diaryId}")
    public ResponseResult getDiaryDetail(@PathVariable Long diaryId){

        return ResponseResult.success();
    }

}
