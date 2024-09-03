package com.aidiary.diary.service;

import com.aidiary.common.vo.ResponseBundle.UserPrincipal;
import com.aidiary.diary.model.DiaryRequestBundle.DiariesOfMonthGetRequest;
import com.aidiary.diary.model.DiaryResponseBundle;

public interface DailyDiaryReadService {

    DiaryResponseBundle.DiaryDetail getDiaryDetail(UserPrincipal userPrincipal, Long diaryId) throws Exception;

    Long getUserDiaryCount(UserPrincipal userPrincipal);

}
