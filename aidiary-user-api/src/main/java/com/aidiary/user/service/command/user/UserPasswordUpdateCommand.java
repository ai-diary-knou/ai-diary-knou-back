package com.aidiary.user.service.command.user;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.UserException;
import com.aidiary.common.utils.SHA256Util;
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
public class UserPasswordUpdateCommand implements UserCommand {

    private final UserDatabaseWriteService userDatabaseWriteService;

    @Override
    public void execute(UserCommandContext context) {

        try {

            userDatabaseWriteService.updateUserPassword(context.getUser(), SHA256Util.getHashString(context.getPassword()));

        } catch (NoSuchAlgorithmException e) {
            log.info("User Password Update Fail");
            throw new UserException(ErrorCode.UNKNOWN_ERROR);
        }

    }

}
