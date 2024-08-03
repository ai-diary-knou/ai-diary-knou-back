package com.aidiary.user.domain.repository;

import com.aidiary.user.domain.entity.DailyAnalysisWordsEntity;
import com.aidiary.user.domain.entity.DiariesEntity;
import com.aidiary.user.domain.entity.UsersEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface JpaDailyAnalysisWordsRepository extends JpaRepository<DailyAnalysisWordsEntity, Long> {

    List<DailyAnalysisWordsEntity> findByDiary(DiariesEntity diariesEntity);

    @Query( "select avg(w.scale) from DailyAnalysisWordsEntity w " +
            "join w.diary as d " +
            "where d.status = 'ACTIVE' and w.type = 'EMOTION' and w.user = :usersEntity " +
            "group by d.id")
    List<BigDecimal> findAverageEmotionScalesByUserAndBetween(UsersEntity usersEntity, Pageable pageable);

    @Query("select w.text from DailyAnalysisWordsEntity w join w.diary d " +
            "where d.status = 'ACTIVE' and (w.type = 'CORE_VALUE' or w.type = 'SELF_THOUGHT') and w.user = :usersEntity " +
            "and d.entryDate >= :startDate and d.entryDate <= :endDate " +
            "group by w.text order by count(*) desc limit 10")
    List<String> findTenRecentRepetitiveKeywordsByUserAndBetween(UsersEntity usersEntity, LocalDate startDate, LocalDate endDate);

}
