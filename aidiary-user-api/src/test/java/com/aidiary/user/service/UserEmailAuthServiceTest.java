package com.aidiary.user.service;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.UserException;
import com.aidiary.core.entity.UserEmailAuthsEntity;
import com.aidiary.core.service.UserDatabaseReadService;
import com.aidiary.core.service.UserDatabaseWriteService;
import com.aidiary.infrastructure.transport.GoogleMailSender;
import com.aidiary.user.model.UserRequestBundle.UserEmailAndAuthCode;
import com.aidiary.user.model.UserRequestBundle.UserEmailAuthCodeSentRequest;
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
class UserEmailAuthServiceTest {

    @Mock
    private UserDatabaseReadService userDatabaseReadService;

    @Mock
    private UserDatabaseWriteService userDatabaseWriteService;

    @Mock
    private GoogleMailSender googleMailSender;

    @InjectMocks
    private UserEmailAuthService userEmailAuthService;

    @Test
    void 회원가입_인증코드_이메일_발송_성공() {

        // given
        UserEmailAuthCodeSentRequest request = new UserEmailAuthCodeSentRequest("register", "test@test.com");

        when(userDatabaseReadService.isUserExistsByEmail("test@test.com")).thenReturn(false);
        when(userDatabaseWriteService.save(any(UserEmailAuthsEntity.class))).thenReturn(
                UserEmailAuthsEntity.builder()
                        .email("test@test.com")
                        .code(anyString())
                        .createdAt(LocalDateTime.now())
                        .build()
        );
        when(googleMailSender.generateEmailVerificationTemplate(anyString(), anyString(), anyString())).thenReturn("templateString");

        // when
        userEmailAuthService.createRandomCodeAndSendEmail(request);

        // then
        verify(userDatabaseReadService, times(1)).isUserExistsByEmail(eq("test@test.com"));
        verify(userDatabaseWriteService, times(1)).save(any(UserEmailAuthsEntity.class));
        verify(googleMailSender, times(1)).generateEmailVerificationTemplate(anyString(), anyString(), anyString());
        verify(googleMailSender, times(1)).sendMail(eq("test@test.com"), anyString(), any(), eq(true));
    }

    @Test
    void 회원가입_인증코드_이메일_발송_실패__이미_존재하는_이메일() {

        // given
        UserEmailAuthCodeSentRequest request = new UserEmailAuthCodeSentRequest("register", "test@test.com");

        when(userDatabaseReadService.isUserExistsByEmail("test@test.com")).thenReturn(true);

        // when
        UserException exception = assertThrows(UserException.class, () -> userEmailAuthService.createRandomCodeAndSendEmail(request));

        // then
        verify(userDatabaseReadService, times(1)).isUserExistsByEmail(eq("test@test.com"));
        assertEquals(ErrorCode.USER_ALREADY_REGISTERED, exception.getErrorCode());

    }

    @Test
    void 비밀번호변경_인증코드_이메일_발송_성공() {

        // given
        UserEmailAuthCodeSentRequest request = new UserEmailAuthCodeSentRequest("password_modification", "test@test.com");

        when(userDatabaseReadService.isUserExistsByEmail("test@test.com")).thenReturn(true);
        when(userDatabaseReadService.findUserEmailAuthByEmail("test@test.com")).thenReturn(
                Optional.of(
                        UserEmailAuthsEntity.builder()
                                .email("test@test.com")
                                .code("REGISTER123!@#")
                                .createdAt(LocalDateTime.now().minusMinutes(30))
                                .build()
                )
        );
        Mockito.lenient().doNothing().when(userDatabaseWriteService).updateUserEmailAuthCodeAndResetConfirmedDate(any(UserEmailAuthsEntity.class), anyString());
        when(googleMailSender.generateEmailVerificationTemplate(anyString(), anyString(), anyString())).thenReturn("templateString");

        // when
        userEmailAuthService.createRandomCodeAndSendEmail(request);

        // then
        verify(userDatabaseReadService, times(1)).isUserExistsByEmail(eq("test@test.com"));
        verify(userDatabaseWriteService, times(1)).updateUserEmailAuthCodeAndResetConfirmedDate(any(UserEmailAuthsEntity.class), anyString());
        verify(googleMailSender, times(1)).generateEmailVerificationTemplate(anyString(), anyString(), anyString());
        verify(googleMailSender, times(1)).sendMail(eq("test@test.com"), anyString(), any(), eq(true));
    }

    @Test
    void 비밀번호변경_인증코드_이메일_발송_실패__해당email의_회원_없음() {

        // given
        UserEmailAuthCodeSentRequest request = new UserEmailAuthCodeSentRequest("password_modification", "test@test.com");

        when(userDatabaseReadService.isUserExistsByEmail("test@test.com")).thenReturn(false);

        // when
        UserException exception = assertThrows(UserException.class, () -> userEmailAuthService.createRandomCodeAndSendEmail(request));

        // then
        verify(userDatabaseReadService, times(1)).isUserExistsByEmail(eq("test@test.com"));
        assertEquals(ErrorCode.USER_NOT_EXIST, exception.getErrorCode());

    }

    @Test
    void 인증코드_컨펌_성공() {

        // given
        UserEmailAndAuthCode request = new UserEmailAndAuthCode("test@test.com", "CODE123!@#");
        UserEmailAuthsEntity userEmailAuthsEntity = UserEmailAuthsEntity.builder()
                .email("test@test.com")
                .code("CODE123!@#")
                .createdAt(LocalDateTime.now().minusMinutes(1)) // expire in 4 minutes
                .build();

        when(userDatabaseReadService.findUserEmailAuthByEmail(request.email())).thenReturn(Optional.of(userEmailAuthsEntity));
        Mockito.lenient().doNothing().when(userDatabaseWriteService).confirmUserEmailAuth(userEmailAuthsEntity);

        // when
        userEmailAuthService.confirmEmailAuthCode(request);

        // then
        verify(userDatabaseReadService, times(1)).findUserEmailAuthByEmail(eq(request.email()));
        verify(userDatabaseWriteService, times(1)).confirmUserEmailAuth(eq(userEmailAuthsEntity));
    }

    @Test
    void 인증코드_컨펌_실패__인증코드_없음() {

        // given
        UserEmailAndAuthCode request = new UserEmailAndAuthCode("test@test.com", "CODE123!@#");

        when(userDatabaseReadService.findUserEmailAuthByEmail(request.email())).thenReturn(Optional.empty());

        // when
        UserException exception = assertThrows(UserException.class, () -> userEmailAuthService.confirmEmailAuthCode(request));

        // then
        verify(userDatabaseReadService, times(1)).findUserEmailAuthByEmail(eq(request.email()));
        verify(userDatabaseWriteService, never()).confirmUserEmailAuth(any(UserEmailAuthsEntity.class));
        assertEquals(ErrorCode.EMAIL_AUTH_FAIL, exception.getErrorCode());

    }

    @Test
    void 인증코드_컨펌_실패__인증코드_만료() {

        // given
        UserEmailAndAuthCode request = new UserEmailAndAuthCode("test@test.com", "CODE123!@#");
        UserEmailAuthsEntity userEmailAuthsEntity = UserEmailAuthsEntity.builder()
                .email("test@test.com")
                .code("CODE123!@#")
                .createdAt(LocalDateTime.now().minusMinutes(35)) // expire 30 minutes ago
                .build();

        when(userDatabaseReadService.findUserEmailAuthByEmail(request.email())).thenReturn(Optional.of(userEmailAuthsEntity));

        // when
        UserException exception = assertThrows(UserException.class, () -> userEmailAuthService.confirmEmailAuthCode(request));

        // then
        verify(userDatabaseReadService, times(1)).findUserEmailAuthByEmail(eq(request.email()));
        verify(userDatabaseWriteService, never()).confirmUserEmailAuth(any(UserEmailAuthsEntity.class));
        assertEquals(ErrorCode.AUTH_CODE_EXPIRED, exception.getErrorCode());

    }

    @Test
    void 인증코드_컨펌_실패__인증코드_불일치() {

        // given
        UserEmailAndAuthCode request = new UserEmailAndAuthCode("test@test.com", "ANOTHER123!@#");
        UserEmailAuthsEntity userEmailAuthsEntity = UserEmailAuthsEntity.builder()
                .email("test@test.com")
                .code("CODE123!@#")
                .createdAt(LocalDateTime.now().minusMinutes(1)) // expire in 4 minutes
                .build();

        when(userDatabaseReadService.findUserEmailAuthByEmail(request.email())).thenReturn(Optional.of(userEmailAuthsEntity));

        // when
        UserException exception = assertThrows(UserException.class, () -> userEmailAuthService.confirmEmailAuthCode(request));

        // then
        verify(userDatabaseReadService, times(1)).findUserEmailAuthByEmail(eq(request.email()));
        verify(userDatabaseWriteService, never()).confirmUserEmailAuth(any(UserEmailAuthsEntity.class));
        assertEquals(ErrorCode.EMAIL_AUTH_FAIL, exception.getErrorCode());

    }
}
