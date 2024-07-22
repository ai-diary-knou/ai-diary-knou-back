package com.aidiary.user.domain.repository;

import com.aidiary.common.enums.DiarySentenceType;
import com.aidiary.user.domain.entity.DailyAnalysisSentencesEntity;
import com.aidiary.user.domain.entity.DiariesEntity;
import com.aidiary.user.domain.entity.UsersEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface JpaDailyAnalysisSentencesRepository extends JpaRepository<DailyAnalysisSentencesEntity, Long> {

    List<DailyAnalysisSentencesEntity> findByDiary(DiariesEntity diariesEntity);

    @Query("select s from DailyAnalysisSentencesEntity s join fetch s.diary d " +
            "where d.status = 'ACTIVE' and s.user = :usersEntity " +
            "and FUNCTION('MONTH', d.entryDate) = :month ")
    List<DailyAnalysisSentencesEntity> findByUserAndMonth(UsersEntity usersEntity, int month);

    @Query("select s.content from DailyAnalysisSentencesEntity s " +
            "where s.diary = (select d from DiariesEntity d " +
            "where d.user = :usersEntity and d.status = 'ACTIVE' " +
            "order by d.id desc limit 1) " +
            "and s.type = 'RECOMMENDED_ACTION'")
    List<String> findRecentRecommendedActions(UsersEntity usersEntity);

    @Query("select s.content from DailyAnalysisSentencesEntity s join s.diary d " +
            "where d.status = 'ACTIVE' and s.type = :type and s.user = :usersEntity")
    List<String> findSentenceByUserAndTypeAndPage(UsersEntity usersEntity, DiarySentenceType type, Pageable pageable);

}
