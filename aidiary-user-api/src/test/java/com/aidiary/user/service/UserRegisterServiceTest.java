package com.aidiary.user.service;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.UserException;
import com.aidiary.core.entity.UsersEntity;
import com.aidiary.core.service.UserDatabaseReadService;
import com.aidiary.core.service.UserDatabaseWriteService;
import com.aidiary.user.model.UserRequestBundle.UserRegisterRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.security.NoSuchAlgorithmException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRegisterServiceTest {

    @Mock
    private UserDatabaseReadService userDatabaseReadService;

    @Mock
    private UserDatabaseWriteService userDatabaseWriteService;

    @InjectMocks
    private UserRegisterService userRegisterService;

    @Test
    void 회원가입_성공() throws NoSuchAlgorithmException {

        // given
        UserRegisterRequest request = new UserRegisterRequest("test@test.com", "testNick", "password", "password");

        when(userDatabaseReadService.isUserExistsByEmail(request.email())).thenReturn(false);
        when(userDatabaseReadService.isUserEmailAuthConfirmedByEmail(request.email())).thenReturn(true);

        // when
        userRegisterService.register(request);

        // then
        verify(userDatabaseReadService, times(1)).isUserExistsByEmail(request.email());
        verify(userDatabaseReadService, times(1)).isUserEmailAuthConfirmedByEmail(request.email());
        verify(userDatabaseWriteService, times(1)).save(any(UsersEntity.class));
    }

    @Test
    void 회원가입_실패__해당email의_회원_존재() {

        // given
        UserRegisterRequest request = new UserRegisterRequest("test@test.com", "testNick", "password", "password");

        when(userDatabaseReadService.isUserExistsByEmail(request.email())).thenReturn(true);

        // when & then
        UserException exception = assertThrows(UserException.class, () -> userRegisterService.register(request));

        verify(userDatabaseReadService, times(1)).isUserExistsByEmail(request.email());
        verify(userDatabaseReadService, never()).isUserEmailAuthConfirmedByEmail(anyString());
        verify(userDatabaseWriteService, never()).save(any(UsersEntity.class));
        assertEquals(ErrorCode.USER_ALREADY_REGISTERED, exception.getErrorCode());

    }

    @Test
    void 회원가입_실패__재입력_비밀번호_불일치() {

        // given
        UserRegisterRequest request = new UserRegisterRequest("test@test.com", "testNick", "password", "differentPassword");

        when(userDatabaseReadService.isUserExistsByEmail(request.email())).thenReturn(false);

        // when & then
        UserException exception = assertThrows(UserException.class, () -> userRegisterService.register(request));

        verify(userDatabaseReadService, times(1)).isUserExistsByEmail(request.email());
        verify(userDatabaseReadService, never()).isUserEmailAuthConfirmedByEmail(anyString());
        verify(userDatabaseWriteService, never()).save(any(UsersEntity.class));
        assertEquals(ErrorCode.INVALID_PARAMETER, exception.getErrorCode());

    }

    @Test
    void 회원가입_실패__인증_컨펌_안됨() {

        // given
        UserRegisterRequest request = new UserRegisterRequest("test@test.com", "testNick", "password", "password");

        when(userDatabaseReadService.isUserExistsByEmail(request.email())).thenReturn(false);
        when(userDatabaseReadService.isUserEmailAuthConfirmedByEmail(request.email())).thenReturn(false);

        // when & then
        UserException exception = assertThrows(UserException.class, () -> userRegisterService.register(request));

        verify(userDatabaseReadService, times(1)).isUserExistsByEmail(request.email());
        verify(userDatabaseReadService, times(1)).isUserEmailAuthConfirmedByEmail(request.email());
        verify(userDatabaseWriteService, never()).save(any(UsersEntity.class));
        assertEquals(ErrorCode.EMAIL_NOT_AUTHORIZED, exception.getErrorCode());

    }
}
