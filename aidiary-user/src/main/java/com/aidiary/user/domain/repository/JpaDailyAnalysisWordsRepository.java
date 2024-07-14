package com.aidiary.user.domain.repository;

import com.aidiary.user.domain.entity.DailyAnalysisWordsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaDailyAnalysisWordsRepository extends JpaRepository<DailyAnalysisWordsEntity, Long> {


}
