package com.aidiary.user.application;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.UserException;
import com.aidiary.common.vo.ResponseBundle.ResponseResult;
import com.aidiary.user.application.dto.DiaryRequestBundle.*;
import com.aidiary.user.application.dto.DiaryResponseBundle.*;
import com.aidiary.user.application.service.DiaryService;
import com.aidiary.user.domain.entity.UsersEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/diaries")
@Slf4j
public class DiaryController {

    private final DiaryService diaryService;

    @GetMapping("/main-reports")
    public ResponseResult getMainReportsOfDiaries(){

        MainReportResponse mainReportResponse = diaryService.getMainReportsOfDiaries();

        return ResponseResult.success(mainReportResponse);
    }

    @GetMapping("/monthly-reports")
    public ResponseResult getMonthlyReportsOfDiaries(DiariesOfMonthGetRequest request){

        MonthlyReportResponse monthlyReportResponse = diaryService.getMonthlyReportsOfDiaries(request);

        return ResponseResult.success(monthlyReportResponse);
    }

    @PostMapping
    public ResponseResult saveDiary(@AuthenticationPrincipal UsersEntity usersEntity, @RequestBody DiaryCreateRequest request){

        try {
            diaryService.saveDiaryAfterOpenAiAnalysis(usersEntity, request);
            return ResponseResult.success();
        } catch (Exception e) {
            log.info("Error :: ", e);
            throw new UserException(ErrorCode.UNKNOWN_ERROR);
        }

    }

    @PutMapping("/{diaryId}")
    public ResponseResult updateDiary(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long diaryId, @RequestBody DiaryUpdateRequest request){


        return ResponseResult.success();
    }

    @GetMapping("/{diaryId}")
    public ResponseResult getDiaryDetail(@PathVariable Long diaryId){

        return ResponseResult.success();
    }

}
