package com.aidiary.core.service;

import com.aidiary.common.enums.DiarySentenceType;
import com.aidiary.common.enums.DiaryStatus;
import com.aidiary.common.enums.DiaryWordType;
import com.aidiary.common.enums.UserStatus;
import com.aidiary.common.vo.PagingRequest;
import com.aidiary.core.dto.DiaryDatabaseRequestBundle.AverageWordsScaleOfTypeByUserWithinPageRequest;
import com.aidiary.core.dto.DiaryDatabaseRequestBundle.DiarySentencesOfTypeInMonthRequest;
import com.aidiary.core.dto.DiaryDatabaseRequestBundle.DiarySentencesOfTypeWithinPageRequest;
import com.aidiary.core.dto.DiaryDatabaseRequestBundle.TopNRepetitiveDiaryWordsOfTypesBetweenDatesRequest;
import com.aidiary.core.entity.DailyAnalysisSentencesEntity;
import com.aidiary.core.entity.DailyAnalysisWordsEntity;
import com.aidiary.core.entity.DiariesEntity;
import com.aidiary.core.entity.UsersEntity;
import com.aidiary.core.repository.jpa.JpaDailyAnalysisSentencesRepository;
import com.aidiary.core.repository.jpa.JpaDailyAnalysisWordsRepository;
import com.aidiary.core.repository.jpa.JpaDiariesRepository;
import com.aidiary.core.repository.jpa.JpaUsersRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.aidiary.common.enums.DiaryStatus.ACTIVE;
import static com.aidiary.common.enums.DiaryStatus.INACTIVE;
import static com.aidiary.common.enums.DiaryWordType.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class DiaryDatabaseReadServiceTest {

    @Autowired
    private DiaryDatabaseReadService diaryDatabaseReadService;

    @Autowired
    private JpaUsersRepository jpaUsersRepository;

    @Autowired
    private JpaDiariesRepository jpaDiariesRepository;

    @Autowired
    private JpaDailyAnalysisWordsRepository jpaDailyAnalysisWordsRepository;

    @Autowired
    private JpaDailyAnalysisSentencesRepository jpaDailyAnalysisSentencesRepository;

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    public String diaryContentSample(){
        return "오늘은 날씨가 맑고 상쾌했어요. 친구와 함께 공원에서 산책하며 오랜만에 즐거운 시간을 보냈습니다.";
    }

    public UsersEntity user(){
        return jpaUsersRepository.save(
                UsersEntity.builder()
                        .email("testJpaReadTest@gmail.com")
                        .nickname("jpaReadTest")
                        .password("password")
                        .status(UserStatus.ACTIVE)
                        .loginAttemptCnt(0)
                        .build()
        );
    }

    public DiariesEntity diary(UsersEntity user){
        return diary(user, LocalDate.now());
    }

    public DiariesEntity diary(UsersEntity user, LocalDate entryDate){
        return diary(user, entryDate, ACTIVE);
    }

    public DiariesEntity diary(UsersEntity user, LocalDate entryDate, DiaryStatus status){
        return DiariesEntity.builder()
                .user(user)
                .content(diaryContentSample())
                .entryDate(entryDate)
                .status(status)
                .build();
    }

    @Test
    public void 다이어리_아이디로_상세_조회() {

        // given
        DiariesEntity diary = jpaDiariesRepository.save(diary(user()));

        // when
        Optional<DiariesEntity> foundDiary = diaryDatabaseReadService.findDiaryById(diary.getId());

        // then
        assertThat(foundDiary).isPresent();
        assertThat(foundDiary.get().getId()).isEqualTo(diary.getId());

    }

    @Test
    public void 사용자_다이어리_작성수() {

        // given
        UsersEntity user = user();
        jpaDiariesRepository.save(diary(user, LocalDate.of(2024, 8, 1)));
        jpaDiariesRepository.save(diary(user, LocalDate.of(2024, 8, 2)));

        // when
        Long count = diaryDatabaseReadService.countAllActiveDiariesByUser(user);

        // then
        assertThat(count).isGreaterThan(0);
        assertThat(count).isEqualTo(2);
    }

    @Test
    public void 다이어리_사용자_및_특정일자로_상세_조회() {

        // given
        UsersEntity user = user();
        jpaDiariesRepository.save(diary(user, LocalDate.of(2024, 8, 1), ACTIVE));
        jpaDiariesRepository.save(diary(user, LocalDate.of(2024, 8, 1), INACTIVE));
        jpaDiariesRepository.save(diary(user, LocalDate.of(2024, 8, 2)));

        // when
        Optional<DiariesEntity> foundDiary = diaryDatabaseReadService.findActiveDiaryByUserAndEntryDate(user, LocalDate.of(2024, 8, 1), ACTIVE);

        // then
        assertThat(foundDiary).isPresent();
        assertThat(foundDiary.get().getEntryDate()).isEqualTo(LocalDate.of(2024, 8, 1));
        assertThat(foundDiary.get().getUser()).isEqualTo(user);
        assertThat(foundDiary.get().getStatus()).isEqualTo(ACTIVE);

    }

    @Test
    public void 다이러리_단어_목록_조회() {

        // given
        UsersEntity user = user();
        DiariesEntity diary = jpaDiariesRepository.save(diary(user, LocalDate.of(2024, 8, 1), ACTIVE));

        jpaDailyAnalysisWordsRepository.save(DailyAnalysisWordsEntity.builder().user(user).diary(diary).type(EMOTION).text("친밀감").scale(7).build());
        jpaDailyAnalysisWordsRepository.save(DailyAnalysisWordsEntity.builder().user(user).diary(diary).type(EMOTION).text("편안함").scale(7).build());
        jpaDailyAnalysisWordsRepository.save(DailyAnalysisWordsEntity.builder().user(user).diary(diary).type(SELF_THOUGHT).text("긍정적인 사람").scale(8).build());
        jpaDailyAnalysisWordsRepository.save(DailyAnalysisWordsEntity.builder().user(user).diary(diary).type(SELF_THOUGHT).text("객관적인 사람").scale(8).build());
        jpaDailyAnalysisWordsRepository.save(DailyAnalysisWordsEntity.builder().user(user).diary(diary).type(CORE_VALUE).text("사랑").scale(7).build());
        jpaDailyAnalysisWordsRepository.save(DailyAnalysisWordsEntity.builder().user(user).diary(diary).type(CORE_VALUE).text("가족").scale(8).build());

        // when
        List<DailyAnalysisWordsEntity> words = diaryDatabaseReadService.findWordsFromDiary(diary);

        // then
        assertThat(words).isNotEmpty();
        assertThat(words.size()).isEqualTo(6);
        assertThat(words.get(0).getDiary()).isEqualTo(diary);
        assertThat(words.get(0).getText()).isEqualTo("친밀감");
        assertThat(words.get(3).getText()).isEqualTo("객관적인 사람");

    }

    @Test
    public void 다이러리_단어_목록_조회_데이터_없음() {

        // given
        UsersEntity user = user();
        DiariesEntity diary = jpaDiariesRepository.save(diary(user, LocalDate.of(2024, 8, 1), INACTIVE));
        jpaDailyAnalysisWordsRepository.save(DailyAnalysisWordsEntity.builder().user(user).diary(diary).type(EMOTION).text("친밀감").scale(7).build());
        jpaDailyAnalysisWordsRepository.save(DailyAnalysisWordsEntity.builder().user(user).diary(diary).type(EMOTION).text("편안함").scale(7).build());

        // when
        List<DailyAnalysisWordsEntity> words = diaryDatabaseReadService.findWordsFromDiary(diary);

        // then
        assertThat(words).isEmpty();

    }

    @Test
    public void 다이러리_문장_목록_조회() {

        // given
        UsersEntity user = user();
        DiariesEntity diary = jpaDiariesRepository.save(diary(user, LocalDate.of(2024, 8, 1), ACTIVE));

        jpaDailyAnalysisSentencesRepository.save(DailyAnalysisSentencesEntity.builder().user(user).diary(diary).type(DiarySentenceType.LITERARY_SUMMARY).content("맑고 따뜻한 하루의 산책").build());
        jpaDailyAnalysisSentencesRepository.save(DailyAnalysisSentencesEntity.builder().user(user).diary(diary).type(DiarySentenceType.EMOTION).content("행복함과 즐거움이 가득한 하루").build());
        jpaDailyAnalysisSentencesRepository.save(DailyAnalysisSentencesEntity.builder().user(user).diary(diary).type(DiarySentenceType.SELF_THOUGHT).content("긍정적인 하루를 바라보는").build());
        jpaDailyAnalysisSentencesRepository.save(DailyAnalysisSentencesEntity.builder().user(user).diary(diary).type(DiarySentenceType.RECOMMENDED_ACTION).content("맑은 날씨를 즐기기").build());

        // when
        List<DailyAnalysisSentencesEntity> sentences = diaryDatabaseReadService.findSentencesFromDiary(diary);

        // then
        assertThat(sentences).isNotEmpty();
        assertThat(sentences.size()).isEqualTo(4);
        assertThat(sentences.get(0).getUser()).isEqualTo(user);
        assertThat(sentences.get(0).getDiary()).isEqualTo(diary);
        assertThat(sentences.get(0).getContent()).isEqualTo("맑고 따뜻한 하루의 산책");
        assertThat(sentences.get(3).getContent()).isEqualTo("맑은 날씨를 즐기기");
    }

    @Test
    public void 다이러리_문장_목록_조회_데이터_없음() {

        // given
        UsersEntity user = user();
        DiariesEntity diary = jpaDiariesRepository.save(diary(user, LocalDate.of(2024, 8, 1), INACTIVE));
        jpaDailyAnalysisSentencesRepository.save(DailyAnalysisSentencesEntity.builder().user(user).diary(diary).type(DiarySentenceType.LITERARY_SUMMARY).content("맑고 따뜻한 하루의 산책").build());
        jpaDailyAnalysisSentencesRepository.save(DailyAnalysisSentencesEntity.builder().user(user).diary(diary).type(DiarySentenceType.EMOTION).content("행복함과 즐거움이 가득한 하루").build());


        // when
        List<DailyAnalysisSentencesEntity> sentences = diaryDatabaseReadService.findSentencesFromDiary(diary);

        // then
        assertThat(sentences).isEmpty();
    }

    @Test
    public void 특정_월의_특정_타입_다이어리_문장_목록_조회() {

        // given
        UsersEntity user = user();
        DiariesEntity diary = jpaDiariesRepository.save(diary(user, LocalDate.of(2023, 9, 1), ACTIVE));
        DiariesEntity diary2 = jpaDiariesRepository.save(diary(user, LocalDate.of(2024, 9, 2), ACTIVE));
        DiariesEntity diary3 = jpaDiariesRepository.save(diary(user, LocalDate.of(2024, 9, 3), ACTIVE));
        DiariesEntity diary4 = jpaDiariesRepository.save(diary(user, LocalDate.of(2024, 9, 4), ACTIVE));
        DiariesEntity diary5 = jpaDiariesRepository.save(diary(user, LocalDate.of(2024, 9, 5), INACTIVE));

        jpaDailyAnalysisSentencesRepository.save(DailyAnalysisSentencesEntity.builder().user(user).diary(diary).type(DiarySentenceType.LITERARY_SUMMARY).content("맑고 따뜻한 하루의 산책").build());
        jpaDailyAnalysisSentencesRepository.save(DailyAnalysisSentencesEntity.builder().user(user).diary(diary2).type(DiarySentenceType.LITERARY_SUMMARY).content("맑고 따뜻한 하루의 산책").build());
        jpaDailyAnalysisSentencesRepository.save(DailyAnalysisSentencesEntity.builder().user(user).diary(diary3).type(DiarySentenceType.LITERARY_SUMMARY).content("맑고 따뜻한 하루의 산책").build());
        jpaDailyAnalysisSentencesRepository.save(DailyAnalysisSentencesEntity.builder().user(user).diary(diary4).type(DiarySentenceType.LITERARY_SUMMARY).content("맑고 따뜻한 하루의 산책").build());
        jpaDailyAnalysisSentencesRepository.save(DailyAnalysisSentencesEntity.builder().user(user).diary(diary5).type(DiarySentenceType.LITERARY_SUMMARY).content("맑고 따뜻한 하루의 산책").build());

        // when
        List<DailyAnalysisSentencesEntity> sentences = diaryDatabaseReadService.findSentencesOfTypeByUserInMonth(
                DiarySentencesOfTypeInMonthRequest.builder()
                        .usersEntity(user)
                        .diaryStatus(ACTIVE)
                        .year(2024)
                        .month(9)
                        .diarySentenceType(DiarySentenceType.LITERARY_SUMMARY)
                        .build()
        );

        // then
        assertThat(sentences).isNotEmpty();
        assertThat(sentences.size()).isEqualTo(3);
        assertThat(sentences.get(0).getUser()).isEqualTo(user);
        assertThat(sentences.get(0).getDiary().getStatus()).isEqualTo(ACTIVE);
        assertThat(sentences.get(0).getDiary().getEntryDate().getYear()).isEqualTo(2024);
        assertThat(sentences.get(0).getDiary().getEntryDate().getMonthValue()).isEqualTo(9);
        assertThat(sentences.get(0).getContent()).isEqualTo("맑고 따뜻한 하루의 산책");
    }

    @Test
    public void 특정_타입의_다이어리_문장_목록_페이지에_따라_조회() {

        // given
        UsersEntity user = user();
        DiariesEntity diary = jpaDiariesRepository.save(diary(user, LocalDate.of(2024, 9, 1), ACTIVE));
        DiariesEntity diary2 = jpaDiariesRepository.save(diary(user, LocalDate.of(2024, 9, 2), ACTIVE));
        DiariesEntity diary3 = jpaDiariesRepository.save(diary(user, LocalDate.of(2024, 9, 3), ACTIVE));
        DiariesEntity diary4 = jpaDiariesRepository.save(diary(user, LocalDate.of(2024, 9, 4), ACTIVE));

        jpaDailyAnalysisSentencesRepository.save(DailyAnalysisSentencesEntity.builder().user(user).diary(diary).type(DiarySentenceType.LITERARY_SUMMARY).content("맑고 따뜻한 하루의 산책").build());
        jpaDailyAnalysisSentencesRepository.save(DailyAnalysisSentencesEntity.builder().user(user).diary(diary2).type(DiarySentenceType.LITERARY_SUMMARY).content("맑고 따뜻한 하루의 산책").build());
        jpaDailyAnalysisSentencesRepository.save(DailyAnalysisSentencesEntity.builder().user(user).diary(diary3).type(DiarySentenceType.LITERARY_SUMMARY).content("맑고 따뜻한 하루의 산책").build());
        jpaDailyAnalysisSentencesRepository.save(DailyAnalysisSentencesEntity.builder().user(user).diary(diary4).type(DiarySentenceType.LITERARY_SUMMARY).content("맑고 따뜻한 하루의 산책").build());

        // when
        PagingRequest pagingRequest = PagingRequest.of(0, 7, PagingRequest.Sort.by("DiariesEntity.id"));
        List<String> sentences = diaryDatabaseReadService.findSentencesOfTypeByUserWithinPage(
                DiarySentencesOfTypeWithinPageRequest.builder()
                        .usersEntity(user)
                        .diaryStatus(DiaryStatus.ACTIVE)
                        .diarySentenceType(DiarySentenceType.LITERARY_SUMMARY)
                        .pagingRequest(pagingRequest)
                        .build()
        );

        // then
        assertThat(sentences).isNotEmpty();
        assertThat(sentences.size()).isEqualTo(4);
        assertThat(sentences.get(0)).isEqualTo("맑고 따뜻한 하루의 산책");

    }

    @Test
    public void 기간내_특정_타입의_일일_다이어리_단어_평균_긍부정_수치_조회() {

        // given
        UsersEntity user = user();
        DiariesEntity diary = jpaDiariesRepository.save(diary(user, LocalDate.of(2024, 9, 1), ACTIVE));
        DiariesEntity diary2 = jpaDiariesRepository.save(diary(user, LocalDate.of(2024, 9, 2), ACTIVE));

        jpaDailyAnalysisWordsRepository.save(DailyAnalysisWordsEntity.builder().user(user).diary(diary).type(EMOTION).text("친밀감").scale(7).build());
        jpaDailyAnalysisWordsRepository.save(DailyAnalysisWordsEntity.builder().user(user).diary(diary).type(EMOTION).text("편안함").scale(6).build());
        jpaDailyAnalysisWordsRepository.save(DailyAnalysisWordsEntity.builder().user(user).diary(diary).type(EMOTION).text("외로움").scale(1).build());
        jpaDailyAnalysisWordsRepository.save(DailyAnalysisWordsEntity.builder().user(user).diary(diary).type(EMOTION).text("피곤함").scale(2).build());

        jpaDailyAnalysisWordsRepository.save(DailyAnalysisWordsEntity.builder().user(user).diary(diary2).type(EMOTION).text("친밀감").scale(7).build());
        jpaDailyAnalysisWordsRepository.save(DailyAnalysisWordsEntity.builder().user(user).diary(diary2).type(EMOTION).text("편안함").scale(6).build());
        jpaDailyAnalysisWordsRepository.save(DailyAnalysisWordsEntity.builder().user(user).diary(diary2).type(EMOTION).text("외로움").scale(1).build());
        jpaDailyAnalysisWordsRepository.save(DailyAnalysisWordsEntity.builder().user(user).diary(diary2).type(EMOTION).text("피곤함").scale(2).build());
        jpaDailyAnalysisWordsRepository.save(DailyAnalysisWordsEntity.builder().user(user).diary(diary2).type(EMOTION).text("지루함").scale(2).build());

        // when
        PagingRequest pagingRequest = PagingRequest.of(0, 7, PagingRequest.Sort.by("DiariesEntity.id"));
        List<BigDecimal> averages = diaryDatabaseReadService.findAverageWordsScaleOfTypeByUserWithinPage(
                AverageWordsScaleOfTypeByUserWithinPageRequest.builder()
                        .usersEntity(user)
                        .diaryStatus(DiaryStatus.ACTIVE)
                        .diaryWordType(DiaryWordType.EMOTION)
                        .pagingRequest(pagingRequest)
                        .build()
        );

        // then
        assertThat(averages).isNotEmpty();
        assertThat(averages.get(0)).isEqualTo(BigDecimal.valueOf(4.0));
        assertThat(averages.get(1)).isEqualTo(BigDecimal.valueOf(3.6));

    }

    @Test
    public void 기간내_다이어리_단어들_빈도수_높은_순으로_조회() {

        // given
        UsersEntity user = user();
        DiariesEntity diary = jpaDiariesRepository.save(diary(user, LocalDate.of(2024, 9, 1), ACTIVE));
        DiariesEntity diary2 = jpaDiariesRepository.save(diary(user, LocalDate.of(2024, 9, 2), ACTIVE));

        jpaDailyAnalysisWordsRepository.save(DailyAnalysisWordsEntity.builder().user(user).diary(diary).type(SELF_THOUGHT).text("긍정적인 사람").scale(8).build());
        jpaDailyAnalysisWordsRepository.save(DailyAnalysisWordsEntity.builder().user(user).diary(diary).type(SELF_THOUGHT).text("객관적인 사람").scale(8).build());
        jpaDailyAnalysisWordsRepository.save(DailyAnalysisWordsEntity.builder().user(user).diary(diary).type(CORE_VALUE).text("사랑").scale(7).build());
        jpaDailyAnalysisWordsRepository.save(DailyAnalysisWordsEntity.builder().user(user).diary(diary).type(CORE_VALUE).text("가족").scale(8).build());

        jpaDailyAnalysisWordsRepository.save(DailyAnalysisWordsEntity.builder().user(user).diary(diary2).type(SELF_THOUGHT).text("긍정적인 사람").scale(8).build());
        jpaDailyAnalysisWordsRepository.save(DailyAnalysisWordsEntity.builder().user(user).diary(diary2).type(CORE_VALUE).text("가족").scale(8).build());

        // when
        LocalDate today = LocalDate.of(2024, 9, 3);
        List<String> keywords = diaryDatabaseReadService.findTopNRepetitiveWordsOfTypesBetweenDates(
                TopNRepetitiveDiaryWordsOfTypesBetweenDatesRequest.builder()
                        .usersEntity(user)
                        .diaryStatus(DiaryStatus.ACTIVE)
                        .diaryWordTypes(List.of(CORE_VALUE, SELF_THOUGHT))
                        .startDate(today.minusDays(30))
                        .endDate(today)
                        .limit(10)
                        .build()
        );

        // then
        assertThat(keywords).isNotEmpty();
        assertThat(keywords).contains("긍정적인 사람", "객관적인 사람", "사랑", "가족");
        assertThat(keywords.get(0)).isEqualTo("긍정적인 사람");
        assertThat(keywords.get(keywords.size() - 1)).isEqualTo("사랑");

    }

    @Test
    public void findRecentRecommendedActionsTest() {

        // given
        UsersEntity user = user();
        DiariesEntity diary = jpaDiariesRepository.save(diary(user, LocalDate.of(2024, 9, 1), ACTIVE));

        jpaDailyAnalysisSentencesRepository.save(DailyAnalysisSentencesEntity.builder().user(user).diary(diary).type(DiarySentenceType.RECOMMENDED_ACTION).content("맑은 날씨를 즐기기").build());
        jpaDailyAnalysisSentencesRepository.save(DailyAnalysisSentencesEntity.builder().user(user).diary(diary).type(DiarySentenceType.RECOMMENDED_ACTION).content("친구에게 연락하기").build());
        jpaDailyAnalysisSentencesRepository.save(DailyAnalysisSentencesEntity.builder().user(user).diary(diary).type(DiarySentenceType.RECOMMENDED_ACTION).content("가족에게 연락하기").build());
        jpaDailyAnalysisSentencesRepository.save(DailyAnalysisSentencesEntity.builder().user(user).diary(diary).type(DiarySentenceType.RECOMMENDED_ACTION).content("수영하러 가기").build());

        // when
        PagingRequest pagingRequest = PagingRequest.of(0, 1, PagingRequest.Sort.by(PagingRequest.Order.desc("DiariesEntity.id")));
        List<String> recommendedActions = diaryDatabaseReadService.findSentencesOfTypeByUserWithinPage(
                DiarySentencesOfTypeWithinPageRequest.builder()
                        .usersEntity(user)
                        .diaryStatus(DiaryStatus.ACTIVE)
                        .diarySentenceType(DiarySentenceType.RECOMMENDED_ACTION)
                        .pagingRequest(pagingRequest)
                        .build()
        );

        // then
        assertThat(recommendedActions).contains("맑은 날씨를 즐기기", "친구에게 연락하기", "가족에게 연락하기", "수영하러 가기");

    }
}