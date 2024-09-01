package com.aidiary.core.service;

import com.aidiary.common.vo.PagingRequest;
import com.aidiary.core.dto.JpaPagingRequest;
import com.aidiary.core.entity.DailyAnalysisWordsEntity;
import com.aidiary.core.entity.DiariesEntity;
import com.aidiary.core.entity.UsersEntity;
import com.aidiary.core.repository.JpaDailyAnalysisWordsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DailyAnalysisWordsDatabaseReadService {

    private final JpaDailyAnalysisWordsRepository jpaDailyAnalysisWordsRepository;

    public List<DailyAnalysisWordsEntity> findByDiary(DiariesEntity diariesEntity) {
        return jpaDailyAnalysisWordsRepository.findByDiary(diariesEntity);
    }

    public List<BigDecimal> findAverageEmotionScalesByUserAndBetween(UsersEntity usersEntity, PagingRequest pagingRequest){
        return jpaDailyAnalysisWordsRepository.findAverageEmotionScalesByUserAndBetween(usersEntity, JpaPagingRequest.of(pagingRequest));
    }

    public List<String> findTenRecentRepetitiveKeywordsByUserAndBetween(UsersEntity usersEntity, LocalDate startDate, LocalDate endDate){
        return jpaDailyAnalysisWordsRepository.findTenRecentRepetitiveKeywordsByUserAndBetween(usersEntity, startDate, endDate);
    }



}
