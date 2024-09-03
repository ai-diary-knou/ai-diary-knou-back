package com.aidiary.user.service;

import com.aidiary.common.enums.UserStatus;
import com.aidiary.common.utils.SHA256Util;
import com.aidiary.core.entity.UsersEntity;
import com.aidiary.core.service.UserDatabaseReadService;
import com.aidiary.core.service.UserDatabaseWriteService;
import com.aidiary.user.model.UserRequestBundle.*;
import com.aidiary.user.service.command.UserCommandContext;
import com.aidiary.user.service.command.UserCommandGroup;
import com.aidiary.user.service.command.validation.ValidateEmailAuthCodeConfirmedCommand;
import com.aidiary.user.service.command.validation.ValidateEmailExistCommand;
import com.aidiary.user.service.command.validation.ValidateRepasswordMatchCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRegisterService {

    private final UserDatabaseReadService userDatabaseReadService;
    private final UserDatabaseWriteService userDatabaseWriteService;

    public void register(UserRegisterRequest request) throws NoSuchAlgorithmException {

        UserCommandGroup userCommandGroup = new UserCommandGroup();
        userCommandGroup.add(new ValidateEmailExistCommand(userDatabaseReadService));
        userCommandGroup.add(new ValidateRepasswordMatchCommand());
        userCommandGroup.add(new ValidateEmailAuthCodeConfirmedCommand(userDatabaseReadService));

        UserCommandContext userCommandContext = UserCommandContext.builder()
                .email(request.email())
                .nickname(request.nickname())
                .password(request.password())
                .rePassword(request.rePassword())
                .build();

        userCommandGroup.execute(userCommandContext);

        userDatabaseWriteService.save(
                UsersEntity.builder()
                        .email(request.email())
                        .nickname(request.nickname())
                        .password(SHA256Util.getHashString(request.password()))
                        .status(UserStatus.ACTIVE)
                        .loginAttemptCnt(0)
                        .build()
        );

    }

}
