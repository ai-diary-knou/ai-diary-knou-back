package com.aidiary.core.service;

import com.aidiary.common.enums.DiarySentenceType;
import com.aidiary.common.enums.DiaryStatus;
import com.aidiary.common.enums.DiaryWordType;
import com.aidiary.common.vo.PagingRequest;
import com.aidiary.core.dto.DiaryDatabaseRequestBundle.CurrentMonthWrittenDiariesRequest;
import com.aidiary.core.entity.*;
import com.aidiary.core.repository.jpa.JpaDailyAnalysisSentencesRepository;
import com.aidiary.core.repository.jpa.JpaDailyAnalysisWordsRepository;
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
import static com.aidiary.common.enums.DiaryWordType.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiaryDatabaseReadService {

    private final JpaDiariesRepository jpaDiariesRepository;
    private final JpaDailyAnalysisWordsRepository jpaDailyAnalysisWordsRepository;
    private final JpaDailyAnalysisSentencesRepository jpaDailyAnalysisSentencesRepository;
    private final JPAQueryFactory jpaQueryFactory;

    public Optional<DiariesEntity> findDiaryById(Long diaryId) {
        return jpaDiariesRepository.findById(diaryId);
    }

    public Long countAllActiveDiariesByUser(UsersEntity usersEntity) {
        return jpaDiariesRepository.countAllByUserAndStatus(usersEntity, ACTIVE);
    }

    public Optional<DiariesEntity> findActiveDiaryByUserAndEntryDate(UsersEntity user, LocalDate entryDate, DiaryStatus status) {
        return jpaDiariesRepository.findByUserAndEntryDateAndStatus(user, entryDate, ACTIVE);
    }

    public List<DailyAnalysisWordsEntity> findWordsFromDiary(DiariesEntity diariesEntity) {
        return jpaDailyAnalysisWordsRepository.findByDiary(diariesEntity);
    }

    public List<DailyAnalysisSentencesEntity> findSentencesFromDiary(DiariesEntity diariesEntity) {
        return jpaDailyAnalysisSentencesRepository.findByDiary(diariesEntity);
    }

    public List<DailyAnalysisSentencesEntity> findByUserAndMonthAndStatusAndType(CurrentMonthWrittenDiariesRequest request) {

        QDailyAnalysisSentencesEntity qDailyAnalysisSentencesEntity = QDailyAnalysisSentencesEntity.dailyAnalysisSentencesEntity;
        QDiariesEntity qDiariesEntity = QDiariesEntity.diariesEntity;

        return jpaQueryFactory.selectFrom(qDailyAnalysisSentencesEntity)
                .join(qDailyAnalysisSentencesEntity.diary, qDiariesEntity)
                .where(
                        qDiariesEntity.status.eq(request.diaryStatus()),
                        qDailyAnalysisSentencesEntity.user.eq(request.usersEntity()),
                        qDiariesEntity.entryDate.month().eq(request.month()),
                        qDailyAnalysisSentencesEntity.type.eq(request.diarySentenceType())
                )
                .fetch();
    }

    public List<String> findSentenceByUserAndTypeAndPage(UsersEntity usersEntity, DiaryStatus diaryStatus, DiarySentenceType diarySentenceType, PagingRequest pagingRequest) {

        QDailyAnalysisSentencesEntity qDailyAnalysisSentencesEntity = QDailyAnalysisSentencesEntity.dailyAnalysisSentencesEntity;
        QDiariesEntity qDiariesEntity = QDiariesEntity.diariesEntity;

        return jpaQueryFactory.select(qDailyAnalysisSentencesEntity.content)
                .join(qDailyAnalysisSentencesEntity.diary, qDiariesEntity)
                .where(
                        qDailyAnalysisSentencesEntity.user.eq(usersEntity),
                        qDiariesEntity.status.eq(diaryStatus),
                        qDailyAnalysisSentencesEntity.type.eq(diarySentenceType)
                )
                .fetch();
    }

    public List<BigDecimal> findAverageEmotionScalesByUserAndDatesBetween(UsersEntity usersEntity, DiaryStatus diaryStatus, DiaryWordType diaryWordType, PagingRequest pagingRequest) {

        QDailyAnalysisWordsEntity qDailyAnalysisWordsEntity = QDailyAnalysisWordsEntity.dailyAnalysisWordsEntity;
        QDiariesEntity qDiariesEntity = QDiariesEntity.diariesEntity;

        NumberExpression<BigDecimal> averageScale = Expressions.numberTemplate(BigDecimal.class, "avg({0})", qDailyAnalysisWordsEntity.scale);

        return jpaQueryFactory.select(averageScale)
                .from(qDailyAnalysisWordsEntity)
                .join(qDailyAnalysisWordsEntity.diary, qDiariesEntity)
                .where(
                        qDiariesEntity.status.eq(diaryStatus),
                        qDailyAnalysisWordsEntity.type.eq(diaryWordType),
                        qDailyAnalysisWordsEntity.user.eq(usersEntity)
                )
                .groupBy(qDiariesEntity.id)
                .orderBy(QueryDslOrderSpecifier.getOrderSpecifier(pagingRequest, qDiariesEntity))
                .fetch();

    }

    public List<String> findTenRecentRepetitiveKeywordsByUserAndBetween(UsersEntity usersEntity, DiaryStatus diaryStatus, List<DiaryWordType> diaryWordTypes, LocalDate startDate, LocalDate endDate){

        QDailyAnalysisWordsEntity qDailyAnalysisWordsEntity = QDailyAnalysisWordsEntity.dailyAnalysisWordsEntity;
        QDiariesEntity qDiariesEntity = QDiariesEntity.diariesEntity;

        return jpaQueryFactory
                .select(qDailyAnalysisWordsEntity.text)
                .from(qDailyAnalysisWordsEntity)
                .join(qDailyAnalysisWordsEntity.diary, qDiariesEntity)
                .where(
                        qDiariesEntity.status.eq(diaryStatus),
                        qDailyAnalysisWordsEntity.type.in(diaryWordTypes),
                        qDailyAnalysisWordsEntity.user.eq(usersEntity),
                        qDiariesEntity.entryDate.between(startDate, endDate)
                )
                .groupBy(qDailyAnalysisWordsEntity.text)
                .orderBy(qDailyAnalysisWordsEntity.count().desc())
                .limit(10)
                .fetch();

    }

    public List<String> findRecentRecommendedActions(UsersEntity usersEntity, DiaryStatus diaryStatus, DiarySentenceType diarySentenceType, PagingRequest pagingRequest) {

        QDailyAnalysisSentencesEntity qDailyAnalysisSentencesEntity = QDailyAnalysisSentencesEntity.dailyAnalysisSentencesEntity;
        QDiariesEntity qDiariesEntity = QDiariesEntity.diariesEntity;

        return jpaQueryFactory.select(qDailyAnalysisSentencesEntity.content)
                .where(
                        qDiariesEntity.eq(
                                jpaQueryFactory.selectFrom(qDiariesEntity)
                                        .where(
                                                qDiariesEntity.user.eq(usersEntity),
                                                qDiariesEntity.status.eq(diaryStatus)
                                        ).orderBy(QueryDslOrderSpecifier.getOrderSpecifier(pagingRequest, qDiariesEntity))
                        ),
                        qDailyAnalysisSentencesEntity.type.eq(diarySentenceType)
                )
                .fetch();

    }

}
