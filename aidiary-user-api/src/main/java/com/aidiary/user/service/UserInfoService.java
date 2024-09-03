package com.aidiary.user.service;

import com.aidiary.common.enums.DuplicateUserValidateType;
import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.UserException;
import com.aidiary.core.entity.UserEmailAuthsEntity;
import com.aidiary.core.entity.UsersEntity;
import com.aidiary.core.service.UserDatabaseReadService;
import com.aidiary.core.service.UserDatabaseWriteService;
import com.aidiary.user.model.UserRequestBundle.DuplicateUserValidateRequest;
import com.aidiary.user.model.UserRequestBundle.UserNicknameUpdateRequest;
import com.aidiary.user.model.UserRequestBundle.UserPasswordUpdateRequest;
import com.aidiary.user.service.command.*;
import com.aidiary.user.service.command.validation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserInfoService {

    private final UserDatabaseReadService userDatabaseReadService;
    private final UserDatabaseWriteService userDatabaseWriteService;

    public void validateUserDuplication(DuplicateUserValidateRequest request) {

        UserCommandGroup userCommandGroup = new UserCommandGroup();
        UserCommandContext userCommandContext = new UserCommandContext();

        switch (DuplicateUserValidateType.valueOf(request.type().toUpperCase(Locale.ROOT))) {
            case EMAIL -> {
                userCommandGroup.add(new ValidateEmailExistCommand(userDatabaseReadService));
                userCommandContext.setDuplicateUserValidateType(DuplicateUserValidateType.EMAIL);
                userCommandContext.setEmail(request.value());
            }
            case NICKNAME -> {
                userCommandGroup.add(new ValidateNicknameExistCommand(userDatabaseReadService));
                userCommandContext.setDuplicateUserValidateType(DuplicateUserValidateType.NICKNAME);
                userCommandContext.setNickname(request.value());
            }
        }

        userCommandGroup.execute(userCommandContext);

    }

    public void updatePassword(UserPasswordUpdateRequest request) {

        UsersEntity user = userDatabaseReadService.findUserByEmail(request.email())
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_EXIST));

        UserEmailAuthsEntity userEmailAuth = userDatabaseReadService.findUserEmailAuthByEmail(request.email())
                .orElseThrow(() -> new UserException(ErrorCode.EMAIL_NOT_AUTHORIZED));

        UserCommandGroup userCommandGroup = new UserCommandGroup();
        userCommandGroup.add(new ValidateUserSignedOutOrLockedCommand());
        userCommandGroup.add(new ValidateEmailAuthCodeConfirmedCommand(userDatabaseReadService));
        userCommandGroup.add(new ValidateRepasswordMatchCommand());
        userCommandGroup.add(new UpdateUserPasswordCommand(userDatabaseWriteService));
        userCommandGroup.add(new ResetLoginAttemptCommand(userDatabaseWriteService));
        userCommandGroup.add(new UnlockUserCommand(userDatabaseWriteService));

        UserCommandContext userCommandContext = UserCommandContext.builder()
                .user(user)
                .userEmailAuth(userEmailAuth)
                .email(request.email())
                .code(request.code())
                .password(request.password())
                .rePassword(request.rePassword())
                .build();

        userCommandGroup.execute(userCommandContext);

    }

    public void updateNickname(Long userId, UserNicknameUpdateRequest request) {

        UsersEntity user = userDatabaseReadService.findUserById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_EXIST));

        UserCommandGroup userCommandGroup = new UserCommandGroup();
        userCommandGroup.add(new ValidateUserSignedOutOrLockedCommand());
        userCommandGroup.add(new ValidateNicknameExistCommand(userDatabaseReadService));
        userCommandGroup.add(new UpdateNicknameCommand(userDatabaseWriteService));

        UserCommandContext userCommandContext = UserCommandContext.builder()
                .user(user)
                .nickname(request.nickname())
                .build();

        userCommandGroup.execute(userCommandContext);

    }

}
