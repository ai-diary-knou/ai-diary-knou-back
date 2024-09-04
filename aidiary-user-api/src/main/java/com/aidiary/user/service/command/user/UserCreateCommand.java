package com.aidiary.user.service.command.user;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.enums.UserStatus;
import com.aidiary.common.exception.UserException;
import com.aidiary.common.utils.SHA256Util;
import com.aidiary.core.entity.UsersEntity;
import com.aidiary.core.service.UserDatabaseWriteService;
import com.aidiary.user.service.command.UserCommand;
import com.aidiary.user.service.command.UserCommandContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserCreateCommand implements UserCommand {

    private final UserDatabaseWriteService userDatabaseWriteService;

    @Override
    public void execute(UserCommandContext context) {
        try {
            userDatabaseWriteService.save(
                    UsersEntity.builder()
                            .email(context.getEmail())
                            .nickname(context.getNickname())
                            .password(SHA256Util.getHashString(context.getPassword()))
                            .status(UserStatus.ACTIVE)
                            .loginAttemptCnt(0)
                            .build()
            );
        } catch (NoSuchAlgorithmException e) {
            throw new UserException(ErrorCode.USER_REGISTER_FAIL);
        }
    }
}
