package com.aidiary.user.service;

import com.aidiary.auth.service.JwtTokenProvider;
import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.enums.UserStatus;
import com.aidiary.common.exception.UserException;
import com.aidiary.common.utils.SHA256Util;
import com.aidiary.core.entity.UsersEntity;
import com.aidiary.core.service.UserDatabaseReadService;
import com.aidiary.core.service.UserDatabaseWriteService;
import com.aidiary.user.service.event.UserLoginEvent;
import com.aidiary.user.model.UserRequestBundle.UserLoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserLoginServiceTest {

    @Mock
    private UserDatabaseReadService userDatabaseReadService;

    @Mock
    private UserDatabaseWriteService userDatabaseWriteService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private UserLoginService userLoginService;

    @Test
    void 로그인_성공() throws Exception {

        // given
        UserLoginRequest request = new UserLoginRequest("test@test.com", "password");

        UsersEntity user = UsersEntity.builder()
                .id(1L)
                .email(request.email())
                .password(SHA256Util.getHashString(request.password()))
                .status(UserStatus.ACTIVE)
                .loginAttemptCnt(3)
                .build();

        String token = "JWT_BEARER_TOKEN";

        when(userDatabaseReadService.findUserByEmail(request.email())).thenReturn(Optional.of(user));
        when(jwtTokenProvider.createToken(user)).thenReturn(token);
        when(httpServletRequest.getHeader("User-Agent")).thenReturn("Mozilla/5.0");
        when(httpServletRequest.getHeader("X-Forwarded-For")).thenReturn("192.123.123.1");

        // when
        String result = userLoginService.login(request, httpServletRequest);

        // then
        assertEquals(token, result);
        verify(userDatabaseReadService, times(1)).findUserByEmail(request.email());
        verify(jwtTokenProvider, times(1)).createToken(user);
        verify(applicationEventPublisher, times(1)).publishEvent(any(UserLoginEvent.class));

    }

    @Test
    void 로그인_실패__해당email의_회원_없음() throws Exception {

        // given
        UserLoginRequest request = new UserLoginRequest("test@test.com", "password");

        when(userDatabaseReadService.findUserByEmail(request.email())).thenReturn(Optional.empty());

        // when
        UserException exception = assertThrows(UserException.class, () -> userLoginService.login(request, httpServletRequest));

        // then
        verify(userDatabaseReadService, times(1)).findUserByEmail(request.email());
        verify(jwtTokenProvider, never()).createToken(any(UsersEntity.class));
        verify(applicationEventPublisher, never()).publishEvent(any(UserLoginEvent.class));
        assertEquals(ErrorCode.USER_NOT_EXIST, exception.getErrorCode());

    }

    @Test
    void 로그인_실패__회원_잠금_상태() throws Exception {

        // given
        UserLoginRequest request = new UserLoginRequest("test@test.com", "password");

        UsersEntity user = UsersEntity.builder()
                .id(1L)
                .email(request.email())
                .password(SHA256Util.getHashString(request.password()))
                .status(UserStatus.BLOCKED)
                .loginAttemptCnt(3)
                .build();

        when(userDatabaseReadService.findUserByEmail(request.email())).thenReturn(Optional.of(user));

        // when
        UserException exception = assertThrows(UserException.class, () -> userLoginService.login(request, httpServletRequest));

        // then
        verify(userDatabaseReadService, times(1)).findUserByEmail(request.email());
        verify(jwtTokenProvider, never()).createToken(any(UsersEntity.class));
        verify(applicationEventPublisher, never()).publishEvent(any(UserLoginEvent.class));
        assertEquals(ErrorCode.USER_LOGIN_LOCKED, exception.getErrorCode());

    }

    @Test
    void 로그인_실패__회원_탈퇴_상태() throws Exception {

        // given
        UserLoginRequest request = new UserLoginRequest("test@test.com", "password");

        UsersEntity user = UsersEntity.builder()
                .id(1L)
                .email(request.email())
                .password(SHA256Util.getHashString(request.password()))
                .status(UserStatus.INACTIVE)
                .loginAttemptCnt(3)
                .build();

        when(userDatabaseReadService.findUserByEmail(request.email())).thenReturn(Optional.of(user));

        // when
        UserException exception = assertThrows(UserException.class, () -> userLoginService.login(request, httpServletRequest));

        // then
        verify(userDatabaseReadService, times(1)).findUserByEmail(request.email());
        verify(jwtTokenProvider, never()).createToken(any(UsersEntity.class));
        verify(applicationEventPublisher, never()).publishEvent(any(UserLoginEvent.class));
        assertEquals(ErrorCode.USER_ALREADY_SIGNED_OUT, exception.getErrorCode());

    }

    @Test
    void 로그인_실패__비밀번호_불일치() throws Exception {

        // given
        UserLoginRequest request = new UserLoginRequest("test@test.com", "password");

        UsersEntity user = UsersEntity.builder()
                .id(1L)
                .email(request.email())
                .password(SHA256Util.getHashString("another-password"))
                .status(UserStatus.ACTIVE)
                .loginAttemptCnt(3)
                .build();

        when(userDatabaseReadService.findUserByEmail(request.email())).thenReturn(Optional.of(user));
        Mockito.lenient().doNothing().when(userDatabaseWriteService).increaseLoginAttemptCntAndLockIfApproachMaxAttempt(any(UsersEntity.class));

        // when
        UserException exception = assertThrows(UserException.class, () -> userLoginService.login(request, httpServletRequest));

        // then
        verify(userDatabaseReadService, times(1)).findUserByEmail(request.email());
        verify(userDatabaseWriteService, times(1)).increaseLoginAttemptCntAndLockIfApproachMaxAttempt(any(UsersEntity.class));
        assertEquals(ErrorCode.USER_LOGIN_FAIL, exception.getErrorCode());

    }

    @Test
    void 로그인_히스토리_이벤트_퍼블리시_실패() throws Exception {

        // given
        UserLoginRequest request = new UserLoginRequest("test@test.com", "password");
        UsersEntity user = UsersEntity.builder().id(1L).email(request.email()).build();
        String token = "JWT_BEARER_TOKEN";

        when(userDatabaseReadService.findUserByEmail(request.email())).thenReturn(Optional.of(user));
        when(jwtTokenProvider.createToken(user)).thenReturn(token);
        when(httpServletRequest.getHeader("User-Agent")).thenReturn("Mozilla/5.0");
        when(httpServletRequest.getHeader("X-Forwarded-For")).thenReturn("192.168.0.1");

        // when
        userLoginService.login(request, httpServletRequest);

        // then
        verify(applicationEventPublisher, times(1)).publishEvent(argThat(event -> {
            UserLoginEvent loginEvent = new UserLoginEvent(user, "192.168.0.1", "Mozilla/5.0");
            return loginEvent.getUser().equals(user) &&
                    "192.168.0.1".equals(loginEvent.getIpAddress()) &&
                    "Mozilla/5.0".equals(loginEvent.getDevice());
        }));

    }
}
