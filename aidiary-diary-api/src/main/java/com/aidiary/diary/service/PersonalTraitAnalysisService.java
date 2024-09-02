package com.aidiary.diary.service;

import com.aidiary.common.vo.ResponseBundle.UserPrincipal;
import com.aidiary.diary.model.DiaryRequestBundle.*;
import com.aidiary.diary.model.DiaryResponseBundle.*;

public interface PersonalTraitAnalysisService {

    MonthlyReportResponse getMonthlyReportsOfDiaries(UserPrincipal userPrincipal, DiariesOfMonthGetRequest request) ;

    MainReportResponse getPersonalTraitAnalysisService(UserPrincipal userPrincipal);

}
