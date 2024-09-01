package com.aidiary.core.service;

import com.aidiary.core.entity.DailyAnalysisWordsEntity;
import com.aidiary.core.repository.JpaDailyAnalysisWordsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
@RequiredArgsConstructor
public class DailyAnalysisWordsDatabaseWriteService {

    private final JpaDailyAnalysisWordsRepository jpaDailyAnalysisWordsRepository;

    public List<DailyAnalysisWordsEntity> saveAll(List<DailyAnalysisWordsEntity> dailyAnalysisWordsEntities){
        return jpaDailyAnalysisWordsRepository.saveAll(dailyAnalysisWordsEntities);
    }

}
