package com.aidiary.core.service;

import com.aidiary.common.enums.DiarySentenceType;
import com.aidiary.common.enums.DiaryStatus;
import com.aidiary.common.vo.PagingRequest;
import com.aidiary.core.dto.JpaPagingRequest;
import com.aidiary.core.entity.*;
import com.aidiary.core.repository.JpaDailyAnalysisSentencesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DailyAnalysisSentencesDatabaseReadService {

    private final JpaDailyAnalysisSentencesRepository jpaDailyAnalysisSentencesRepository;

    public List<DailyAnalysisSentencesEntity> findByDiary(DiariesEntity diariesEntity) {
        return jpaDailyAnalysisSentencesRepository.findByDiary(diariesEntity);
    }

    public List<String> findSentenceByUserAndTypeAndPage(UsersEntity usersEntity, DiarySentenceType type, PagingRequest pagingRequest){

        return jpaDailyAnalysisSentencesRepository.findSentenceByUserAndTypeAndPage(usersEntity, type, JpaPagingRequest.of(pagingRequest));
    }

    public List<String> findRecentRecommendedActions(UsersEntity usersEntity){
        return jpaDailyAnalysisSentencesRepository.findRecentRecommendedActions(usersEntity);
    }

    public List<DailyAnalysisSentencesEntity> findByUserAndMonthAndStatusAndType(UsersEntity usersEntity, int month, DiaryStatus status, DiarySentenceType type){
        return jpaDailyAnalysisSentencesRepository.findByUserAndMonthAndStatusAndType(usersEntity, month, status, type);
    }

}
