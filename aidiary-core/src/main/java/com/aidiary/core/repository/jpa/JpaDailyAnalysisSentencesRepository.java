package com.aidiary.core.repository.jpa;

import com.aidiary.core.entity.DailyAnalysisSentencesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaDailyAnalysisSentencesRepository extends JpaRepository<DailyAnalysisSentencesEntity, Long>,
        QuerydslPredicateExecutor<DailyAnalysisSentencesEntity> {


}
