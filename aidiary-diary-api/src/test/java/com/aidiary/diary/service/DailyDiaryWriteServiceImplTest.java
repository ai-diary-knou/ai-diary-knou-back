package com.aidiary.diary.service;

import com.aidiary.common.enums.DiaryStatus;
import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.DiaryException;
import com.aidiary.common.utils.HybridEncryptor;
import com.aidiary.common.vo.ResponseBundle.UserPrincipal;
import com.aidiary.core.entity.DiariesEntity;
import com.aidiary.core.entity.UsersEntity;
import com.aidiary.core.service.DiaryDatabaseReadService;
import com.aidiary.core.service.DiaryDatabaseWriteService;
import com.aidiary.diary.model.DiaryRequestBundle.DiaryCreateRequest;
import com.aidiary.diary.model.DiaryRequestBundle.DiaryUpdateRequest;
import com.aidiary.diary.model.DiaryResponseBundle.DiarySaveRes;
import com.aidiary.diary.event.DailyDiaryCreateEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DailyDiaryWriteServiceImplTest {

    @Mock
    private DiaryDatabaseReadService diaryDatabaseReadService;

    @Mock
    private DiaryDatabaseWriteService diaryDatabaseWriteService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private HybridEncryptor hybridEncryptor;

    @InjectMocks
    private DailyDiaryWriteServiceImpl dailyDiaryWriteService;

    private UsersEntity user;
    private DiaryCreateRequest createRequest;
    private DiaryUpdateRequest updateRequest;
    private DiariesEntity diary;

    @BeforeEach
    public void setUp() {
        user = UsersEntity.builder().id(1L).build();
        createRequest = new DiaryCreateRequest(LocalDate.now(), "오늘은 기분이 좋았다.");
        updateRequest = new DiaryUpdateRequest("업데이트된 일기 내용");
        diary = DiariesEntity.builder().id(1L).user(user).entryDate(LocalDate.now()).content("기존 내용").status(DiaryStatus.ACTIVE).build();
    }

    @Test
    public void 다이어리_저장_성공() throws Exception {
        // given
        UserPrincipal userPrincipal = UserPrincipal.builder().userId(1L).build();

        when(diaryDatabaseReadService.findActiveDiaryByUserAndEntryDate(any(UsersEntity.class), any(LocalDate.class), any(DiaryStatus.class)))
                .thenReturn(Optional.empty());
        when(hybridEncryptor.encrypt(createRequest.content())).thenReturn("암호화된 내용");
        when(diaryDatabaseWriteService.saveDailyDiary(any(DiariesEntity.class))).thenReturn(diary);

        // when
        DiarySaveRes result = dailyDiaryWriteService.saveDiaryAndAnalyzeByAi(userPrincipal, createRequest);

        // then
        assertNotNull(result);
        assertEquals(diary.getId(), result.diaryId());
        verify(diaryDatabaseWriteService, times(1)).saveDailyDiary(any(DiariesEntity.class));
        verify(applicationEventPublisher, times(1)).publishEvent(any(DailyDiaryCreateEvent.class));
    }

    @Test
    public void 다이어리_저장_실패_해당날짜_이미_저장함() {
        // given
        UserPrincipal userPrincipal = UserPrincipal.builder().userId(1L).build();

        when(diaryDatabaseReadService.findActiveDiaryByUserAndEntryDate(any(UsersEntity.class), any(LocalDate.class), any(DiaryStatus.class)))
                .thenReturn(Optional.of(diary));

        // when & then
        DiaryException exception = assertThrows(DiaryException.class, () -> dailyDiaryWriteService.saveDiaryAndAnalyzeByAi(userPrincipal, createRequest));
        assertEquals(ErrorCode.DIARY_ALREADY_EXIST, exception.getErrorCode());
    }

    @Test
    public void 다이어리_업데이트_성공() throws Exception {
        // given
        UserPrincipal userPrincipal = UserPrincipal.builder().userId(1L).build();

        when(diaryDatabaseReadService.findDiaryById(diary.getId())).thenReturn(Optional.of(diary));
        when(hybridEncryptor.encrypt(updateRequest.content())).thenReturn("암호화된 업데이트 내용");
        when(diaryDatabaseWriteService.updateDailyDiary(any(DiariesEntity.class), any(DiariesEntity.class))).thenReturn(diary);

        // when
        DiarySaveRes result = dailyDiaryWriteService.updateDiaryAfterOpenAiAnalysis(userPrincipal, diary.getId(), updateRequest);

        // then
        assertNotNull(result);
        assertEquals(diary.getId(), result.diaryId());
        verify(diaryDatabaseWriteService, times(1)).updateDailyDiary(any(DiariesEntity.class), any(DiariesEntity.class));
        verify(applicationEventPublisher, times(1)).publishEvent(any(DailyDiaryCreateEvent.class));
    }

    @Test
    public void 다이어리_업데이트_실패_해당id의_다이어리_없음() {
        // given
        UserPrincipal userPrincipal = UserPrincipal.builder().userId(1L).build();

        when(diaryDatabaseReadService.findDiaryById(diary.getId())).thenReturn(Optional.empty());

        // when & then
        DiaryException exception = assertThrows(DiaryException.class, () -> dailyDiaryWriteService.updateDiaryAfterOpenAiAnalysis(userPrincipal, diary.getId(), updateRequest));
        assertEquals(ErrorCode.DIARY_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    public void 다이어리_업데이트_실패_해당id의_다이어리_작성자_아님() {
        // given
        UserPrincipal userPrincipal = UserPrincipal.builder().userId(1L).build();
        DiariesEntity anotherUserDiary = DiariesEntity.builder().id(1L).user(UsersEntity.builder().id(2L).build()).build();

        when(diaryDatabaseReadService.findDiaryById(diary.getId())).thenReturn(Optional.of(anotherUserDiary));

        // when & then
        DiaryException exception = assertThrows(DiaryException.class, () -> dailyDiaryWriteService.updateDiaryAfterOpenAiAnalysis(userPrincipal, diary.getId(), updateRequest));
        assertEquals(ErrorCode.DIARY_NOT_FOUND, exception.getErrorCode());
    }

}