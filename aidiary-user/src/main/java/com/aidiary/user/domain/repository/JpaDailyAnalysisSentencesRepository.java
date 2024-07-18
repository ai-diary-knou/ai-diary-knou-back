package com.aidiary.user.domain.repository;

import com.aidiary.user.domain.entity.DailyAnalysisSentencesEntity;
import com.aidiary.user.domain.entity.DiariesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaDailyAnalysisSentencesRepository extends JpaRepository<DailyAnalysisSentencesEntity, Long> {

    List<DailyAnalysisSentencesEntity> findByDiary(DiariesEntity diariesEntity);

}
