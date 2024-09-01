package com.aidiary.user.controller;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.DiaryException;
import com.aidiary.common.vo.ResponseBundle.ResponseResult;
import com.aidiary.common.vo.ResponseBundle.UserPrincipal;
import com.aidiary.user.model.DiaryRequestBundle.DiariesOfMonthGetRequest;
import com.aidiary.user.model.DiaryRequestBundle.DiaryCreateRequest;
import com.aidiary.user.model.DiaryRequestBundle.DiaryUpdateRequest;
import com.aidiary.user.model.DiaryResponseBundle.MainReportResponse;
import com.aidiary.user.model.DiaryResponseBundle.MonthlyReportResponse;
import com.aidiary.user.service.DiaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/diaries")
@Slf4j
public class DiaryController {

    private final DiaryService diaryService;

    @GetMapping("/main-reports")
    public ResponseResult getMainReportsOfDiaries(@RequestAttribute("userPrincipal") UserPrincipal userPrincipal) {

        MainReportResponse mainReportResponse = diaryService.getMainReportsOfDiaries(userPrincipal);

        return ResponseResult.success(mainReportResponse);
    }

    @GetMapping("/monthly-reports")
    public ResponseResult getMonthlyReportsOfDiaries(@RequestAttribute("userPrincipal") UserPrincipal userPrincipal, DiariesOfMonthGetRequest request){

        MonthlyReportResponse monthlyReportResponse = diaryService.getMonthlyReportsOfDiaries(userPrincipal, request);

        return ResponseResult.success(monthlyReportResponse);
    }

    @PostMapping
    public ResponseResult saveDiary(@RequestAttribute("userPrincipal") UserPrincipal userPrincipal, @RequestBody DiaryCreateRequest request){

        try {

            return ResponseResult.success(diaryService.saveDiaryAfterOpenAiAnalysis(userPrincipal, request));

        } catch (DiaryException e) {
            throw e;
        } catch (Exception e) {
            log.info("Error :: ", e);
            throw new DiaryException(ErrorCode.DIARY_REGISTER_FAIL);
        }

    }

    @PutMapping("/{diaryId}")
    public ResponseResult updateDiary(@RequestAttribute("userPrincipal") UserPrincipal userPrincipal, @PathVariable Long diaryId, @RequestBody DiaryUpdateRequest request){

        try {

            return ResponseResult.success(diaryService.updateDiaryAfterOpenAiAnalysis(userPrincipal, diaryId, request));

        } catch (DiaryException e) {
            throw e;
        } catch (Exception e) {
            log.info("Error :: ", e);
            throw new DiaryException(ErrorCode.DIARY_UPDATE_FAIL);
        }

    }

    @GetMapping("/{diaryId}")
    public ResponseResult getDiaryDetail(@RequestAttribute("userPrincipal") UserPrincipal userPrincipal, @PathVariable Long diaryId){

        try {

            return ResponseResult.success(diaryService.getDiaryDetail(userPrincipal.userId(), diaryId));

        } catch (DiaryException e) {
            throw e;
        } catch (Exception e) {
            log.info("Error ::", e);
            throw new DiaryException(ErrorCode.UNKNOWN_ERROR);
        }

    }

    @GetMapping("/count")
    public ResponseResult getUserDiaryCount(@RequestAttribute("userPrincipal") UserPrincipal userPrincipal){

        return ResponseResult.success(diaryService.getUserDiaryCount(userPrincipal));
    }

}
