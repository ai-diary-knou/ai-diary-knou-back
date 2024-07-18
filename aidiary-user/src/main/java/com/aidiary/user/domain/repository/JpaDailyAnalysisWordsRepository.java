package com.aidiary.user.domain.repository;

import com.aidiary.user.domain.entity.DailyAnalysisWordsEntity;
import com.aidiary.user.domain.entity.DiariesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaDailyAnalysisWordsRepository extends JpaRepository<DailyAnalysisWordsEntity, Long> {

    List<DailyAnalysisWordsEntity> findByDiary(DiariesEntity diariesEntity);

}
