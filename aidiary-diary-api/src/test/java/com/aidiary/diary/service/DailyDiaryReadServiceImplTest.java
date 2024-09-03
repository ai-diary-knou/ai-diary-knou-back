package com.aidiary.diary.service;

import com.aidiary.common.enums.*;
import com.aidiary.common.exception.DiaryException;
import com.aidiary.common.vo.ResponseBundle.UserPrincipal;
import com.aidiary.core.entity.DailyAnalysisSentencesEntity;
import com.aidiary.core.entity.DailyAnalysisWordsEntity;
import com.aidiary.core.entity.DiariesEntity;
import com.aidiary.core.entity.UsersEntity;
import com.aidiary.core.service.DiaryDatabaseReadService;
import com.aidiary.diary.model.DiaryResponseBundle.*;
import com.aidiary.diary.service.processor.DailyDiaryContentProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.aidiary.common.enums.DiaryStatus.ACTIVE;
import static com.aidiary.common.enums.DiaryStatus.INACTIVE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class DailyDiaryReadServiceImplTest {

    @Mock
    private DiaryDatabaseReadService diaryDatabaseReadService;

    @Mock
    private DailyDiaryContentProcessor dailyDiaryContentProcessor;

    @InjectMocks
    private DailyDiaryReadServiceImpl dailyDiaryReadService;

    @Test
    public void 다이어리_상세_조회_성공() throws Exception {

        // given
        UserPrincipal userPrincipal = UserPrincipal.builder().userId(1L).build();
        UsersEntity user = UsersEntity.builder().id(1L).email("test@gmail.com").build();
        String content = "오늘은 새로운 도전을 결심한 날이었다. 아침에는 약간의 두려움이 있었지만, 긍정적인 마음으로 출발했다. 친구들과 대화를 나누면서 더 많은 영감을 얻을 수 있었다.";
        DiariesEntity diary = DiariesEntity.builder().id(1L).user(user).content(content).build();
        List<DailyAnalysisWordsEntity> words = List.of(
                word(1L, user, diary, DiaryWordType.EMOTION, "행복", 8),
                word(2L, user, diary, DiaryWordType.EMOTION, "긴장", 10),
                word(3L, user, diary, DiaryWordType.SELF_THOUGHT, "결심", 7),
                word(4L, user, diary, DiaryWordType.SELF_THOUGHT, "의지", 6),
                word(5L, user, diary, DiaryWordType.CORE_VALUE, "진실성", 9),
                word(6L, user, diary, DiaryWordType.CORE_VALUE, "성실성", 8)
        );
        List<DailyAnalysisSentencesEntity> sentences = List.of(
                sentence(1L, user, diary, DiarySentenceType.EMOTION, "오늘 하루는 행복하고 긴장되는 순간들이 많았다."),
                sentence(2L, user, diary, DiarySentenceType.SELF_THOUGHT, "자신감을 가지려고 노력했다."),
                sentence(3L, user, diary, DiarySentenceType.CORE_VALUE, "진실됨과 성실함을 지키는 것이 중요하다."),
                sentence(4L, user, diary, DiarySentenceType.RECOMMENDED_ACTION, "매일 아침 일기를 쓰기"),
                sentence(5L, user, diary, DiarySentenceType.RECOMMENDED_ACTION, "감정을 기록하는 시간을 갖기"),
                sentence(6L, user, diary, DiarySentenceType.ADDITIONAL, "오늘은 새로운 책을 읽기 시작했다."),
                sentence(7L, user, diary, DiarySentenceType.ADDITIONAL, "산책을 하면서 생각을 정리했다."),
                sentence(8L, user, diary, DiarySentenceType.LITERARY_SUMMARY, "오늘 하루는 새로운 도전을 시작한 의미 있는 날이었다.")
        );
        DiaryDetail diaryDetail = DiaryDetail.builder()
                .entryDate(LocalDate.of(2023, 9, 4))
                .emotions(DiaryEmotions.builder()
                        .content("오늘 하루는 행복하고 긴장되는 순간들이 많았다.")
                        .words(List.of(DiaryWord.builder().text("행복").scale(8).build(), DiaryWord.builder().text("긴장").scale(10).build()))
                        .build())
                .selfThoughts(DiarySelfThoughts.builder()
                        .content("자신감을 가지려고 노력했다.")
                        .words(List.of(DiaryWord.builder().text("결심").scale(7).build(), DiaryWord.builder().text("의지").scale(6).build()))
                        .build())
                .coreValues(DiaryCoreValues.builder()
                        .content("진실됨과 성실함을 지키는 것이 중요하다.")
                        .words(List.of(DiaryWord.builder().text("진실성").scale(9).build(), DiaryWord.builder().text("성실성").scale(8).build()))
                        .build())
                .recommendedActions(List.of("매일 아침 일기를 쓰기", "감정을 기록하는 시간을 갖기"))
                .additionals(List.of("오늘은 새로운 책을 읽기 시작했다.", "산책을 하면서 생각을 정리했다."))
                .literarySummary("오늘 하루는 새로운 도전을 시작한 의미 있는 날이었다.")
                .diaryContent(content)
                .build();

        when(diaryDatabaseReadService.findDiaryById(1L)).thenReturn(Optional.of(diary));
        when(diaryDatabaseReadService.findWordsFromDiary(diary)).thenReturn(words);
        when(diaryDatabaseReadService.findSentencesFromDiary(diary)).thenReturn(sentences);
        when(dailyDiaryContentProcessor.process(diary, words, sentences)).thenReturn(diaryDetail);

        // when
        DiaryDetail result = dailyDiaryReadService.getDiaryDetail(userPrincipal, 1L);

        // then
        String jsonResult = new ObjectMapper().registerModule(new JavaTimeModule()).writerWithDefaultPrettyPrinter().writeValueAsString(result);
        System.out.println("JSON Result: " + jsonResult);

        assertNotNull(result);
        assertEquals(diaryDetail.diaryContent(), result.diaryContent());
        assertEquals(diaryDetail.emotions().words().size(), 2);
        assertEquals(diaryDetail.selfThoughts().words().size(), 2);
        assertEquals(diaryDetail.coreValues().words().size(), 2);
        assertEquals(diaryDetail.emotions().content(), "오늘 하루는 행복하고 긴장되는 순간들이 많았다.");
        assertEquals(diaryDetail.coreValues().content(), "진실됨과 성실함을 지키는 것이 중요하다.");
        assertEquals(diaryDetail.recommendedActions().size(), 2);
        assertEquals(diaryDetail.additionals().size(), 2);
        assertEquals(diaryDetail.literarySummary(), "오늘 하루는 새로운 도전을 시작한 의미 있는 날이었다.");

        verify(diaryDatabaseReadService, times(1)).findDiaryById(1L);
        verify(dailyDiaryContentProcessor, times(1)).process(diary, words, sentences);

    }

    DailyAnalysisWordsEntity word(Long id, UsersEntity user, DiariesEntity diary, DiaryWordType diaryWordType, String text, int scale){
        return DailyAnalysisWordsEntity.builder()
                .id(id)
                .user(user)
                .diary(diary)
                .type(diaryWordType)
                .text(text)
                .scale(scale)
                .build();
    }

    DailyAnalysisSentencesEntity sentence(Long id, UsersEntity user, DiariesEntity diary, DiarySentenceType diarySentenceType, String content){
        return DailyAnalysisSentencesEntity.builder()
                .id(id)
                .user(user)
                .diary(diary)
                .type(diarySentenceType)
                .content(content)
                .build();
    }

    @Test
    public void 다이어리_상세_조회_실패__해당id의_다이어리_없음() {

        // given
        UserPrincipal userPrincipal = UserPrincipal.builder().userId(1L).build();

        when(diaryDatabaseReadService.findDiaryById(1L)).thenReturn(Optional.empty());

        // when & then
        DiaryException exception = assertThrows(DiaryException.class, () -> dailyDiaryReadService.getDiaryDetail(userPrincipal, 1L));
        assertEquals(ErrorCode.DIARY_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    public void 다이어리_상세_조회_실패__해당id의_다이어리_비활성화됨() {
        // given
        UserPrincipal userPrincipal = UserPrincipal.builder().userId(1L).build();
        UsersEntity user = UsersEntity.builder().id(1L).email("test@gmail.com").build();
        DiariesEntity diary = DiariesEntity.builder().id(1L).user(user).status(INACTIVE).build();

        when(diaryDatabaseReadService.findDiaryById(1L)).thenReturn(Optional.of(diary));

        // when & then
        DiaryException exception = assertThrows(DiaryException.class, () -> dailyDiaryReadService.getDiaryDetail(userPrincipal, 1L));
        assertEquals(ErrorCode.DIARY_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    public void 다이어리_상세_조회_실패__작성자가_아님() {

        // given
        UserPrincipal userPrincipal = UserPrincipal.builder().userId(1L).build();

        UsersEntity anotherUser = UsersEntity.builder().id(2L).email("test@gmail.com").build();
        DiariesEntity anotherUserDiary = DiariesEntity.builder().id(1L).user(anotherUser).build();

        when(diaryDatabaseReadService.findDiaryById(1L)).thenReturn(Optional.of(anotherUserDiary));

        // when & then
        DiaryException exception = assertThrows(DiaryException.class, () -> dailyDiaryReadService.getDiaryDetail(userPrincipal, 1L));
        assertEquals(ErrorCode.DIARY_OWNER_MISMATCH, exception.getErrorCode());

    }

    @Test
    public void testGetUserDiaryCount() {

        // given
        UserPrincipal userPrincipal = UserPrincipal.builder().userId(1L).build();
        UsersEntity user = UsersEntity.builder().id(1L).email("test@gmail.com").build();
        String content = "오늘은 새로운 도전을 결심한 날이었다. 아침에는 약간의 두려움이 있었지만, 긍정적인 마음으로 출발했다. 친구들과 대화를 나누면서 더 많은 영감을 얻을 수 있었다.";

        List<DiariesEntity> diaries = List.of(
                DiariesEntity.builder().id(1L).user(user).content(content).status(ACTIVE).build(),
                DiariesEntity.builder().id(2L).user(user).content(content).status(ACTIVE).build(),
                DiariesEntity.builder().id(3L).user(user).content(content).status(ACTIVE).build(),
                DiariesEntity.builder().id(4L).user(user).content(content).status(ACTIVE).build(),
                DiariesEntity.builder().id(5L).user(user).content(content).status(ACTIVE).build()
        );

        when(diaryDatabaseReadService.countAllActiveDiariesByUser(any(UsersEntity.class))).thenReturn(Long.valueOf(diaries.size()));

        // when
        Long count = dailyDiaryReadService.getUserDiaryCount(userPrincipal);

        // then
        assertEquals(Long.valueOf(diaries.size()), count);
        verify(diaryDatabaseReadService, times(1)).countAllActiveDiariesByUser(any(UsersEntity.class));

    }

}