package com.aidiary.core.service;

import com.aidiary.common.enums.DiaryStatus;
import com.aidiary.core.entity.DailyAnalysisSentencesEntity;
import com.aidiary.core.entity.DailyAnalysisWordsEntity;
import com.aidiary.core.entity.DiariesEntity;
import com.aidiary.core.repository.jpa.JpaDailyAnalysisSentencesRepository;
import com.aidiary.core.repository.jpa.JpaDailyAnalysisWordsRepository;
import com.aidiary.core.repository.jpa.JpaDiariesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
@RequiredArgsConstructor
public class DiaryDatabaseWriteService {

    private final JpaDiariesRepository jpaDiariesRepository;
    private final JpaDailyAnalysisSentencesRepository jpaDailyAnalysisSentencesRepository;
    private final JpaDailyAnalysisWordsRepository jpaDailyAnalysisWordsRepository;

    public DiariesEntity saveDailyDiary(DiariesEntity diariesEntity) {
        return jpaDiariesRepository.save(diariesEntity);
    }

    public DiariesEntity updateDailyDiary(DiariesEntity originalDiariesEntity, DiariesEntity newDiariesEntity){
        originalDiariesEntity.updateStatus(DiaryStatus.INACTIVE);
        return saveDailyDiary(newDiariesEntity);
    }

    public void saveDailyDiaryAnalysisWordsAndSentences(List<DailyAnalysisWordsEntity> dailyAnalysisWordsEntities, List<DailyAnalysisSentencesEntity> dailyAnalysisSentencesEntities){
        jpaDailyAnalysisWordsRepository.saveAll(dailyAnalysisWordsEntities);
        jpaDailyAnalysisSentencesRepository.saveAll(dailyAnalysisSentencesEntities);
    }

}
