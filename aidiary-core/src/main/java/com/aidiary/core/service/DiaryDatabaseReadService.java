package com.aidiary.core.service;

import com.aidiary.common.enums.DiaryStatus;
import com.aidiary.core.dto.DiaryDatabaseRequestBundle.AverageWordsScaleOfTypeByUserWithinPageRequest;
import com.aidiary.core.dto.DiaryDatabaseRequestBundle.DiarySentencesOfTypeInMonthRequest;
import com.aidiary.core.dto.DiaryDatabaseRequestBundle.DiarySentencesOfTypeWithinPageRequest;
import com.aidiary.core.dto.DiaryDatabaseRequestBundle.TopNRepetitiveDiaryWordsOfTypesBetweenDatesRequest;
import com.aidiary.core.entity.*;
import com.aidiary.core.repository.jpa.JpaDiariesRepository;
import com.aidiary.core.utils.QueryDslOrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.aidiary.common.enums.DiaryStatus.ACTIVE;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiaryDatabaseReadService {

    private final JpaDiariesRepository jpaDiariesRepository;
    private final JPAQueryFactory jpaQueryFactory;

    public Optional<DiariesEntity> findDiaryById(Long diaryId) {
        return jpaDiariesRepository.findById(diaryId);
    }

    public Long countAllActiveDiariesByUser(UsersEntity usersEntity) {
        return jpaDiariesRepository.countAllByUserAndStatus(usersEntity, ACTIVE);
    }

    public Optional<DiariesEntity> findActiveDiaryByUserAndEntryDate(UsersEntity user, LocalDate entryDate, DiaryStatus status) {
        return jpaDiariesRepository.findByUserAndEntryDateAndStatus(user, entryDate, status);
    }

    public List<DailyAnalysisWordsEntity> findWordsFromDiary(DiariesEntity diariesEntity) {

        QDailyAnalysisWordsEntity qDailyAnalysisWordsEntity = QDailyAnalysisWordsEntity.dailyAnalysisWordsEntity;
        QDiariesEntity qDiariesEntity = QDiariesEntity.diariesEntity;

        return jpaQueryFactory.selectFrom(qDailyAnalysisWordsEntity)
                .join(qDailyAnalysisWordsEntity.diary, qDiariesEntity)
                .where(
                        qDiariesEntity.eq(diariesEntity),
                        qDiariesEntity.status.eq(ACTIVE)
                )
                .fetch();
    }

    public List<DailyAnalysisSentencesEntity> findSentencesFromDiary(DiariesEntity diariesEntity) {

        QDailyAnalysisSentencesEntity qDailyAnalysisSentencesEntity = QDailyAnalysisSentencesEntity.dailyAnalysisSentencesEntity;
        QDiariesEntity qDiariesEntity = QDiariesEntity.diariesEntity;

        return jpaQueryFactory.selectFrom(qDailyAnalysisSentencesEntity)
                .join(qDailyAnalysisSentencesEntity.diary, qDiariesEntity)
                .where(
                        qDiariesEntity.eq(diariesEntity),
                        qDiariesEntity.status.eq(ACTIVE)
                )
                .fetch();
    }

    public List<DailyAnalysisSentencesEntity> findSentencesOfTypeByUserInMonth(DiarySentencesOfTypeInMonthRequest request) {

        QDailyAnalysisSentencesEntity qDailyAnalysisSentencesEntity = QDailyAnalysisSentencesEntity.dailyAnalysisSentencesEntity;
        QDiariesEntity qDiariesEntity = QDiariesEntity.diariesEntity;

        return jpaQueryFactory.selectFrom(qDailyAnalysisSentencesEntity)
                .join(qDailyAnalysisSentencesEntity.diary, qDiariesEntity)
                .where(
                        qDiariesEntity.user.eq(request.usersEntity()),
                        qDiariesEntity.status.eq(request.diaryStatus()),
                        qDiariesEntity.entryDate.year().eq(request.year()),
                        qDiariesEntity.entryDate.month().eq(request.month()),
                        qDailyAnalysisSentencesEntity.type.eq(request.diarySentenceType())
                )
                .fetch();
    }

    public List<String> findSentencesOfTypeByUserWithinPage(DiarySentencesOfTypeWithinPageRequest request) {

        QDailyAnalysisSentencesEntity qDailyAnalysisSentencesEntity = QDailyAnalysisSentencesEntity.dailyAnalysisSentencesEntity;
        QDiariesEntity qDiariesEntity = QDiariesEntity.diariesEntity;

        return jpaQueryFactory.select(qDailyAnalysisSentencesEntity.content)
                .from(qDailyAnalysisSentencesEntity)
                .join(qDailyAnalysisSentencesEntity.diary, qDiariesEntity)
                .where(
                        qDiariesEntity.user.eq(request.usersEntity()),
                        qDiariesEntity.status.eq(request.diaryStatus()),
                        qDailyAnalysisSentencesEntity.type.eq(request.diarySentenceType())
                )
                .orderBy(QueryDslOrderSpecifier.getOrderSpecifier(request.pagingRequest()))
                .fetch();
    }

    public List<BigDecimal> findAverageWordsScaleOfTypeByUserWithinPage(AverageWordsScaleOfTypeByUserWithinPageRequest request) {

        QDailyAnalysisWordsEntity qDailyAnalysisWordsEntity = QDailyAnalysisWordsEntity.dailyAnalysisWordsEntity;
        QDiariesEntity qDiariesEntity = QDiariesEntity.diariesEntity;

        NumberExpression<BigDecimal> averageScale = Expressions.numberTemplate(BigDecimal.class, "avg({0})", qDailyAnalysisWordsEntity.scale);

        return jpaQueryFactory.select(averageScale)
                .from(qDailyAnalysisWordsEntity)
                .join(qDailyAnalysisWordsEntity.diary, qDiariesEntity)
                .where(
                        qDiariesEntity.user.eq(request.usersEntity()),
                        qDiariesEntity.status.eq(request.diaryStatus()),
                        qDailyAnalysisWordsEntity.type.eq(request.diaryWordType())
                )
                .groupBy(qDiariesEntity.id)
                .orderBy(QueryDslOrderSpecifier.getOrderSpecifier(request.pagingRequest()))
                .fetch();

    }

    public List<String> findTopNRepetitiveWordsOfTypesBetweenDates(TopNRepetitiveDiaryWordsOfTypesBetweenDatesRequest request){

        QDailyAnalysisWordsEntity qDailyAnalysisWordsEntity = QDailyAnalysisWordsEntity.dailyAnalysisWordsEntity;
        QDiariesEntity qDiariesEntity = QDiariesEntity.diariesEntity;

        return jpaQueryFactory
                .select(qDailyAnalysisWordsEntity.text)
                .from(qDailyAnalysisWordsEntity)
                .join(qDailyAnalysisWordsEntity.diary, qDiariesEntity)
                .where(
                        qDailyAnalysisWordsEntity.user.eq(request.usersEntity()),
                        qDiariesEntity.status.eq(request.diaryStatus()),
                        qDailyAnalysisWordsEntity.type.in(request.diaryWordTypes()),
                        qDiariesEntity.entryDate.between(request.startDate(), request.endDate())
                )
                .groupBy(qDailyAnalysisWordsEntity.text)
                .orderBy(qDailyAnalysisWordsEntity.count().desc())
                .limit(request.limit())
                .fetch();

    }

}
