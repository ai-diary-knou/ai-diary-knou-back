package com.aidiary.user.domain.repository;

import com.aidiary.user.domain.entity.DailyAnalysisSentencesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaDailyAnalysisSentencesRepository extends JpaRepository<DailyAnalysisSentencesEntity, Long> {


}
