package com.aidiary.diary.service;

import com.aidiary.common.vo.ResponseBundle.UserPrincipal;
import com.aidiary.diary.model.DiaryRequestBundle.DiaryCreateRequest;
import com.aidiary.diary.model.DiaryRequestBundle.DiaryUpdateRequest;
import com.aidiary.diary.model.DiaryResponseBundle;

public interface DailyDiaryWriteService {

    DiaryResponseBundle.DiarySaveRes saveDiaryAfterOpenAiAnalysis(UserPrincipal userPrincipal, DiaryCreateRequest request) throws Exception;

    DiaryResponseBundle.DiarySaveRes updateDiaryAfterOpenAiAnalysis(UserPrincipal userPrincipal, Long diaryId, DiaryUpdateRequest request) throws Exception;

}
