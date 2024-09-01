package com.aidiary.database.service;

import com.aidiary.core.entity.DailyAnalysisSentencesEntity;
import com.aidiary.core.repository.JpaDailyAnalysisSentencesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
@RequiredArgsConstructor
public class DailyAnalysisSentencesDatabaseWriteService {

    private final JpaDailyAnalysisSentencesRepository jpaDailyAnalysisSentencesRepository;

    public DailyAnalysisSentencesEntity save(DailyAnalysisSentencesEntity dailyAnalysisSentencesEntity) {
        return jpaDailyAnalysisSentencesRepository.save(dailyAnalysisSentencesEntity);
    }

    public List<DailyAnalysisSentencesEntity> saveAll(List<DailyAnalysisSentencesEntity> dailyAnalysisSentencesEntities) {
        return jpaDailyAnalysisSentencesRepository.saveAll(dailyAnalysisSentencesEntities);
    }

}
