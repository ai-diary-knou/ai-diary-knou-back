package com.aidiary.user.service;

import com.aidiary.common.enums.DuplicateUserValidateType;
import com.aidiary.core.service.UserDatabaseReadService;
import com.aidiary.core.service.UserDatabaseWriteService;
import com.aidiary.user.model.UserRequestBundle.DuplicateUserValidateRequest;
import com.aidiary.user.model.UserRequestBundle.UserNicknameUpdateRequest;
import com.aidiary.user.model.UserRequestBundle.UserPasswordUpdateRequest;
import com.aidiary.user.service.command.UserCommandContext;
import com.aidiary.user.service.command.UserCommandGroup;
import com.aidiary.user.service.command.emailAuth.EmailAuthSearchByEmailCommand;
import com.aidiary.user.service.command.user.*;
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
            case EMAIL -> validateEmailDuplication(request, userCommandGroup, userCommandContext);
            case NICKNAME -> validateNicknameDuplication(request, userCommandGroup, userCommandContext);
        }

        userCommandGroup.execute(userCommandContext);

    }

    private void validateNicknameDuplication(DuplicateUserValidateRequest request, UserCommandGroup userCommandGroup, UserCommandContext userCommandContext) {
        userCommandGroup.add(new ValidateUserNicknameExistCommand(userDatabaseReadService));
        userCommandContext.setDuplicateUserValidateType(DuplicateUserValidateType.NICKNAME);
        userCommandContext.setNickname(request.value());
    }

    private void validateEmailDuplication(DuplicateUserValidateRequest request, UserCommandGroup userCommandGroup, UserCommandContext userCommandContext) {
        userCommandGroup.add(new ValidateUserExistByEmailCommand(userDatabaseReadService));
        userCommandContext.setDuplicateUserValidateType(DuplicateUserValidateType.EMAIL);
        userCommandContext.setEmail(request.value());
    }

    public void updatePassword(UserPasswordUpdateRequest request) {

        UserCommandGroup userCommandGroup = new UserCommandGroup();
        userCommandGroup.add(new UserSearchByEmailCommand(userDatabaseReadService));
        userCommandGroup.add(new EmailAuthSearchByEmailCommand(userDatabaseReadService));
        userCommandGroup.add(new ValidateUserSignedOutOrLockedCommand());
        userCommandGroup.add(new ValidateEmailAuthCodeConfirmedCommand(userDatabaseReadService));
        userCommandGroup.add(new ValidateRepasswordMatchCommand());
        userCommandGroup.add(new UserPasswordUpdateCommand(userDatabaseWriteService));
        userCommandGroup.add(new UserLoginAttemptResetCommand(userDatabaseWriteService));
        userCommandGroup.add(new UserStatusUnlockCommand(userDatabaseWriteService));

        UserCommandContext userCommandContext = UserCommandContext.builder()
                .email(request.email())
                .code(request.code())
                .password(request.password())
                .rePassword(request.rePassword())
                .build();

        userCommandGroup.execute(userCommandContext);

    }

    public void updateNickname(Long userId, UserNicknameUpdateRequest request) {

        UserCommandGroup userCommandGroup = new UserCommandGroup();
        userCommandGroup.add(new UserSearchByIdCommand(userDatabaseReadService));
        userCommandGroup.add(new ValidateUserSignedOutOrLockedCommand());
        userCommandGroup.add(new ValidateUserNicknameExistCommand(userDatabaseReadService));
        userCommandGroup.add(new UserNicknameUpdateCommand(userDatabaseWriteService));

        UserCommandContext userCommandContext = UserCommandContext.builder()
                .userId(userId)
                .nickname(request.nickname())
                .build();

        userCommandGroup.execute(userCommandContext);

    }

}
