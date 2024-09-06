package com.aidiary.diary.service;

import com.aidiary.common.vo.ResponseBundle.UserPrincipal;
import com.aidiary.diary.model.DiaryRequestBundle.DiaryCreateRequest;
import com.aidiary.diary.model.DiaryRequestBundle.DiaryUpdateRequest;
import com.aidiary.diary.model.DiaryResponseBundle;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public interface DailyDiaryWriteService {

    DiaryResponseBundle.DiarySaveRes saveDiaryAndAnalyzeByAi(UserPrincipal userPrincipal, DiaryCreateRequest request) throws Exception;

    DiaryResponseBundle.DiarySaveRes updateDiaryAfterOpenAiAnalysis(UserPrincipal userPrincipal, Long diaryId, DiaryUpdateRequest request) throws Exception;

}
