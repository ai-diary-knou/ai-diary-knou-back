package com.aidiary.user.service;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.enums.UserStatus;
import com.aidiary.common.exception.UserException;
import com.aidiary.common.vo.ResponseBundle.UserPrincipal;
import com.aidiary.core.entity.UserEmailAuthsEntity;
import com.aidiary.core.entity.UsersEntity;
import com.aidiary.core.service.UserDatabaseReadService;
import com.aidiary.core.service.UserDatabaseWriteService;
import com.aidiary.user.model.UserRequestBundle.DuplicateUserValidateRequest;
import com.aidiary.user.model.UserRequestBundle.UserNicknameUpdateRequest;
import com.aidiary.user.model.UserRequestBundle.UserPasswordUpdateRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserInfoServiceTest {

    @Mock
    private UserDatabaseReadService userDatabaseReadService;

    @Mock
    private UserDatabaseWriteService userDatabaseWriteService;

    @InjectMocks
    private UserInfoService userInfoService;

    @Test
    void 회원_이메일_중복_확인__중복없음() {

        // given
        DuplicateUserValidateRequest request = new DuplicateUserValidateRequest("email", "test@test.com");
        when(userDatabaseReadService.isUserExistsByEmail(request.value())).thenReturn(false);

        // when & then
        userInfoService.validateUserDuplication(request);

        verify(userDatabaseReadService, times(1)).isUserExistsByEmail(request.value());

    }

    @Test
    void 회원_이메일_중복_확인__중복있음() {

        // given
        DuplicateUserValidateRequest request = new DuplicateUserValidateRequest("email", "test@test.com");
        when(userDatabaseReadService.isUserExistsByEmail(request.value())).thenReturn(true);

        // when & then
        UserException exception = assertThrows(UserException.class, () -> userInfoService.validateUserDuplication(request));

        verify(userDatabaseReadService, times(1)).isUserExistsByEmail(request.value());
        assertEquals(ErrorCode.USER_ALREADY_REGISTERED, exception.getErrorCode());

    }

    @Test
    void 회원_닉네임_중복_확인__중복없음() {

        // given
        DuplicateUserValidateRequest request = new DuplicateUserValidateRequest("nickname", "testNick");
        when(userDatabaseReadService.isUserExistsByNickname(request.value())).thenReturn(false);

        // when & then
        UserException exception = assertThrows(UserException.class, () -> userInfoService.validateUserDuplication(request));

        verify(userDatabaseReadService, times(1)).isUserExistsByNickname(request.value());
        assertEquals(ErrorCode.USER_ALREADY_REGISTERED, exception.getErrorCode());

    }

    @Test
    void 회원_닉네임_중복_확인__중복있음() {

        // given
        DuplicateUserValidateRequest request = new DuplicateUserValidateRequest("nickname", "testNick");
        when(userDatabaseReadService.isUserExistsByNickname(request.value())).thenReturn(true);

        // when & then
        assertThrows(UserException.class, () -> userInfoService.validateUserDuplication(request));

        verify(userDatabaseReadService, times(1)).isUserExistsByNickname(request.value());

    }

    @Test
    void 비밀번호_변경_성공() {

        // given
        UserPasswordUpdateRequest request = new UserPasswordUpdateRequest("test@test.com", "CODE123!@#", "newPassword", "newPassword");

        UsersEntity user = UsersEntity.builder()
                .id(1L)
                .email("test@test.com")
                .status(UserStatus.ACTIVE)
                .password("originalPasswordHash")
                .loginAttemptCnt(4) // 5 attempts then locked
                .build();

        UserEmailAuthsEntity userEmailAuth = UserEmailAuthsEntity.builder()
                .email("test@test.com")
                .code("CODE123!@#")
                .confirmedAt(LocalDateTime.of(2024, 1, 2, 0, 0, 0))
                .build();

        when(userDatabaseReadService.findUserByEmail(request.email())).thenReturn(Optional.of(user));
        when(userDatabaseReadService.findUserEmailAuthByEmail(request.email())).thenReturn(Optional.of(userEmailAuth));
        when(userDatabaseReadService.isUserEmailAuthConfirmedByEmail(request.email())).thenReturn(true);
        Mockito.lenient().doNothing().when(userDatabaseWriteService).updateUserPassword(any(UsersEntity.class), anyString());
        Mockito.lenient().doNothing().when(userDatabaseWriteService).resetUserLoginAttemptCnt(any(UsersEntity.class));

        // when
        userInfoService.updatePassword(request);

        // then
        verify(userDatabaseReadService, times(1)).findUserByEmail(eq(request.email()));
        verify(userDatabaseReadService, times(1)).findUserEmailAuthByEmail(eq(request.email()));
        verify(userDatabaseReadService, times(1)).isUserEmailAuthConfirmedByEmail(eq(request.email()));
        verify(userDatabaseWriteService, times(1)).updateUserPassword(any(UsersEntity.class), anyString());
        verify(userDatabaseWriteService, times(1)).resetUserLoginAttemptCnt(any(UsersEntity.class));

    }

    @Test
    void 비밀번호_변경_실패__해당email의_회원없음() {

        // given
        UserPasswordUpdateRequest request = new UserPasswordUpdateRequest("test@test.com", "CODE123!@#", "newPassword", "newPassword");

        when(userDatabaseReadService.findUserByEmail(request.email())).thenReturn(Optional.empty());

        // when & then
        UserException exception = assertThrows(UserException.class, () -> userInfoService.updatePassword(request));

        verify(userDatabaseReadService, times(1)).findUserByEmail(request.email());
        assertEquals(ErrorCode.USER_NOT_EXIST, exception.getErrorCode());

    }

    @Test
    void 비밀번호_변경_실패__해당email의_인증_코드_이력_없음() {

        // given
        UserPasswordUpdateRequest request = new UserPasswordUpdateRequest("test@test.com", "CODE123!@#", "newPassword", "newPassword");

        UsersEntity user = UsersEntity.builder()
                .id(1L)
                .email("test@test.com")
                .status(UserStatus.ACTIVE)
                .password("originalPasswordHash")
                .loginAttemptCnt(4) // 5 attempts then locked
                .build();

        when(userDatabaseReadService.findUserByEmail(request.email())).thenReturn(Optional.of(user));
        when(userDatabaseReadService.findUserEmailAuthByEmail(request.email())).thenReturn(Optional.empty());

        // when & then
        UserException exception = assertThrows(UserException.class, () -> userInfoService.updatePassword(request));

        verify(userDatabaseReadService, times(1)).findUserByEmail(request.email());
        verify(userDatabaseReadService, times(1)).findUserEmailAuthByEmail(request.email());
        assertEquals(ErrorCode.EMAIL_NOT_AUTHORIZED, exception.getErrorCode());

    }

    @Test
    void 비밀번호_변경_실패__회원_탈퇴_상태() {

        // given
        UserPasswordUpdateRequest request = new UserPasswordUpdateRequest("test@test.com", "CODE123!@#", "newPassword", "newPassword");

        UsersEntity user = UsersEntity.builder()
                .id(1L)
                .email("test@test.com")
                .status(UserStatus.INACTIVE)
                .password("originalPasswordHash")
                .loginAttemptCnt(4) // 5 attempts then locked
                .build();

        UserEmailAuthsEntity userEmailAuth = UserEmailAuthsEntity.builder()
                .email("test@test.com")
                .code("CODE123!@#")
                .confirmedAt(LocalDateTime.of(2024, 1, 2, 0, 0, 0))
                .build();

        when(userDatabaseReadService.findUserByEmail(request.email())).thenReturn(Optional.of(user));
        when(userDatabaseReadService.findUserEmailAuthByEmail(request.email())).thenReturn(Optional.of(userEmailAuth));

        // when & then
        UserException exception = assertThrows(UserException.class, () -> userInfoService.updatePassword(request));

        verify(userDatabaseReadService, times(1)).findUserByEmail(request.email());
        verify(userDatabaseReadService, times(1)).findUserEmailAuthByEmail(request.email());
        assertEquals(ErrorCode.USER_ALREADY_SIGNED_OUT, exception.getErrorCode());

    }

    @Test
    void 비밀번호_변경_실패__회원_잠금_상태() {

        // given
        UserPasswordUpdateRequest request = new UserPasswordUpdateRequest("test@test.com", "CODE123!@#", "newPassword", "newPassword");

        UsersEntity user = UsersEntity.builder()
                .id(1L)
                .email("test@test.com")
                .status(UserStatus.BLOCKED)
                .password("originalPasswordHash")
                .loginAttemptCnt(4) // 5 attempts then locked
                .build();

        UserEmailAuthsEntity userEmailAuth = UserEmailAuthsEntity.builder()
                .email("test@test.com")
                .code("CODE123!@#")
                .confirmedAt(LocalDateTime.of(2024, 1, 2, 0, 0, 0))
                .build();

        when(userDatabaseReadService.findUserByEmail(request.email())).thenReturn(Optional.of(user));
        when(userDatabaseReadService.findUserEmailAuthByEmail(request.email())).thenReturn(Optional.of(userEmailAuth));

        // when & then
        UserException exception = assertThrows(UserException.class, () -> userInfoService.updatePassword(request));

        verify(userDatabaseReadService, times(1)).findUserByEmail(request.email());
        verify(userDatabaseReadService, times(1)).findUserEmailAuthByEmail(request.email());
        assertEquals(ErrorCode.USER_LOGIN_LOCKED, exception.getErrorCode());

    }

    @Test
    void 비밀번호_변경_실패__인증코드_컨펌_안됨() {

        // given
        UserPasswordUpdateRequest request = new UserPasswordUpdateRequest("test@test.com", "CODE123!@#", "newPassword", "newPassword");

        UsersEntity user = UsersEntity.builder()
                .id(1L)
                .email("test@test.com")
                .status(UserStatus.ACTIVE)
                .password("originalPasswordHash")
                .loginAttemptCnt(4) // 5 attempts then locked
                .build();

        UserEmailAuthsEntity userEmailAuth = UserEmailAuthsEntity.builder()
                .email("test@test.com")
                .code("CODE123!@#")
                .confirmedAt(null)
                .build();

        when(userDatabaseReadService.findUserByEmail(request.email())).thenReturn(Optional.of(user));
        when(userDatabaseReadService.findUserEmailAuthByEmail(request.email())).thenReturn(Optional.of(userEmailAuth));
        when(userDatabaseReadService.isUserEmailAuthConfirmedByEmail(request.email())).thenReturn(false);

        // when & then
        UserException exception = assertThrows(UserException.class, () -> userInfoService.updatePassword(request));

        verify(userDatabaseReadService, times(1)).findUserByEmail(request.email());
        verify(userDatabaseReadService, times(1)).findUserEmailAuthByEmail(request.email());
        verify(userDatabaseReadService, times(1)).isUserEmailAuthConfirmedByEmail(request.email());
        assertEquals(ErrorCode.EMAIL_NOT_AUTHORIZED, exception.getErrorCode());

    }

    @Test
    void 닉네임_변경_성공() {

        // given
        UserPrincipal userPrincipal = new UserPrincipal(1L, "test@test.com", "nickname");
        UserNicknameUpdateRequest request = new UserNicknameUpdateRequest("newNickname");

        UsersEntity user = UsersEntity.builder()
                .id(1L)
                .email("test@test.com")
                .status(UserStatus.ACTIVE)
                .password("originalPasswordHash")
                .loginAttemptCnt(4) // 5 attempts then locked
                .build();

        when(userDatabaseReadService.findUserById(1L)).thenReturn(Optional.of(user));
        when(userDatabaseReadService.isUserExistsByNickname(request.nickname())).thenReturn(false);
        Mockito.lenient().doNothing().when(userDatabaseWriteService).updateUserNickname(any(UsersEntity.class), anyString());

        // when
        userInfoService.updateNickname(userPrincipal.userId(), request);

        // then
        verify(userDatabaseReadService, times(1)).findUserById(userPrincipal.userId());
        verify(userDatabaseReadService, times(1)).isUserExistsByNickname(request.nickname());
        verify(userDatabaseWriteService, times(1)).updateUserNickname(any(UsersEntity.class), anyString());

    }

    @Test
    void 닉네임_변경_실패__해당id의_회원_없음() {

        // given
        UserPrincipal userPrincipal = new UserPrincipal(1L, "test@test.com", "nickname");
        UserNicknameUpdateRequest request = new UserNicknameUpdateRequest("newNickname");

        when(userDatabaseReadService.findUserById(1L)).thenReturn(Optional.empty());

        // when & then
        UserException exception = assertThrows(UserException.class, () -> userInfoService.updateNickname(userPrincipal.userId(), request));

        verify(userDatabaseReadService, times(1)).findUserById(userPrincipal.userId());
        assertEquals(ErrorCode.USER_NOT_EXIST, exception.getErrorCode());

    }

    @Test
    void 닉네임_변경_실패__회원_잠금_상태() {

        // given
        UserPrincipal userPrincipal = new UserPrincipal(1L, "test@test.com", "nickname");
        UserNicknameUpdateRequest request = new UserNicknameUpdateRequest("newNickname");

        UsersEntity user = UsersEntity.builder()
                .id(1L)
                .email("test@test.com")
                .status(UserStatus.BLOCKED)
                .password("originalPasswordHash")
                .loginAttemptCnt(4) // 5 attempts then locked
                .build();

        when(userDatabaseReadService.findUserById(1L)).thenReturn(Optional.of(user));

        // when & then
        UserException exception = assertThrows(UserException.class, () -> userInfoService.updateNickname(userPrincipal.userId(), request));

        verify(userDatabaseReadService, times(1)).findUserById(userPrincipal.userId());
        assertEquals(ErrorCode.USER_LOGIN_LOCKED, exception.getErrorCode());

    }

    @Test
    void 닉네임_변경_실패__회원_탈퇴_상태() {

        // given
        UserPrincipal userPrincipal = new UserPrincipal(1L, "test@test.com", "nickname");
        UserNicknameUpdateRequest request = new UserNicknameUpdateRequest("newNickname");

        UsersEntity user = UsersEntity.builder()
                .id(1L)
                .email("test@test.com")
                .status(UserStatus.INACTIVE)
                .password("originalPasswordHash")
                .loginAttemptCnt(4) // 5 attempts then locked
                .build();

        when(userDatabaseReadService.findUserById(1L)).thenReturn(Optional.of(user));

        // when & then
        UserException exception = assertThrows(UserException.class, () -> userInfoService.updateNickname(userPrincipal.userId(), request));

        verify(userDatabaseReadService, times(1)).findUserById(userPrincipal.userId());
        assertEquals(ErrorCode.USER_ALREADY_SIGNED_OUT, exception.getErrorCode());

    }

    @Test
    void 닉네임_변경_실패__신규_닉네임_이미_있음() {

        // given
        UserPrincipal userPrincipal = new UserPrincipal(1L, "test@test.com", "nickname");
        UserNicknameUpdateRequest request = new UserNicknameUpdateRequest("newNickname");

        UsersEntity user = UsersEntity.builder()
                .id(1L)
                .email("test@test.com")
                .status(UserStatus.ACTIVE)
                .password("originalPasswordHash")
                .loginAttemptCnt(4) // 5 attempts then locked
                .build();

        when(userDatabaseReadService.findUserById(1L)).thenReturn(Optional.of(user));
        when(userDatabaseReadService.isUserExistsByNickname(request.nickname())).thenReturn(true);

        // when
        UserException exception = assertThrows(UserException.class, () -> userInfoService.updateNickname(userPrincipal.userId(), request));

        // then
        verify(userDatabaseReadService, times(1)).findUserById(userPrincipal.userId());
        verify(userDatabaseReadService, times(1)).isUserExistsByNickname(request.nickname());
        assertEquals(ErrorCode.USER_ALREADY_REGISTERED, exception.getErrorCode());

    }

}
