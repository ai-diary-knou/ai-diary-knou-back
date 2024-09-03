package com.aidiary.core.repository.jpa;

import com.aidiary.core.entity.DailyAnalysisWordsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaDailyAnalysisWordsRepository extends JpaRepository<DailyAnalysisWordsEntity, Long>,
        QuerydslPredicateExecutor<DailyAnalysisWordsEntity> {


}
