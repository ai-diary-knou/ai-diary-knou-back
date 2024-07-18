package com.aidiary.user.application;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.DiaryException;
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

            return ResponseResult.success(diaryService.saveDiaryAfterOpenAiAnalysis(usersEntity, request));

        }  catch (Exception e) {
            log.info("Error :: ", e);
            throw new UserException(ErrorCode.DIARY_REGISTER_FAIL);
        }

    }

    @PutMapping("/{diaryId}")
    public ResponseResult updateDiary(@AuthenticationPrincipal UsersEntity usersEntity, @PathVariable Long diaryId, @RequestBody DiaryUpdateRequest request){

        try {

            return ResponseResult.success(diaryService.updateDiaryAfterOpenAiAnalysis(usersEntity, diaryId, request));

        } catch (Exception e) {
            log.info("Error :: ", e);
            throw new UserException(ErrorCode.DIARY_UPDATE_FAIL);
        }

    }

    @GetMapping("/{diaryId}")
    public ResponseResult getDiaryDetail(@AuthenticationPrincipal UsersEntity usersEntity, @PathVariable Long diaryId){

        try {
            return ResponseResult.success(diaryService.getDiaryDetail(usersEntity.getId(), diaryId));
        } catch (DiaryException e) {
            throw e;
        }  catch (Exception e) {
            log.info("Error ::", e);
            throw new UserException(ErrorCode.UNKNOWN_ERROR);
        }

    }

}
